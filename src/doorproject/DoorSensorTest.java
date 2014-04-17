/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package doorproject;

import com.oracle.deviceaccess.PeripheralConfigInvalidException;
import com.oracle.deviceaccess.PeripheralExistsException;
import com.oracle.deviceaccess.PeripheralNotFoundException;
import com.oracle.deviceaccess.PeripheralTypeNotSupportedException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Yamir
 */
public class DoorSensorTest extends MIDlet {
   
    DoorSensor doorSensor;
        /**
     * Imlet lifecycle start method
     * 
     * This method creates a GPIOLED, and invoked the blink method, to blink the
     * LED X number of times, in our case 8 times. 
     */
    @Override
    public void startApp() {
        doorSensor = new DoorSensor(0, 17, 24, 23);
        
        try {
            doorSensor.start();
        } catch (IOException ex) {
            Logger.getLogger(DoorSensorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
       /**
   * Imlet lifecycle termination method
   * 
   * @param unconditional If the imlet should be terminated whatever
   */
    @Override
    public void destroyApp(boolean unconditional) {
        try {
            doorSensor.stop();
        } catch (IOException ex) {
            System.out.println("IOException: " + ex);
        }
    }

    /**
     * Imlet lifecycle pause method
     */
    @Override
    public void pauseApp() {
    }
}
