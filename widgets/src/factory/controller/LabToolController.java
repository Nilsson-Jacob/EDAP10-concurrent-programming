package factory.controller;

import factory.model.DigitalSignal;
import factory.model.WidgetKind;
import factory.simulation.Painter;
import factory.simulation.Press;
import factory.swingview.Factory;

/**
 * Implementation of the ToolController interface,
 * to be used for the Widget Factory lab.
 * 
 * @see ToolController
 */
public class LabToolController implements ToolController {
    private final DigitalSignal conveyor, press, paint;
    private final long pressingMillis, paintingMillis;
    private int processes;
    
    public LabToolController(DigitalSignal conveyor, DigitalSignal press, DigitalSignal paint, long pressingMillis, long paintingMillis) {
        this.conveyor = conveyor;
        this.press = press;
        this.paint = paint;
        this.pressingMillis = pressingMillis;
        this.paintingMillis = paintingMillis;
        this.processes = 0;
    }

    @Override
    public synchronized void onPressSensorHigh(WidgetKind widgetKind) throws InterruptedException {
        //
        // TODO: you will need to modify this method.
        //
        // Note that this method can be called concurrently with onPaintSensorHigh
        // (that is, in a separate thread).
        //
        if (widgetKind == WidgetKind.BLUE_RECTANGULAR_WIDGET) {

        	conveyorOff();
        	//conveyor.off();
        	//
        	press.on();
            //Thread.sleep(pressingMillis);
        	waitOutside(pressingMillis);
            press.off();
            waitOutside(pressingMillis);
            //Thread.sleep(pressingMillis);
            //
            //conveyor.on();
            conveyorOn();
            
        }
    }
    //paint turns on conveyor even though the press needs it still
    @Override
    public synchronized void onPaintSensorHigh(WidgetKind widgetKind) throws InterruptedException {
        //
        // TODO: you will need to modify this method.
        //
        // Note that this method can be called concurrently with onPressSensorHigh
        // (that is, in a separate thread).
        //
        if (widgetKind == WidgetKind.ORANGE_ROUND_WIDGET) {

        	conveyorOff();
        	//conveyor.off();
        	
        	
        	//
        	paint.on();       	
        	//Thread.sleep(paintingMillis);
        
        	waitOutside(paintingMillis);
        	paint.off();
        	//
        	
        	//conveyor.on();
        	conveyorOn();
        	
        	
        	
        	
        }
    }
    
    private synchronized void conveyorOff() {
    	conveyor.off();
    	processes += 1;
    }
    
    
    
    private synchronized void conveyorOn() {
    	if (processes == 1) {
    		conveyor.on();
    		processes -= 1;
    	} else {
    		processes -= 1;
    	}
    }
    
    private synchronized void waitOutside(long millis) throws InterruptedException {
    	long timeToWakeUp = System.currentTimeMillis() + millis;
    	long now = System.currentTimeMillis();
    	while (timeToWakeUp > now) {
    		long dt = timeToWakeUp - now;
    		wait(dt);
    		now = System.currentTimeMillis();
    	}
    }
    
    


    // -----------------------------------------------------------------------
    
    public static void main(String[] args) {
        Factory factory = new Factory();
        ToolController toolController = new LabToolController(factory.getConveyor(),
                                                              factory.getPress(),
                                                              factory.getPaint(),
                                                              Press.PRESSING_MILLIS,
                                                              Painter.PAINTING_MILLIS);
        factory.startSimulation(toolController);
    }
}
