import lift.LiftView;
import lift.Passenger;
import java.util.Random;

public class PassengerThread extends Thread {

	private LiftView view;
	private Passenger pass;
	private LiftMonitor mon;
	
	
	
	public PassengerThread(LiftView view, LiftMonitor mon) {
		this.view = view;
		//this.pass = view.createPassenger();
		this.mon = mon;
	}
	
	
	@Override
	public void run() {
		Random rand = new Random();
		while(true) {
			try {
				Thread.sleep(rand.nextInt(45)*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pass = view.createPassenger();
			pass.begin();
			
			try {
				mon.waitForLift(pass);
				pass.enterLift();
				mon.notifyEntered();
				mon.waitForExit(pass);
				pass.exitLift();
				mon.notifyExited();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			pass.end();
			
			
		}
	}
	
	
}
