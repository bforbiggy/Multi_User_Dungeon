package model.events;

import java.util.ArrayList;

public class DayCycle {
    private Thread dcThread;
    private static final int CYCLE_TIMER = 5 * 60; // Currently 5 minutes

    private boolean toggle = true;
    private ArrayList<DayCycleListener> listeners = new ArrayList<DayCycleListener>();

    public DayCycle()
    {
        Runnable runnable = () -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(CYCLE_TIMER * 1000);
                    toggleDayNight();
                }
            } catch (InterruptedException e) {

            }
        };
        dcThread = new Thread(runnable);
        dcThread.start();
    }
    
    public void addListener(DayCycleListener listener){
        listeners.add(listener);
    }

    public void removeListener(DayCycleListener listener){
        listeners.remove(listener);
    }

    public void removeAllListeners(){
        listeners.clear();
    }

    public void notifyListener()
    {
        for(DayCycleListener listener : listeners)
            listener.DayCycleChange(this);
    }

    private void toggleDayNight()
    {
        toggle = !toggle;
        notifyListener();
    }

    public boolean isDay()
    {
        return toggle;
    }

    public boolean isNight()
    {
        return !toggle;
    }

    public void stop(){
        dcThread.interrupt();
    }
}