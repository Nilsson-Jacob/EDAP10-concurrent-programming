
import lift.LiftView;


public class LiftMain {

    public static void main(String[] args) {
    	
        LiftView view = new LiftView();
        LiftMonitor mon = new LiftMonitor(view);
        
        for (int i = 0; i < 20; i++) {
        	PassengerThread p1 = new PassengerThread(view,mon);
        	
        	p1.start();
        }
        
        LiftThread l = new LiftThread(view,mon);
        l.start();
        
    }
}
