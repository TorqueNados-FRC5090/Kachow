package frc.robot.subsystems;

import java.util.Optional;
import java.util.function.Supplier;

// CTRE Phoenix 6 Imports (Updated for Talon FXS)
import com.ctre.phoenix6.configs.TalonFXSConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFXS;
import com.ctre.phoenix6.signals.NeutralModeValue;

// WPILib Imports
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {

    // --- Hardware & Control ---
    private final TalonFXS m_turretMotor;
    private final MotionMagicVoltage m_motionMagic;

    // --- External Dependencies ---
    private final Supplier<Pose2d> m_robotPoseSupplier;
    private final AprilTagFieldLayout m_atLayout;

    // --- Mechanical Constants ---
    private final double kTurretRingTeeth = 200.0; 
    private final double kEncoderGearTeeth = 16.0; // Updated to 16t Minion Pinion
    private final double kTurretGearRatio = kTurretRingTeeth / kEncoderGearTeeth; // Exactly 12.5

    // Physical limit so the turret never exceeds a 360-degree sweep (-180 to +180)
    // 0.48 leaves a tiny 2% software buffer before hitting the physical hard stops to prevent shattering them.
    private final double kMaxTurretRotations = 0.48; 

    /**
     * Turret Subsystem Constructor
     * 
     * @param poseSupplier A method that returns the Drivetrain's current Pose2d
     * @param atLayout     The field's AprilTag layout
     */
    public Turret(Supplier<Pose2d> poseSupplier, AprilTagFieldLayout atLayout) {
        this.m_robotPoseSupplier = poseSupplier;
        this.m_atLayout = atLayout;

        // Initialize Talon FXS with CAN ID 8
        m_turretMotor = new TalonFXS(8);
        
        // Initialize the Motion Magic Control Request
        m_motionMagic = new MotionMagicVoltage(0);

        // --- Configure the Talon FXS ---
        TalonFXSConfiguration config = new TalonFXSConfiguration();
        
        // PID Tuning (You and Tully will need to tune these for the lightweight Minion!)
        config.Slot0.kP = 4.0; 
        config.Slot0.kI = 0.0;
        config.Slot0.kD = 0.1;
        config.Slot0.kV = 0.12; // Minion motors respond very well to velocity feedforward
        
        // Motion Magic Profile
        // Note: These values are in Motor Rotations per second. Tune them so the turret is fast but stable.
        config.MotionMagic.MotionMagicCruiseVelocity = 20.0; // Max speed
        config.MotionMagic.MotionMagicAcceleration = 40.0;   // Acceleration
        config.MotionMagic.MotionMagicJerk = 400.0;          // Smoothness (S-Curve)

        // Set Neutral Mode to Brake so the turret holds its position securely against inertia
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        // Apply configs to the motor
        m_turretMotor.getConfigurator().apply(config);

        // Tell the encoder it is currently at position 0. 
        // NOTE: Turret MUST be perfectly centered facing backward when the robot boots!
        m_turretMotor.setPosition(0.0);
    }

    /**
     * Resets the motor's internal encoder to 0. 
     * IMPORTANT: The physical turret MUST be facing exactly backwards 
     * before you call this, otherwise all your aiming math will be completely offset!
     */
    public void zeroTurret() {
        m_turretMotor.setPosition(0.0);
    }

    /**
     * Automatically Aligns the Turret to the Hub.
     * Call this inside a Command's execute() loop.
     */
    public void alignToHub() {
        // 1. Safely handle AprilTags to prevent robot crashes
        boolean isRed = isRedAlliance();
        int targetTag = isRed ? 10 : 26;
        Optional<Pose3d> tagPoseOpt = m_atLayout.getTagPose(targetTag);
        
        if (tagPoseOpt.isEmpty()) {
            return; // Exit early if the tag isn't found in the layout JSON
        }
        
        Pose2d hubPose = tagPoseOpt.get().toPose2d();
        Translation2d hubTranslation;
        
        // 2. Adjust target position based on alliance (Fixed mirrored math bug)
        if (!isRed) {
            // Blue alliance: Shift -23.75 inches in X
            hubTranslation = hubPose.getTranslation().minus(new Translation2d(Units.inchesToMeters(23.75), 0));
        } else {
            // Red alliance: Shift +23.75 inches in X
            hubTranslation = hubPose.getTranslation().plus(new Translation2d(Units.inchesToMeters(23.75), 0));
        }
            
        // Get the latest robot pose directly from the drivetrain supplier
        Pose2d robotPose = m_robotPoseSupplier.get(); 
        
        // 3. Rotate the turret offset by the robot's heading to find true field position
        Translation2d robotRelativeTurretOffset = new Translation2d(Units.inchesToMeters(-5), Units.inchesToMeters(5));
        Translation2d globalTurretPos = robotPose.getTranslation().plus(robotRelativeTurretOffset.rotateBy(robotPose.getRotation()));

        // Vector pointing from the Turret to the Hub
        Translation2d turretToHub = hubTranslation.minus(globalTurretPos);

        // Desired turret angle = (Angle to target) - (Robot Heading) - (180 deg for backward mount)
        Rotation2d turretSetpoint = turretToHub.getAngle()
            .minus(robotPose.getRotation())
            .minus(Rotation2d.fromDegrees(180)); 

        // 4. --- 360 DEGREE ROTATION LOGIC ---
        // Dividing by 2*PI gives us physical turret rotations strictly between -0.5 and 0.5.
        double desiredTurretRotations = turretSetpoint.getRadians() / (2 * Math.PI);

        // Clamp at +/- kMaxTurretRotations to guarantee the mechanism never breaks the wires
        desiredTurretRotations = MathUtil.clamp(desiredTurretRotations, -kMaxTurretRotations, kMaxTurretRotations);

        // 5. Convert the physical turret position to Minion MOTOR rotations
        double motorRotations = desiredTurretRotations * kTurretGearRatio;
        
        // Command the Talon FXS
        m_turretMotor.setControl(m_motionMagic.withPosition(motorRotations));
        
        // Telemetry for Debugging
        SmartDashboard.putNumber("Turret/Target_Motor_Rots", motorRotations);
        SmartDashboard.putNumber("Turret/Target_Turret_Rots", desiredTurretRotations);
    }

    /**
     * Helper method to safely get the Alliance color directly from WPILib.
     */
    private boolean isRedAlliance() {
        Optional<DriverStation.Alliance> alliance = DriverStation.getAlliance();
        return alliance.isPresent() && alliance.get() == DriverStation.Alliance.Red;
    }

    @Override
    public void periodic() {
        // Constantly report where the turret actually is to the dashboard for PID tuning
        double currentMotorRotations = m_turretMotor.getPosition().getValueAsDouble();
        double currentTurretRotations = currentMotorRotations / kTurretGearRatio;
        
        SmartDashboard.putNumber("Turret/Current_Motor_Rots", currentMotorRotations);
        SmartDashboard.putNumber("Turret/Current_Turret_Rots", currentTurretRotations);
    }
}