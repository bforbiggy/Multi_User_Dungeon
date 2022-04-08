package model.events;

import java.util.ArrayDeque;
import java.util.ArrayList;

import model.Game;

public class PlayerTurnEnd {
    private Game game;
    private ArrayList<PlayerTurnEndListener> listeners = new ArrayList<PlayerTurnEndListener>();
    private ArrayDeque<PlayerTurnEndListener> removeTargets = new ArrayDeque<PlayerTurnEndListener>();

    public PlayerTurnEnd(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void addListener(PlayerTurnEndListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PlayerTurnEndListener listener) {
        listeners.remove(listener);
    }

    public void scheduleRemoveListener(PlayerTurnEndListener listener) {
        removeTargets.push(listener);
    }

    public void removeScheduled() {
        while (!removeTargets.isEmpty())
            listeners.remove(removeTargets.pop());
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    public void notifyAllListeners() {
        for (PlayerTurnEndListener listener : listeners)
            listener.onPlayerTurnEnd(this);
        removeScheduled();
    }
}
