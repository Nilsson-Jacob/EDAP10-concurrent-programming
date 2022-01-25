package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {
	private WashingMessage task;
	private ActorThread<WashingMessage> sender;
	private WashingMessage ack;
	private WashingMessage previousTask;
	private WashingIO io;
	private double value;
	
	private final double TEMP_MARGIN = 2;
	private final int DT = 10000;
	private final double MU = DT * 0.0000478;
	private final double ML = DT * 0.000000952+0.2;
	private int check;

    public TemperatureController(WashingIO io) {
        this.io = io;
    }

    @Override
    public void run() {
    	 try {
        
    		 
    		 
    		 
    		 //TEMP SENDS ACK WHEN TEMP IDLE HAS BEEN SET AND WHEN TEMP SET HAS REACHED TEMP
    		 while(true) {
    			 WashingMessage m = receiveWithTimeout(DT / Settings.SPEEDUP);
    			
    		       if (m != null) {
    		    	   System.out.println(MU);
                       System.out.println("got " + m);
                       task = m;
                       sender = task.getSender();
                       ack = new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT);
                       value = task.getValue();
                       check = 1;
                   } 
    	
    		       if (task != null) {
    		    	   
    		    	   switch (task.getCommand()) {
    		    	   		case WashingMessage.TEMP_IDLE:
    		    	   			io.heat(false);
    		    	   			if (check == 1) {
    		    	   				sender.send(ack);
    		    	   				check--;
    		    	   			}
    		    	   			
    		    	   			break;
    		    	   			
    		    	   		case WashingMessage.TEMP_SET:
    		    	   			if  (io.getTemperature() >= (value - MU)) {
    		    	   				System.out.println(io.getTemperature());
    		    	   				io.heat(false);
    		    	   			} else if (io.getTemperature() <= (value-TEMP_MARGIN+ML)) {
    		    	   				System.out.println(io.getTemperature());
    		    	   				io.heat(true);
    		    	   			} 
    		    	   			if (check == 1 && io.getTemperature() >= value-TEMP_MARGIN) {
    		    	   				sender.send(ack);
    		    	   				check--;
    		    	   			}
    		    	   			
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
