package model.tracking;

import java.util.EnumMap;

// ONLY SUPPORTS TRACKING FOR ONE GAME AT A TIME
// CAN BE FIXED BY EACH GAME'S METHODS ALSO SETTING THE TRACKER
public class StatTracker {
    //#region Static methods
    private static StatTracker tracker;
    
    /**
     * Returns the currently used tracker
     * @return the tracker
     */
    public static StatTracker getTracker() {
        return tracker;
    }

    /**
     * Given a previous tracker, change change tracker to new one
     * @param newTracker the tracker to use
     */
    public static void loadTracker(StatTracker newTracker) {
        tracker = newTracker;
    }

    /**
     * Resets the statically stored tracker
     * @return new tracker
     */
    public static StatTracker reset() {
        tracker = new StatTracker();
        return tracker;
    }
    //#endregion

    protected EnumMap<TrackedStat, Integer> trackedStats;

    public StatTracker() {
        trackedStats = new EnumMap<>(TrackedStat.class);
    }

    public StatTracker(EnumMap<TrackedStat, Integer> trackedStats) {
        this.trackedStats = trackedStats != null ? trackedStats : new EnumMap<>(TrackedStat.class);
    }

    /**
     * Given a stat, increase the value associated with said stat.
     * @param stat Stat to change
     * @param val Value to add
     */
    public void addValue(TrackedStat stat, Integer val){
        Integer newVal = getValue(stat) + val;
        trackedStats.put(stat, newVal);
    }

    /**
     * Retrieves the value associated with a stat.
     * @param stat The stat to get the value of
     * @return Integer value of stat, 0 if unavailable
     */
    public Integer getValue(TrackedStat stat){
        Integer val = trackedStats.get(stat);
        return val != null ? val : 0;
    }

    /**
     * Returns the associated stattracker object
     * @return The enum map contained
     */
    public EnumMap<TrackedStat, Integer> getEnumMap(){
        return trackedStats;
    }
}
