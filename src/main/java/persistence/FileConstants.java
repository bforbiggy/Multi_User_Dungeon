package persistence;

import java.util.regex.Pattern;

public class FileConstants {
    // Text assets for game
    public final static String TITLE_SCREEN_PATH = "src/main/resources/assets/title.txt";
    public final static String CONTROLS_PATH = "src/main/resources/assets/controls.txt";

    // Database paths
    public final static String SAVE_FOLDER_PATH = "src/main/resources/saves/";
    public final static String ASSETS_PATH = "src/main/resources/assets/";
    public static final String ACCOUNT_PATH = "src/main/resources/accounts.db";


    // Used for CSV saving/loading
    public static final String COMMA = ",";
    public static final String PERIOD = ".";
    public static final String VERT_BAR = "|";
    public static final String COMMA_REGEX = Pattern.quote(COMMA);
    public static final String PERIOD_REGEX = Pattern.quote(PERIOD);
    public static final String VERT_BAR_REGEX = Pattern.quote(VERT_BAR);
}
