package train.simulation;

import java.util.HashSet;
import java.util.Set;
import train.model.Segment;


public class TrainMonitor {
	private Set<Segment> busySegments;
	
	public TrainMonitor() {
		this.busySegments = new HashSet<Segment>();
	}
	
	private synchronized void setSegmentBusy(Segment s) {
		busySegments.add(s);
	}
	
	public synchronized void setSegmentFree(Segment s) {
		busySegments.remove(s);
		notifyAll();
	}
	
	private synchronized void awaitSegment(Segment s) throws InterruptedException {
		while(busySegments.contains(s)) {
			wait();
		}
	}
	
	public synchronized void awaitAndSetBusyAndEnter(Segment s) throws InterruptedException {
		awaitSegment(s);
		setSegmentBusy(s);
		s.enter();
		
	}
	
}

