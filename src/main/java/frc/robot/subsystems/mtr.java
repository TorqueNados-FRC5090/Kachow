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

public class mtr extends SubsystemBase {
    TalonFXS samsmotor;
    double setpoint;
    double riseandshine = .5;
    nanami joltik = nanami.regular;

    public mtr() {
        samsmotor = new TalonFXS(8);
        int off = 1600;
        int steamhappy = 1000; //happy and fast
        int CyberPunk2077 = 60; //happy accelerator

        TalonFXSConfiguration samsconfig = new TalonFXSConfiguration();
            samsconfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
            samsconfig.Commutation.MotorArrangement = MotorArrangementValue.Minion_JST;
        samsmotor.getConfigurator().apply(samsconfig);

        MotionMagicConfigs samsmotionmagic = new TalonFXSConfiguration().MotionMagic;
            samsmotionmagic.MotionMagicCruiseVelocity = steamhappy; // steam happy very fast
            samsmotionmagic.MotionMagicAcceleration = CyberPunk2077; // cyberpunk2077 :333 qwik
            samsmotionmagic.MotionMagicJerk = off; // jerking off
        samsmotor.getConfigurator().apply (samsmotionmagic);

        Slot0Configs samspidconfig = new Slot0Configs();
            // kG, kS, kV, and kA all go unused, we dont have gravity or static
            samspidconfig.kP = .6; // output per unit of error in position (output/rotation)
            samspidconfig.kI = 0; // output per unit of integrated error in position (output/(rotation*s))
            samspidconfig.kD = .005; // output per unit of error derivative in position (output/rps)
        samsmotor.getConfigurator().apply(samspidconfig);
    }
    
    public double samsposition(){
        //System.out.println("Turret Rotation Pos:" + samsmotor.getPosition().getValueAsDouble());
        return samsmotor.getPosition().getValueAsDouble();
    }
    public double samsvelocity(){
        return samsmotor.getVelocity().getValueAsDouble();
    }

    public void helicopteringitrightnowlikeholyholyshitohmygoditsjustfuckingspinningmanholyholyfuckingshititskindaitskindaimpressiveactuallymygod(double speed){
        switch (joltik){
            case regular:
                while(joltik == nanami.regular){
                    if (samsposition() >= 1.7){
                        joltik = nanami.flipcw;
                        System.out.println("Hey guys We have to flip this (ccw)");
                    }
                    if (samsposition() <= -2.2){
                        joltik = nanami.flipccw;
                        System.out.println("Hey guys We have to flip this (cw)");
                    }
                    else {
                        samsmotor.set(speed);
                        System.out.println("Hey guys Everything looks a-okay up here!");
                    }
                }
            case flipcw:
                while(joltik == nanami.flipcw){
                    if(samsposition() <= -2.2 + riseandshine){
                        samsmotor.set(0);
                        joltik = nanami.regular;
                        System.out.println("Hey guys Its normal again good news! (cw)");
                    }
                    else {
                    samsmotor.set(.2);
                    System.out.println("Hey guys We are still spinning! (cw)");
                    }
                }
            case flipccw:
                while(joltik == nanami.flipccw){
                    if(samsposition() >= 1.7 - riseandshine){
                        samsmotor.set(0);
                        joltik = nanami.regular;
                        System.out.println("Hey guys Its normal again good news! (ccw)");
                    }
                    else {
                        samsmotor.set(.2);
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
        var MinLimit = samsmotor.getReverseLimit();
        var MaxLimit = samsmotor.getForwardLimit();
    }
    */
    public void hiiscytheiloveyouyoutheabsolutegoatandiwontskinyoualiveprobablynotokaysoiliedbutisitallthatbadimeanhavingnoskinisablessingifyoureallythinkaboutitlikenomorepapercutsifnoskin(double setpoint){
        switch (joltik){
            case regular:
            //Every 1.0 is a quarter turn, 4:1 ratio
                while(joltik == nanami.regular){
                    if (samsposition() >= 1.5){
                        //Move the robot to the Neg Pos listed in the next line
                        //samsmotor.setPosition(-1.56, 3);
                        
                        
                        joltik = nanami.flipccw;
                        //System.out.println("Hey guys We have to flip this (ccw)");
                    }
                    if (samsposition() <= -2.3){
                        //Move the robot to the Neg Pos listed in the next line
                        //samsmotor.setPosition(1.48, 3);
                        
                        
                        joltik = nanami.flipcw;
                        //System.out.println("Hey guys We have to flip this (cw)");
                    }
                    else {
                        final PositionVoltage meimeiisastupidpedophilebitch = new PositionVoltage(setpoint).withSlot(0);
                        samsmotor.setControl(meimeiisastupidpedophilebitch);
                        //System.out.println("Hey guys Everything looks a-okay up here!");
                    }
                }
            case flipcw:
                while(joltik == nanami.flipcw){
                    if(samsposition() <= 1.5 - riseandshine){
                        joltik = nanami.regular;
                        System.out.println("Hey guys Its normal again good news! (cw)");
                    }
                    else {
                    final PositionVoltage meimeiisastupidpedophilebitch = new PositionVoltage(1.5 - riseandshine).withSlot(0);
                    samsmotor.setControl(meimeiisastupidpedophilebitch);
                    System.out.println("Hey guys We are still spinning! (cw)");
                    }
                }
            case flipccw:
                while(joltik == nanami.flipccw){
                    if(samsposition() >= -1.5 + riseandshine){
                        joltik = nanami.regular;
                        System.out.println("Hey guys Its normal again good news! (ccw)");
                    }
                    else {
                        final PositionVoltage meimeiisastupidpedophilebitch = new PositionVoltage(-1.5 + riseandshine).withSlot(0);
                        samsmotor.setControl(meimeiisastupidpedophilebitch);
                        System.out.println("Hey guys We are still spinning! (cw)");
                    }
                }
        }
    }
    public boolean arewethereyetarewethereyetarewethereyetpleasearewethereyeticantwaittogettheretotheplacearewethereyetidontknowwhenillgettherebecauseimdrivingblindfoldedchallengehastagyoutubehashtagscary(double setpoint){
        return Math.abs(samsposition() - setpoint) <= .5;
    }
    
    public void dudeijustcantrightnowthisshitispissingmeoffsodamnmuchlikemomwhywontyoubuymethosedamntissuesandfuckinglotionimjustinneedofmoisturepleaseeeemomineedthisfromchrischanheartbigbigheartonmysignatureyay(){
        samsmotor.set(0);
    }
   @Override 
   public void periodic(){
    SmartDashboard.putNumber("turret pos", samsposition());
   }
}










