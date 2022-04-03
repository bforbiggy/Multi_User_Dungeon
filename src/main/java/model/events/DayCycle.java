package model.events;

import java.util.ArrayList;

public class DayCycle {
    private Runnable runnable = () -> {
        while(true) {
            try {
                Thread.sleep(CYCLE_TIMER * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            toggleDayNight();
        }
    };
    private static final int CYCLE_TIMER = 5 * 60; // Currently 5 minutes

    private boolean toggle = true;
    private ArrayList<DayCycleListener> listeners = new ArrayList<DayCycleListener>();

    public DayCycle()
    {
        Thread thread = new Thread(runnable);
        thread.start();
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
}