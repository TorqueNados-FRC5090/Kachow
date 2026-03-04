package frc.robot.subsystems;

import java.util.Optional;
import java.util.function.Supplier;

// CTRE Phoenix 6 Imports
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

    // --- PHYSICAL TURRET OFFSET (MEASURE THESE ON YOUR ROBOT!) ---
    // Negative X means BACKWARDS from the center of the robot.
    private final double kTurretOffsetXInches = -10.5; 
    
    // Negative Y means RIGHT from the center of the robot.
    private final double kTurretOffsetYInches = -8.0;  

    // We create the offset vector once here so we don't recreate it every single loop
    private final Translation2d m_robotRelativeTurretOffset = new Translation2d(
        Units.inchesToMeters(kTurretOffsetXInches), 
        Units.inchesToMeters(kTurretOffsetYInches)
    );

    // --- Mechanical Constants ---
    private final double kTurretRingTeeth = 200.0; 
    private final double kEncoderGearTeeth = 16.0; // 16t Minion Pinion
    private final double kTurretGearRatio = kTurretRingTeeth / kEncoderGearTeeth; // Exactly 12.5

    // Physical limit so the turret never exceeds a 360-degree sweep (-180 to +180)
    private final double kMaxTurretRotations = 0.48; 

    // --- STATE VARIABLES ---
    // This will hold the live distance so the dashboard and the shooter can see it
    private double m_distanceToHubMeters = 0.0;

    /**
     * Turret Subsystem Constructor
     */
    public Turret(Supplier<Pose2d> poseSupplier, AprilTagFieldLayout atLayout) {
        this.m_robotPoseSupplier = poseSupplier;
        this.m_atLayout = atLayout;

        // Initialize Talon FXS with CAN ID 8
        m_turretMotor = new TalonFXS(8);
        m_motionMagic = new MotionMagicVoltage(0);

        // --- Configure the Talon FXS ---
        TalonFXSConfiguration config = new TalonFXSConfiguration();
        
        // PID Tuning for the fast Minion
        config.Slot0.kP = 4.0; 
        config.Slot0.kI = 0.0;
        config.Slot0.kD = 0.1;
        config.Slot0.kV = 0.12; 
        
        // Motion Magic Profile (Motor Rotations per second)
        config.MotionMagic.MotionMagicCruiseVelocity = 20.0; 
        config.MotionMagic.MotionMagicAcceleration = 40.0;   
        config.MotionMagic.MotionMagicJerk = 400.0;          

        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        m_turretMotor.getConfigurator().apply(config);

        // Reset the encoder. Turret MUST be perfectly centered facing backward on boot!
        m_turretMotor.setPosition(0.0);
    }

    public void zeroTurret() {
        m_turretMotor.setPosition(0.0);
    }

    /**
     * @return The exact straight-line distance from the physical Turret to the Hub in meters.
     */
    public double getDistanceToHubMeters() {
        return m_distanceToHubMeters;
    }

    /**
     * Automatically Aligns the Turret to the Hub and calculates pinpoint distance.
     */
    public void alignToHub() {
        // 1. Get the target AprilTag based on Alliance
        boolean isRed = isRedAlliance();
        int targetTag = isRed ? 10 : 26;
        Optional<Pose3d> tagPoseOpt = m_atLayout.getTagPose(targetTag);
        
        if (tagPoseOpt.isEmpty()) {
            return; // Exit early if tag isn't found
        }
        
        Pose2d hubPose = tagPoseOpt.get().toPose2d();
        Translation2d hubTranslation;
        
        // 2. Adjust target position to the exact center of the Hub
        if (!isRed) {
            // Blue alliance: Shift -23.75 inches in X
            hubTranslation = hubPose.getTranslation().minus(new Translation2d(Units.inchesToMeters(23.75), 0));
        } else {
            // Red alliance: Shift +23.75 inches in X
            hubTranslation = hubPose.getTranslation().plus(new Translation2d(Units.inchesToMeters(23.75), 0));
        }
            
        // Get the latest robot pose from the Swerve Drivetrain
        Pose2d robotPose = m_robotPoseSupplier.get(); 
        
        // 3. --- APPLY THE BACK-RIGHT OFFSET ---
        // Rotate the offset by the robot's heading to find where the turret is physically swinging in the real world
        Translation2d globalTurretPos = robotPose.getTranslation().plus(m_robotRelativeTurretOffset.rotateBy(robotPose.getRotation()));

        // 4. --- CALCULATE VECTOR & EXACT DISTANCE ---
        // Create a 2D line pointing exactly from the physical Turret to the Hub
        Translation2d turretToHub = hubTranslation.minus(globalTurretPos);

        // .getNorm() gets the exact length (magnitude) of that line!
        m_distanceToHubMeters = turretToHub.getNorm();

        // Push the pinpoint distance to the SmartDashboard!
        SmartDashboard.putNumber("Turret/Distance_To_Hub_Meters", m_distanceToHubMeters);
        // We also push it in inches so it's easier for you and the drivers to verify with a tape measure
        SmartDashboard.putNumber("Turret/Distance_To_Hub_Inches", Units.metersToInches(m_distanceToHubMeters));

        // 5. --- CALCULATE AIMING ANGLE ---
        // Desired turret angle = (Angle to target) - (Robot Heading) - (180 deg for backward mount)
        Rotation2d turretSetpoint = turretToHub.getAngle()
            .minus(robotPose.getRotation())
            .minus(Rotation2d.fromDegrees(180)); 

        // Convert target angle to physical turret rotations
        double desiredTurretRotations = turretSetpoint.getRadians() / (2 * Math.PI);

        // Clamp to prevent the mechanism from breaking the wires
        desiredTurretRotations = MathUtil.clamp(desiredTurretRotations, -kMaxTurretRotations, kMaxTurretRotations);

        // 6. --- COMMAND THE MOTOR ---
        // Convert the physical turret position to Minion MOTOR rotations
        double motorRotations = desiredTurretRotations * kTurretGearRatio;
        
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
        // Constantly report where the turret actually is to the dashboard
        double currentMotorRotations = m_turretMotor.getPosition().getValueAsDouble();
        double currentTurretRotations = currentMotorRotations / kTurretGearRatio;
        
        SmartDashboard.putNumber("Turret/Current_Motor_Rots", currentMotorRotations);
        SmartDashboard.putNumber("Turret/Current_Turret_Rots", currentTurretRotations);
    }
}