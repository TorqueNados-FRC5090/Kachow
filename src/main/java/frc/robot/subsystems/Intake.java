
    package frc.robot.subsystems;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.sim.TalonFXSimState.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.wrappers.GenericPID;
import frc.robot.wrappers.LimitSwitch;

public class Intake extends SubsystemBase{
    private TalonFX intakeMotor;
    private TalonFX rotationMotor;
    private GenericPID rotationPID;
    private LimitSwitch limitSwitch;

    /** Constructs an Intake
     *  @param intakeID The ID of the intake motor
     *  @param rotateID The ID of the rotate motor
     *  @param limPort  Limelight port
     */
    public Intake(int intakeID, int rotateID, int limPort){
        intakeMotor = new TalonFX(intakeID);        
        rotationMotor = new TalonFX(rotateID);
        Slot0Configs intakePIDConfig = new Slot0Configs();
        intakePIDConfig.kP = 0.4;
        intakePIDConfig.kD = .007;
        limitSwitch = new LimitSwitch(limPort);
        limitSwitch.setInverted(true);
    }
    
    // Go-go Gadget Move (Makes the Intake Move)
    public void yummy(){
        intakeMotor.set(.5);
    }
    
    // Go-go Gadget Rotate (Makes Intake Rotate)
    public void rotate(){
        rotationMotor.set(.25);
    }

    // Go-go Gadget Stop (Stops the Intake)
    public void full(){
        intakeMotor.set(0);
    }
    
    // Go-go Gadget Rotate-No-More
    public void rotatestop(){
        rotationMotor.set(0);
    }

    
    @Override
    public void periodic() {
        SmartDashboard.putNumber("Intake Position Degrees", rotationPID.getMeasurement());
    }
}


