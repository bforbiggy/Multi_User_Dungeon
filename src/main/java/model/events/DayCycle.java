package model.events;

import java.util.ArrayList;

public class DayCycle {
    private Thread dcThread;
    private static final int CYCLE_SECOND_COUNT = 5 * 60; // Five minutes * 60 seconds/minute

    private int timeElapsed = 0;
    private boolean toggle = true;
    private ArrayList<DayCycleListener> listeners = new ArrayList<DayCycleListener>();

    public DayCycle() {
        Runnable runnable = () -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1000);
                    timeElapsed++;
                    if(timeElapsed >= CYCLE_SECOND_COUNT) {
                        timeElapsed = 0;
                        toggleDayNight();
                    }
                }
            } catch (InterruptedException e) {

            }
        };
        dcThread = new Thread(runnable);
        dcThread.start();
    }

    public void addListener(DayCycleListener listener) {
        listeners.add(listener);
    }

    public void removeListener(DayCycleListener listener) {
        listeners.remove(listener);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    public void notifyListener() {
        for (DayCycleListener listener : listeners)
            listener.DayCycleChange(this);
    }

    private void toggleDayNight() {
        toggle = !toggle;
        notifyListener();
    }

    public boolean isDay() {
        return toggle;
    }

    public boolean isNight() {
        return !toggle;
    }

    public void stop() {
        dcThread.interrupt();
    }

    public void reset(){
        timeElapsed = 0;
        removeAllListeners();
    }
}
