/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package doorproject;

import com.oracle.deviceaccess.PeripheralConfig;
import com.oracle.deviceaccess.PeripheralConfigInvalidException;
import com.oracle.deviceaccess.PeripheralExistsException;
import com.oracle.deviceaccess.PeripheralManager;
import com.oracle.deviceaccess.PeripheralNotFoundException;
import com.oracle.deviceaccess.PeripheralTypeNotSupportedException;
import com.oracle.deviceaccess.gpio.GPIOPin;
import com.oracle.deviceaccess.gpio.GPIOPinConfig;
import com.oracle.deviceaccess.gpio.PinEvent;
import com.oracle.deviceaccess.gpio.PinListener;
import java.io.IOException;
import mooc.data.SwitchData;
import mooc.sensor.SwitchSensor;

/**
 *
 * @author Yamir
 */
public class DoorSensor implements PinListener, SwitchSensor  {

    private int doorClosedPinId;
    private int doorOpenPinId;
    private int switchPinId;
    private int switchPortId;
    
    private GPIOLED doorOpenLED;
    private GPIOLED doorClosedLED;   //Green LED

    private GPIOPin switchPin;
    
    private boolean filteringPreviousState = true;

    public DoorSensor(int portId, int switchPinId, int doorClosedPinId, int doorOpenPinId){
        this.doorClosedPinId = doorClosedPinId;
        this.doorOpenPinId = doorOpenPinId;
        this.switchPinId = switchPinId;
        this.switchPortId = switchPortId;
    }
    
        /**
     * This method start the connection to the LED, and turns it on.
     * @throws IOException, PeripheralNotFoundException, PeripheralTypeNotSupportedException,
     * PeripheralConfigInvalidException, PeripheralExistsException
     */
    public void start() throws IOException,
            PeripheralNotFoundException, PeripheralTypeNotSupportedException,
            PeripheralConfigInvalidException, PeripheralExistsException {

        // Open the LED pin (Output)
        doorOpenLED = new GPIOLED(doorOpenPinId);
        doorOpenLED.start();
        System.out.println("Creating doorOpenLED on pin "+doorOpenPinId);
        doorClosedLED = new GPIOLED(doorClosedPinId);
        doorClosedLED.start();
        System.out.println("Creating doorClosedLED on pin "+doorClosedPinId);

        // Config information for the switch
        GPIOPinConfig config1 = new GPIOPinConfig(switchPortId, switchPinId, GPIOPinConfig.DIR_INPUT_ONLY,
                PeripheralConfig.DEFAULT, GPIOPinConfig.TRIGGER_BOTH_EDGES, false);

        //Open pin using the config1 information
        switchPin = (GPIOPin) PeripheralManager.open(config1);

        // Add this class as a pin listener to the buttons
        switchPin.setInputListener(this);
        System.out.println("Listening for switch on pin "+switchPinId);


    }

    @Override
    public void valueChanged(PinEvent event) {
        GPIOPin pin = (GPIOPin) event.getPeripheral();
        // Simple one button = one LED
        try {
            //Verify the event come from the switch
            if (pin == switchPin) {
                if (pin.getValue() == true) {
                    if (filteringPreviousState != true) { //filtering multiple consecutive trues
                        doorClosedLED.setValue(true);
                        doorOpenLED.setValue(false);
                        filteringPreviousState = true;
                    }
                } else {
                    if (filteringPreviousState != false) { //filtering multiple consecutive false
                        doorClosedLED.setValue(false);
                        doorOpenLED.blink(3);
                        doorOpenLED.setValue(true);
                        filteringPreviousState= false;
                    }
                }

            }
        } catch (IOException ex) {
            System.out.println("IOException: " + ex);
        }    
    }

    @Override
    public boolean getState() throws IOException {
        return switchPin.getValue();
    }

    @Override
    public SwitchData getSwitchData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void stop() throws IOException {
        if (doorOpenLED != null) {
            doorOpenLED.setValue(false);
            doorOpenLED.stop();
        }
        if (doorClosedLED != null) {
            doorClosedLED.setValue(false);
            doorClosedLED.stop();
        }
        if(switchPin != null){
            switchPin.close();
        }
    }
    
}
