package frc.robot.subsystems;

import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXSConfiguration;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.hardware.TalonFXS;
import com.ctre.phoenix6.signals.MotorArrangementValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class turret extends SubsystemBase {
    TalonFXS turretMotor;
    double setpoint;
    double buffer = .5;
    // very big state machine i don't comprehend. but that's okay!
    StateMachine state = StateMachine.regular;

    public mtr() {
        turretMotor = new TalonFXS(8);
        int off = 1600; // i don't know what jerk does.
        int velocityValue = 1000;
        int accelerationValue = 60;

        TalonFXSConfiguration turretConfig = new TalonFXSConfiguration();
            turretMotor.MotorOutput.NeutralMode = NeutralModeValue.Brake;
            turretMotor.Commutation.MotorArrangement = MotorArrangementValue.Minion_JST;
        turretMotor.getConfigurator().apply(turretConfig);

        MotionMagicConfigs turretMotionMagic = new TalonFXSConfiguration().MotionMagic;
            samsmotionmagic.MotionMagicCruiseVelocity = velocityValue;
            samsmotionmagic.MotionMagicAcceleration = accelerationValue;
            samsmotionmagic.MotionMagicJerk = off;
        turretMotor.getConfigurator().apply (turretMotionMagic);

        Slot0Configs turretPID = new Slot0Configs();
            // kG, kS, kV, and kA all go unused, we dont have gravity or static
            turretPID.kP = .6; // output per unit of error in position (output/rotation)
            turretPID.kI = 0; // output per unit of integrated error in position (output/(rotation*s))
            turretPID.kD = .005; // output per unit of error derivative in position (output/rps)
        turretMotor.getConfigurator().apply(turretPID);
    }
    
    public double turretPosition(){
        //System.out.println("Turret Rotation Pos:" + turretMotor.getPosition().getValueAsDouble());
        return turretMotor.getPosition().getValueAsDouble();
    }
    public double turretVelocity(){
        return turretVelocity.getVelocity().getValueAsDouble();
    }

    public void spin(double speed){
        switch (state){
            case regular:
                while(state == StateMachine.regular){
                    if (turretPosition() >= 1.7){
                        state = StateMachine.flipcw;
                        System.out.println("Hey guys We have to flip this (ccw)");
                    }
                    if (turretPosition() <= -2.2){
                        state = StateMachine.flipccw;
                        System.out.println("Hey guys We have to flip this (cw)");
                    }
                    else {
                        turretMotor.set(speed);
                        System.out.println("Hey guys Everything looks a-okay up here!");
                    }
                }
            case flipcw:
                while(state == StateMachine.flipcw){
                    if(turretPosition() <= -2.2 + buffer){
                        turretMotor.set(0);
                        state = StateMachine.regular;
                        System.out.println("Hey guys Its normal again good news! (cw)");
                    }
                    else {
                    turretMotor.set(.2);
                    System.out.println("Hey guys We are still spinning! (cw)");
                    }
                }
            case flipccw:
                while(state == StateMachine.flipccw){
                    if(turretPosition() >= 1.7 - buffer){
                        turretMotor.set(0);
                        state = StateMachine.regular;
                        System.out.println("Hey guys Its normal again good news! (ccw)");
                    }
                    else {
                        turretMotor.set(.2);
                        System.out.println("Hey guys We are still spinning! (cw)");
                    }
                }
        }
    }
    /*
    private float MinLimit;
    private double MaxLimit;
    private double currTempLimit;
    private float Buffertimer = 1;
    //private final Timer timer = new Timer();
    public void HardLimits(){
        var MinLimit = turretMotor.getReverseLimit();
        var MaxLimit = turretMotor.getForwardLimit();
    }
    */
    public void gotoSetpoint(double setpoint){
        switch (state){
            case regular:
            //Every 1.0 is a quarter turn, 4:1 ratio
                while(state == StateMachine.regular){
                    if (turretPosition() >= 1.5){
                        //Move the robot to the Neg Pos listed in the next line
                        //turretMotor.setPosition(-1.56, 3);
                        
                        
                        state = StateMachine.flipccw;
                        //System.out.println("Hey guys We have to flip this (ccw)");
                    }
                    if (turretPosition() <= -2.3){
                        //Move the robot to the Neg Pos listed in the next line
                        //turretMotor.setPosition(1.48, 3);
                        
                        
                        state = StateMachine.flipcw;
                        //System.out.println("Hey guys We have to flip this (cw)");
                    }
                    else {
                        final PositionVoltage setPosition = new PositionVoltage(setpoint).withSlot(0);
                        turretMotor.setControl(setPosition);
                        //System.out.println("Hey guys Everything looks a-okay up here!");
                    }
                }
            case flipcw:
                while(state == StateMachine.flipcw){
                    if(turretPosition() <= 1.5 - buffer){
                        state = StateMachine.regular;
                        System.out.println("Hey guys Its normal again good news! (cw)");
                    }
                    else {
                    final PositionVoltage setPosition = new PositionVoltage(1.5 - buffer).withSlot(0);
                    turretMotor.setControl(setPosition);
                    System.out.println("Hey guys We are still spinning! (cw)");
                    }
                }
            case flipccw:
                while(state == StateMachine.flipccw){
                    if(turretPosition() >= -1.5 + buffer){
                        state = StateMachine.regular;
                        System.out.println("Hey guys Its normal again good news! (ccw)");
                    }
                    else {
                        final PositionVoltage setPosition = new PositionVoltage(-1.5 + buffer).withSlot(0);
                        turretMotor.setControl(setPosition);
                        System.out.println("Hey guys We are still spinning! (cw)");
                    }
                }
        }
    }

    public void MotorAutoRotate(){
        switch (state){
            case flipcw:
                if (turretPosition() <= 1.3){
                    state = StateMachine.Regular;
                    System.out.println("Hey guys Its normal again good news! (cw)");
                }
            case flipccw:
                if (turretPosition() >= -2.1){
                    state = StateMachine.regular;
                    System.out.pritnln("Hey guys Its normal again good news! (ccw)");
                }
            default:
                // Every 1.0 is a quarter turn, 4:1 ratio
                if (turretPosition() >= 1.5){
                    // Move the robot to the Neg Pos listed in the next line
                    state = StateMachine.flipccw;
                }
                else if (turrentPosition() <= -2.3){
                    // Move the robot to the Neg Pos listed in the next line
                    state = StateMachine.flipcw;
                }
        }
    }

    public boolean areweatsetpoint(double setpoint){
        return Math.abs(turretPosition() - setpoint) <= .5;
    }
    
    public void stop(){
        turretMotor.set(0);
    }
   @Override 
   public void periodic(){
    SmartDashboard.putNumber("turret pos", turretPosition());
   }
}










