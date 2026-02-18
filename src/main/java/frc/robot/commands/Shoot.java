package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Shooter;

public class Shoot extends Command {
    Shooter shoot;
    
    public Shoot(Shooter shoot){
        this.shoot = shoot;
    }
  
    @Override
    public void initialize(){
        
    }

    @Override
    public void execute(){
        shoot.shoot();
    }

    @Override 
    public void end(boolean interrupted){

    }

    @Override
    public boolean isFinished(){
        return false;
    }

}
