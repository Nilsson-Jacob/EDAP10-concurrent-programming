package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

/**
 * Program 3 for washing machine. This also serves as an example of how washing
 * programs can be structured.
 * 
 * This short program stops all regulation of temperature and water levels,
 * stops the barrel from spinning, and drains the machine of water.
 * 
 * It can be used after an emergency stop (program 0) or a power failure.
 */
public class WashingProgram1 extends ActorThread<WashingMessage> {

    private WashingIO io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;
    private WashingMessage ack;
    
    public WashingProgram1(WashingIO io,
                           ActorThread<WashingMessage> temp,
                           ActorThread<WashingMessage> water,
                           ActorThread<WashingMessage> spin) 
    {
        this.io = io;
        this.temp = temp;
        this.water = water;
        this.spin = spin;
    }
    
    @Override
    public void run() {
        try {
        	// Lock the hatch
        	io.lock(true);
        	//LET WATER INTO MACHINE
        	water.send(new WashingMessage(this,WashingMessage.WATER_FILL,10));
        	ack = receive();
        	
        	//SET TEMPERATURE TO 40
        	temp.send(new WashingMessage(this,WashingMessage.TEMP_SET,40));
        	ack = receive();
        	
        	
        	//START WASHING
        	spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
        	ack = receive();
        	Thread.sleep(30 * 60000 / Settings.SPEEDUP);

        	
        	//DRAIN
        	temp.send(new WashingMessage(this,WashingMessage.TEMP_IDLE));
        	ack = receive();
        	water.send(new WashingMessage(this,WashingMessage.WATER_DRAIN));
        	ack = receive();
        	water.send(new WashingMessage(this,WashingMessage.WATER_IDLE));
        	
        	
        	
        	for (int i = 0; i<5; i++) {
        		water.send(new WashingMessage(this,WashingMessage.WATER_FILL,10));
        		ack = receive();
        		Thread.sleep(60000 * 2 / Settings.SPEEDUP);
        		water.send(new WashingMessage(this,WashingMessage.WATER_DRAIN));
        		ack = receive();
        		water.send(new WashingMessage(this,WashingMessage.WATER_IDLE));
        	}
        	
        	
        	//CENTRIFUGE
        	water.send(new WashingMessage(this,WashingMessage.WATER_DRAIN));
        	ack = receive();
        	spin.send(new WashingMessage(this,WashingMessage.SPIN_FAST));
        	ack = receive();
        	Thread.sleep(60000 * 5 / Settings.SPEEDUP);
        	
        	spin.send(new WashingMessage(this,WashingMessage.SPIN_OFF));
        	ack = receive();
        	water.send(new WashingMessage(this,WashingMessage.WATER_IDLE));
        	// Now that the barrel has stopped, it is safe to open the hatch.
        	io.lock(false);
            
            System.out.println("washing program 1 finished");
        } catch (InterruptedException e) {
            
            // If we end up here, it means the program was interrupt()'ed:
            // set all controllers to idle
        	
            try {
				temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
	            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
	            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
	            System.out.println("washing program terminated");
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

        }
    }
}
