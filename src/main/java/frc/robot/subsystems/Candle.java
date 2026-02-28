/*package frc.robot.subsystems;

import com.ctre.phoenix6.configs.LEDConfigs;
import com.ctre.phoenix6.hardware.CANdle;
import frc.robot.Constants.LEDConstants.LEDColor;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Candle extends SubsystemBase {

 public CANdle candle;
    /** Creates a CANdle
     * @param ID CAN ID of the CANdle
    public Candle(){
        candle = new CANdle(2, "Default Name");
        candle.setStatusFramePeriod(CANdleStatusFrame.CANdleStatusFrame_Status_1_General, 500);
    }
    
    /** Sets the LEDs to the selected color
     *  @param color The {@link LEDColor} to use
    public void setAll(LEDConfigs color) {
        candle.setLEDs(color.getRed(), color.getGreen(), color.getBlue());
    }

    /** Sets one LED strip to one color 
     *  @param color The {@link LEDColor} to use
     *  @param strip The {@link LEDStrip} to change color
    public void setStrip(LEDColor color, LEDStrip strip) {
        candle.setLEDs(color.getRed(), color.getGreen(), color.getBlue(), 0, 
            strip.getStartingIndex(), strip.getStripLength()); 
    }
}
*/