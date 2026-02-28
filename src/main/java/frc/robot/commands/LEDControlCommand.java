/*package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Intake;
//import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.Shooter;
import frc.robot.Constants.ClimberConstants.LEDConstants.LEDColor;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Candle;

public class LEDControlCommand extends Command {
   
  //  private final Limelight shooterLimelight;
    private final Candle candle;
    public LEDControlCommand(Candle candle, RobotContainer robot) {
    
      //  shooterLimelight = robot.shooterLimelight;
        this.candle = candle;

        addRequirements(candle);
    }

    @Override
    public void initialize() {
    // This command should turn on the LED at the start of the game
        
    }
    
    @Override
    public void execute() {
    
    }
    @Override
    public boolean isFinished(){
        // This command should turn off the LED at the end of the game
        return false;
    }
}*/