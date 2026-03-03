package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Turret;


public class TurretPosition extends Command {
   Turret turret; 
   double setpoint;

    public TurretPosition(Turret turret, double setpoint){
        this.turret = turret;
        this.setpoint = setpoint;
    }

    @Override
    public void initialize(){}

    @Override
    public void execute(){
       turret.gotoSetpoint(setpoint);
    }

    // Called once the command ends or is interrupted.
   // @Override
   // public void end(boolean interrupted){
        //turret.stop();
   // }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
       return turret.areweatsetpoint(setpoint); 
    // Has no end condition
    }
    
}
