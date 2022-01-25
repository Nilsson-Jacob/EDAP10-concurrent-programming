import java.util.concurrent.Semaphore;

import clock.AlarmClockEmulator;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

public class ClockMain {

    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();
        Semaphore sem = in.getSemaphore();
        Monitor mon = new Monitor(0,0,0, out);
        Semaphore alarmSemaphore = mon.getAlarmSemaphore();
        
        Thread clockTick = new Thread(() -> {
        		try {
					mon.updateTime();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	
        });
        
       Thread alarmCheck = new Thread(() -> {
	    	   try {
	    		   mon.alarmCheck();
	    	   } catch (InterruptedException e) {
				// TODO Auto-generated catch block
	    		   e.printStackTrace();
	    	   }
       });
        
        clockTick.start();
        alarmCheck.start();
        
        //MAIN INPUT LOOP
        while (true) {
        	
        	//Waits for user input
        	sem.acquire();
            UserInput userInput = in.getUserInput();
            int choice = userInput.getChoice();
           
            int h = userInput.getHours();
            int m = userInput.getMinutes();
            int s = userInput.getSeconds();
         
            switch(choice) {
            case 1:
            	//CLOCK TIME
            	mon.setHour(h);
            	mon.setMinute(m);
            	mon.setSecond(s);
            	
            	break;
            case 2:
            	//ALARM TIME
            	mon.setAHour(h);
            	mon.setAMinute(m);
            	mon.setASecond(s);
            
            	break;
            case 3:
            	//PRESSED BOTH BUTTONS
            	if (mon.getAlarmEnabled()) {
            		mon.setAlarmEnabled(false);
            		out.setAlarmIndicator(false);
                	mon.setAHour(0);
                	mon.setAMinute(0);
                	mon.setASecond(0);
                	
            	} else {
            		mon.setAlarmEnabled(true);
            		out.setAlarmIndicator(true);
            		alarmSemaphore.release();
            	}
            	
            	
            	break;
            }
            
      
            
        
            
            
            System.out.println("choice=" + choice + " h=" + h + " m=" + m + " s=" + s);
        }
    }
}
