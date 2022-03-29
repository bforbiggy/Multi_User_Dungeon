package persistence.accounts;

public enum AccountStat {
    GAMES_PLAYED,
    LIVES_LOST,
    MONSTERS_SLAIN,
    GOLD_EARNED,
    ITEMS_FOUND;

    public static AccountStat stringToEnum(String input){
        switch(input){
            case "GAMES_PLAYED":
                return GAMES_PLAYED;
            case "LIVES_LOST":
                return LIVES_LOST;
            case "MONSTERS_SLAIN":
                return MONSTERS_SLAIN;
            case "GOLD_EARNED":
                return GOLD_EARNED;
            case "ITEMS_FOUND":
                return ITEMS_FOUND;
            default:
                return null;
        }
    }

    public static String enumToString(AccountStat stat) {
        switch (stat) {
            case GAMES_PLAYED:
                return "GAMES_PLAYED";
            case LIVES_LOST:
                return "LIVES_LOST";
            case MONSTERS_SLAIN:
                return "MONSTERS_SLAIN";
            case GOLD_EARNED:
                return "GOLD_EARNED";
            case ITEMS_FOUND:
                return "ITEMS_FOUND";
            default:
                return "";
        }
    }
}
