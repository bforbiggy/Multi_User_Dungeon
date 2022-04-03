package persistence.accounts;

import java.util.EnumMap;
import java.util.Set;
import model.Statistic;

public class Account {
    private String username;
    private String password;

    private EnumMap<Statistic, Integer> statistics;

    public Account(String username, String password) {
        this(username, password, new EnumMap<Statistic, Integer>(Statistic.class));
    }

    public Account(String username, String password, EnumMap<Statistic, Integer> statistics) {
        this.username = username;
        this.password = password;
        this.statistics = statistics;
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

    public Set<Statistic> getKeySet(){
        return statistics.keySet();
    }

    public Integer getData(Statistic stat) {
        Integer value = statistics.get(stat);
        return value != null ? value : -1;
    }

    public void setData(Statistic stat, Integer value) {
        statistics.put(stat, value);
    }

    public void addToData(Statistic stat, Integer change) {
        Integer orgVal = statistics.containsKey(stat) ? statistics.get(stat) : 0;
        statistics.put(stat, orgVal + change);
    }
}
