package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class SpinController extends ActorThread<WashingMessage> {
	private WashingIO io;
	private WashingMessage task;
	private int spinDirection;
	private ActorThread<WashingMessage> sender;
	private WashingMessage ack;
	private WashingMessage previousTask;
    // TODO: add attributes

    public SpinController(WashingIO io) {
        // TODO
    	this.io = io;
    }

    @Override
    public void run() {
        try {

            // ... TODO ...

            while (true) {
                // wait for up to a (simulated) minute for a WashingMessage
                WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);
              
                // if m is null, it means a minute passed and no message was received
                if (m != null) {
                    System.out.println("got " + m);
                    task = m;
                    sender = task.getSender();
                    ack = new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT);
                } 
                
                if (task != null) {
                	
    	            switch(task.getCommand()) {
    	            
	            	case WashingMessage.SPIN_OFF:
	            		io.setSpinMode(WashingIO.SPIN_IDLE);
	            		break;
	            		
	            	case WashingMessage.SPIN_SLOW:
	            		if (spinDirection == WashingIO.SPIN_LEFT) {
	            			io.setSpinMode(WashingIO.SPIN_RIGHT);
	            			spinDirection = WashingIO.SPIN_RIGHT;
	            		} else {
	            			io.setSpinMode(WashingIO.SPIN_LEFT);
	            			spinDirection = WashingIO.SPIN_LEFT;
	            		}
	            		break;
	            		
	            	case WashingMessage.SPIN_FAST:
	            		io.setSpinMode(WashingIO.SPIN_FAST);
	            		break;
    	            }
    	            
    	            if (previousTask != task) {
    	            	 sender.send(ack);
    	            }
    	            previousTask = task; 
                }
                
               


             
                
         // ... TODO ...
            }
        } catch (InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        	}
        }
    }

