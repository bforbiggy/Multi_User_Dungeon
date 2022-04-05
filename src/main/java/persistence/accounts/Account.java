package persistence.accounts;

import model.tracking.StatTracker;

public class Account {
    private String username;
    private String password;

    private StatTracker tracker;

    public Account(String username, String password) {
        this(username, password, StatTracker.reset());
    }

    public Account(String username, String password, StatTracker tracker) {
        this.username = username;
        this.password = password;
        this.tracker = tracker;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public StatTracker getTracker() {
        return tracker;
    }
}
