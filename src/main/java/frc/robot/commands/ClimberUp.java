package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ClimberConstants.ClimberPosition;
import frc.robot.subsystems.Climber;


public class ClimberUp extends Command {
    Climber climb;
    ClimberPosition pos;

    public ClimberUp(Climber climb, ClimberPosition pos){
        this.climb = climb;
        this.pos = pos;
    }
    
    @Override
    public void initialize() {
        
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        climb.climbTo(pos);
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        climb.stop();
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false; // Has no end condition
    }
    
}
