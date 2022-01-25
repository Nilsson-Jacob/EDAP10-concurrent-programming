import lift.Passenger;
import lift.LiftView;

public class LiftMonitor {
	private int liftFloor;
	private int nbrOfPassengers;
	private boolean doorsOpen;
	private int direction;
	private int[] waitEntry;
	private int[] waitExit;
	private LiftView view;
	private int currentlyEntering;
	private int currentlyExiting;
	
	
	
	public LiftMonitor(LiftView view) {
		liftFloor = 0;
		nbrOfPassengers = 0;
		doorsOpen = false;
		direction = 1;
		waitEntry = new int[7];
		waitExit = new int[7];
		this.view = view;
		currentlyEntering = 0;
		currentlyExiting = 0;
	}
	

	
	public synchronized int getNbrOfPassengers() {
		return nbrOfPassengers;
	}
	
	
	public synchronized boolean waitingToEnterAtFloor(int floor) {
		return waitEntry[floor] != 0;
	}
	
	public synchronized boolean waitingToExitAtFloor(int floor) {
		return waitExit[floor] != 0;
	}
	
	public synchronized int getDirection() {
		return direction;
	}
	
	public synchronized void setFloor(int floor) {
		liftFloor = floor;
	}
	
	public synchronized int getFloor() {
		return liftFloor;
	}
	
	public synchronized void handleDirection() {
		if(liftFloor == 6) {
			direction = -1;
		} else if (liftFloor == 0) {
			direction = 1;
		}
	}
	
	private synchronized boolean waitingToEnter() {
		boolean check = false;
		for (int i = 0; i < waitEntry.length; i++) {
			if (waitEntry[i] != 0) {
				check = true;
			}
		}
		return check;
	}
	
	public synchronized void waitForPassengers() throws InterruptedException {
		while (nbrOfPassengers == 0 && !waitingToEnter()) {
			wait();
		}
	}
	
	public synchronized void handleOpening(int floor) {
		view.openDoors(floor);
		doorsOpen = true;
		notifyAll(); //Notify passengers to enter
	}
	
	public synchronized void handleClosing() throws InterruptedException {
		while((waitEntry[liftFloor] != 0 && nbrOfPassengers <4) || (waitExit[liftFloor] != 0)
				|| currentlyEntering != 0 || currentlyExiting != 0) {
			wait();
		}
		doorsOpen = false;
		view.closeDoors();
	}
	
	public synchronized void waitForExit(Passenger pass) throws InterruptedException {
		waitExit[pass.getDestinationFloor()] += 1;
		while(liftFloor != pass.getDestinationFloor() || !doorsOpen) {
			wait();
		}

		currentlyExiting += 1;
		waitExit[pass.getDestinationFloor()] -= 1;
		nbrOfPassengers -= 1;
	}
	
	public synchronized void notifyExited() {
		currentlyExiting -= 1;
		if(currentlyExiting == 0 && currentlyEntering == 0) {
			notifyAll();
		}
	}
	
	public synchronized void notifyEntered() {
		currentlyEntering -= 1;
		if(currentlyEntering == 0 && currentlyExiting == 0) {
			notifyAll();
		}
	}
	
	public synchronized void waitForLift(Passenger pass) throws InterruptedException {
		waitEntry[pass.getStartFloor()] += 1;
		notifyAll(); //Notify that a passenger has arrived
		while(liftFloor != pass.getStartFloor() || !doorsOpen || nbrOfPassengers == 4) {
			wait();
		}
		currentlyEntering +=1;
		waitEntry[pass.getStartFloor()] -= 1;
		nbrOfPassengers += 1;
	}
}
