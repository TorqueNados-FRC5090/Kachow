package frc.robot.subsystems;


import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase{
    SparkMax leadMotor;
    SparkMax followMotor;
    public Shooter(int leadID, int followID){
        leadMotor = new SparkMax(leadID, MotorType.kBrushless);
        followMotor = new SparkMax(followID, MotorType.kBrushless);
       
       // configuration for follower motor
        SparkMaxConfig followConfig = new SparkMaxConfig();
        followConfig.follow(leadID, true);
        
        followMotor.configure(followConfig, ResetMode.kResetSafeParameters , PersistMode.kPersistParameters);

    }

    public void shoot(double speed){
        leadMotor.set(speed);

    }

    public void stop(){
        leadMotor.set(0);
    }

}
