// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

<<<<<<< HEAD
import com.ctre.phoenix6.HootAutoReplay;
import edu.wpi.first.cameraserver.CameraServer;
=======

import com.pathplanner.lib.commands.PathfindingCommand;

>>>>>>> 2487096b0539ccd43981432a30c4e28e61e0afb6
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.wrappers.Limelighthelpers;


public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private final RobotContainer m_robotContainer;

  private final boolean kUseLimelight = true;
<<<<<<< HEAD

    /* log and replay timestamp and joystick data */
    private final HootAutoReplay m_timeAndJoystickReplay = new HootAutoReplay()
        .withTimestampReplay()
        .withJoystickReplay();
=======
>>>>>>> 2487096b0539ccd43981432a30c4e28e61e0afb6

  public Robot() {
    m_robotContainer = new RobotContainer();
  }

  @Override
    public void robotInit() 
    {
        // Make sure you only configure port forwarding once in your robot code.
        // Do not place these function calls in any periodic functions
        // for (int port = 5800;  port <= 5809; port++) {
        //     PortForwarder.add(port, "limelight.local", port);
        //     PortForwarder.add(port+10, "limelight1.local", port);
        // }
        PathfindingCommand.warmupCommand().schedule();
    }

<<<<<<< HEAD
    @Override
    public void robotPeriodic() {
        m_timeAndJoystickReplay.update();
        CommandScheduler.getInstance().run(); 



            /*
=======
  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();

    /*
>>>>>>> 2487096b0539ccd43981432a30c4e28e61e0afb6
     * This example of adding Limelight is very simple and may not be sufficient for on-field use.
     * Users typically need to provide a standard deviation that scales with the distance to target
     * and changes with number of tags available.
     *
     * This example is sufficient to show that vision integration is possible, though exact implementation
     * of how to use vision should be tuned per-robot and to the team's specification.
     */
    if (kUseLimelight) {
      var driveState = m_robotContainer.drivetrain.getState();
      
      double omegaRps = Units.radiansToRotations(driveState.Speeds.omegaRadiansPerSecond);
      
     
  
      Limelighthelpers.SetRobotOrientation("limelight",m_robotContainer.drivetrain.getgyroyaw().getDegrees(), 0, 0, 0, 0, 0);
      var llMeasurement = Limelighthelpers.getBotPoseEstimate_wpiBlue("limelight");
      if (llMeasurement != null && llMeasurement.tagCount > 0 && Math.abs(omegaRps) < 2.0) {
        m_robotContainer.drivetrain.addVisionMeasurement(llMeasurement.pose, llMeasurement.timestampSeconds);
<<<<<<< HEAD
    }
}
        
=======
        
      }
      
      SmartDashboard.putNumber("111 drive pose X", m_robotContainer.drivetrain.getState().Pose.getX());
      SmartDashboard.putNumber("111 drive pose Y",  m_robotContainer.drivetrain.getState().Pose.getY());
    } 
      
  }
    

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
>>>>>>> 2487096b0539ccd43981432a30c4e28e61e0afb6
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}

<<<<<<< HEAD
    @Override
    public void testInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void testPeriodic() {}

    @Override
    public void testExit() {}

    @Override
    public void simulationPeriodic() {}
=======
  @Override
  public void simulationPeriodic() {}
>>>>>>> 2487096b0539ccd43981432a30c4e28e61e0afb6
}