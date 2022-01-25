package train.simulation;

import java.util.ArrayList;
import java.util.List;


import train.model.Route;
import train.model.Segment;
import train.view.TrainView;

public class TrainSimulation {

    public static void main(String[] args) {
    	
        TrainView view = new TrainView();
        TrainMonitor monitor = new TrainMonitor();
  
        createTrainsOfLengthN(20,3,monitor,view);
        
    }
    
    public static void createTrainsOfLengthN(int amount,int size, TrainMonitor monitor, TrainView view) {
        for (int i = 0; i < amount; i++) {
        Thread t1 = new Thread(() -> {
        		try {
        			createTrainOfLengthN(view.loadRoute(),monitor, size);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	});
        	
        t1.start();
        }
    }
    
    public static void createTrainOfLengthN(Route route,TrainMonitor monitor, int n) throws InterruptedException {
    	List<Segment> list = new ArrayList<Segment>();
         for (int i = 0; i < n; i++) {
        	 Segment s = route.next();
        	 list.add(s);
        	 monitor.awaitAndSetBusyAndEnter(s);
         }
         while(true) {
        	 Segment next = route.next();
        	 monitor.awaitAndSetBusyAndEnter(next);
        	 list.add(0,next);
        	 Segment tail = list.remove(list.size()-1);
        	 tail.exit();
        	 monitor.setSegmentFree(tail);
         }
    }
}
