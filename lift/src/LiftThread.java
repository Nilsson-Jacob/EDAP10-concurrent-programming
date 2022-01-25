import lift.LiftView;


public class LiftThread extends Thread {
private LiftMonitor mon;
private LiftView view;
	
	public LiftThread(LiftView view, LiftMonitor mon) {
		this.mon = mon;
		this.view = view;
	}
	
	
	
	@Override
	public void run() {
		while(true) {
			mon.handleDirection();
			
			int destination = mon.getFloor()+mon.getDirection();
			
			try {
				mon.waitForPassengers();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			view.moveLift(mon.getFloor(),destination);
			mon.setFloor(destination);
		
		
			int floor = mon.getFloor();
			if ((mon.waitingToEnterAtFloor(floor) && mon.getNbrOfPassengers() <4) || mon.waitingToExitAtFloor(floor)) {
				mon.handleOpening(destination);
				try {
					mon.handleClosing();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

				
			
			
			
			
			
			
			
			}
			
	
	
	
	
	}
}