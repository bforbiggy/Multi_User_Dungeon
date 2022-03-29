package persistence.accounts;

import java.util.EnumMap;
import java.util.Set;

public class Account {
    private String username;
    private String password;

    private EnumMap<AccountStat, Integer> statistics;

    public Account(String username, String password) {
        this(username, password, new EnumMap<AccountStat, Integer>(AccountStat.class));
    }

    public Account(String username, String password, EnumMap<AccountStat, Integer> statistics) {
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

    public Set<AccountStat> getKeySet(){
        return statistics.keySet();
    }

    public Integer getData(AccountStat stat) {
        Integer value = statistics.get(stat);
        return value != null ? value : -1;
    }

    public void setData(AccountStat stat, Integer value) {
        statistics.put(stat, value);
    }

    public void addToData(AccountStat stat, Integer change) {
        Integer orgVal = statistics.containsKey(stat) ? statistics.get(stat) : 0;
        statistics.put(stat, orgVal + change);
    }
}
