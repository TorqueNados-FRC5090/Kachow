package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ClimberConstants.ClimberPosition;
import frc.robot.wrappers.GenericPID;

public class Climber extends SubsystemBase {
    
    SparkMax climbMotor;
    GenericPID climberPID;
    ClimberPosition pos;
    

    public Climber(int climberID, double p){
       climbMotor = new SparkMax(climberID, MotorType.kBrushless);
       climberPID = new GenericPID(climbMotor, ControlType.kPosition, p);
    }
   // moves climber to any position
    public void moveClimber(double speed){
        climbMotor.set(speed);
    }

    // moves climber to setpoints
    public void climbTo(ClimberPosition pos){
        climberPID.activate(pos.getAngle());
    }

    public void stop(){
        climbMotor.set(0);
    }

    @Override
    public void periodic(){
       SmartDashboard.putNumber(getName(), climberPID.getMeasurement()); 
    }
}
