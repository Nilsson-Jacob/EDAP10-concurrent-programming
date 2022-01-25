package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {
	private WashingIO io;
	private WashingMessage task;
	private ActorThread<WashingMessage> sender;
	private WashingMessage ack;
	private WashingMessage previousTask;
	private double value;
    private int check;

	
	private final int PERIOD = 5 * 1000;
	
    public WaterController(WashingIO io) {
        // TODO
    	this.io = io;
    }

    @Override
    public void run() {
   	 	try {
         // TODO
     		 
     		 
     		 while(true) {
     			 WashingMessage m = receiveWithTimeout(PERIOD / Settings.SPEEDUP);
     			
     		       if (m != null) {
                        System.out.println("got " + m);
                        task = m;
                        sender = task.getSender();
                        ack = new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT);
                        value = task.getValue();
                        check = 1;
                    } 
     	
     		       if (task != null) {
     		    	   
     		    	   switch (task.getCommand()) {
     		    	   		case WashingMessage.WATER_IDLE:
     		    	   			io.fill(false);
     		    	   			io.drain(false);
     		    	   			task = null;
     		    	   			break;
     		    	   			
     		    	   		case WashingMessage.WATER_FILL:
     		    	   			if (io.getWaterLevel() < value) {
     		    	   				io.fill(true);
     		    	   			} else {
     		    	   				sender.send(ack);
     		    	   				io.fill(false);
     		    	   				task = null;
     		    	   			}   		
     		    	   			break;
     		    	   		case WashingMessage.WATER_DRAIN:
     		    	   			if (io.getWaterLevel() == 0 && check == 1) {
     		    	   				//io.drain(false);
     		    	   				sender.send(ack);
     		    	   				check--;
     		    	   				//task = null;
     		    	   			}
     		    	   			io.drain(true);
     		    	   			
     		    	   			
     		    	
     		    	   			break;
     		    	   }
     		    	   
     		    	
     		    	   
     		    	   
     		    	   
     		    	   
     		       }
     	
     		 			}
     	   	} catch (InterruptedException unexpected) {
                // we don't expect this thread to be interrupted,
                // so throw an error if it happens anyway
     	   		throw new Error(unexpected);
            }
    	
    	
    }
}
