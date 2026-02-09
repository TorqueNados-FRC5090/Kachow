package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Intake;

public class IntakePiece extends Command{
 Intake intake;
    public IntakePiece(Intake intake){
        this.intake = intake;
    }
    
    @Override
    public void initialize() {
        intake.yummy();
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
    intake.rotate();
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
    intake.rotatestop();
    intake.full();
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false; // Has no end condition
    }
    
}
