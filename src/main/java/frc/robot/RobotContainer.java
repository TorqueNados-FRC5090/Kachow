package frc.robot;

import static frc.robot.Constants.ControllerPorts.*;
import static frc.robot.Constants.DriveConstants.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.Constants.ClimberConstants.ClimberPosition;
import frc.robot.commands.AutonContainer;
import frc.robot.commands.Shoot;
import frc.robot.subsystems.Shooter;
import frc.robot.commands.ClimberUp;
import frc.robot.subsystems.CTRESwerveDrivetrain;
import frc.robot.subsystems.Climber;

public class RobotContainer {
    // CTRE drivetrain control functions
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(TRANSLATION_DEADBAND).withRotationalDeadband(ROTATION_DEADBAND)
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage);
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    // Controllers
    private final CommandXboxController driverController = new CommandXboxController(DRIVER_PORT);
    private final CommandXboxController operatorController = new CommandXboxController(OPERATOR_PORT);
    // Subsystems
    public final CTRESwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
    public final Shooter shoot = new Shooter(10, 11);
    private final Climber climber = new Climber(12, .27);

    // Misc objects
    private final AutonContainer auton = new AutonContainer(this);
    private final SendableChooser<Command> autonChooser = auton.buildAutonChooser();
    private final Telemetry logger = new Telemetry(MAX_TRANSLATION_SPEED);

    /** Constructs a RobotContainer */
    public RobotContainer() {
        SmartDashboard.putData("Auton Selector", autonChooser);

        setDriverControls();
        setOperatorControls();
    }

    /** @return Whether the robot is on the red alliance or not */
    public boolean onRedAlliance() { 
        return DriverStation.getAlliance().get().equals(DriverStation.Alliance.Red);
    }

    /** Configures a set of control bindings for the robot's driver */
    private void setDriverControls() {
        // Note that X is defined as forward according to WPILib convention,
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
            drivetrain.applyRequest(() ->
                drive.withVelocityX(-driverController.getLeftY() * MAX_TRANSLATION_SPEED) 
                    .withVelocityY(-driverController.getLeftX() * MAX_TRANSLATION_SPEED)
                    .withRotationalRate(-driverController.getRightX() * MAX_ROTATION_SPEED) 
            )
        );

        driverController.a().whileTrue(drivetrain.applyRequest(() -> brake));
        driverController.b().whileTrue(drivetrain.applyRequest(() ->
            point.withModuleDirection(new Rotation2d(-driverController.getLeftY(), -driverController.getLeftX()))
        ));

        // Run SysId routines when holding back/start and X/Y.
        // Note that each routine should be run exactly once in a single log.
        driverController.back().and(driverController.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        driverController.back().and(driverController.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        driverController.start().and(driverController.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        driverController.start().and(driverController.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        // reset the field-centric heading on left bumper press
        driverController.leftBumper().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

        drivetrain.registerTelemetry(logger::telemeterize);
    }

    /** Configures a set of control bindings for the robot's operator */
    private void setOperatorControls() {
        // Runs the auton command as an example binding
        operatorController.rightTrigger().whileTrue(new Shoot(shoot, .45));
        operatorController.b().whileTrue(new ClimberUp(climber, ClimberPosition.zero));
        operatorController.x().whileTrue(new ClimberUp(climber, ClimberPosition.climb));
        operatorController.y().whileTrue(new ClimberUp(climber, ClimberPosition.stow));
    }

    /** Use this to pass the autonomous command to the main {@link Robot} class. */
    public Command getAutonomousCommand() {
        return autonChooser.getSelected();
    }
}
