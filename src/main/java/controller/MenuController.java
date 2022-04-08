package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import model.*;
import model.entities.Player;
import model.env.Map;
import model.tracking.StatTracker;
import model.tracking.TrackedStat;
import persistence.FileConstants;
import persistence.accounts.*;
import util.CSVLoader;
import util.GameFormat;
import util.GameLoader;
import util.TextLoader;

public class MenuController extends Controller {
    private enum State {
        NEW, CONTINUE, ENDLESS, SPECTATE
    }

    private static final String GUEST_MENU_TXT = TextLoader.loadText(FileConstants.GUEST_MENU_PATH);
    private static final String USER_MENU_TXT = TextLoader.loadText(FileConstants.USER_MENU_TXT);

    private AccountService accountService;
    private List<File> maps;

    private Account account;
    private Game game;
    private Player player;
    private Map map;

    private State state;

    public MenuController() {
        // Loads in all accounts
        accountService = AccountService.load(FileConstants.ACCOUNT_PATH);

        // Loads in map files
        File targetFolder = new File(FileConstants.ASSETS_PATH);
        FilenameFilter filter = (file, name) -> name.endsWith("xml") || name.endsWith("csv") || name
                .endsWith("json");
        maps = Arrays.asList(targetFolder.listFiles(filter));
    }

    public static Player requestPlayer() {
        System.out.println("Please give your Player's name:");
        String name = scanner.nextLine();
        System.out.println("Please give a basic description about them: ");
        String description = scanner.nextLine();
        return new Player(name, description, Player.DEFAULT_STATS.copy());
    }

    public void processInput(String input) {
        String[] tokens = input.split(" ");
        tokens[0] = tokens[0].toUpperCase();

        // Logging into account
        if (tokens[0].equals("LOGIN") && tokens.length >= 3) {
            account = accountService.authenticate(tokens[1], tokens[2]);

            // Authentication success
            if (account != null) {
                System.out.println("Successfully logged in!");
                StatTracker.loadTracker(account.getTracker());
            }
            // Authentication fail
            else {
                System.out.println("Invalid username/password combination!");
            }
        }
        // Registering account
        else if (tokens[0].equals("REGISTER") && tokens.length >= 3) {
            account = accountService.addAccount(tokens[1], tokens[2]);
            if (account != null) {
                accountService.save(FileConstants.ACCOUNT_PATH);
            }
        }
        // Change password
        else if (tokens[0].equals("CHANGEPASSWORD") && tokens.length >= 3) {
            if (account != null && account.getPassword().equals(tokens[1])) {
                account.setPassword(tokens[2]);
                accountService.save(FileConstants.ACCOUNT_PATH);
            }
        }
        // View account history
        else if (tokens[0].equals("HISTORY") && account != null) {
            System.out.println("[Player History]");
            for (TrackedStat stat : TrackedStat.values()) {
                System.out.println(String.format("%s: %d", stat.name(), account.getTracker()
                        .getValue(stat)));
            }
        }
        // View available maps
        else if (tokens[0].equals("MAPS")) {
            // Display all maps
            for (int i = 0; i < maps.size(); i++) {
                File file = maps.get(i);
                System.out.println(String.format("Map %d: %s", i + 1, file.getName()));
            }
        }
        // Play/spectate map of choice
        else if (tokens[0].equals("START") && tokens.length >= 2) {
            File mapFile = maps.get(Integer.parseInt(tokens[1])-1);

            // If there is no account, then we are spectating map
            if (account == null) {
                try {
                    map = CSVLoader.loadMap(new Scanner(mapFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                state = State.SPECTATE;
            }
            // There is an account, so we start new game
            else if (player == null) {
                player = MenuController.requestPlayer();
                game = GameLoader.loadNewGame(mapFile, player);
                state = State.NEW;
            }
        }
        // Join endless game (creating it if it doesn't exist)
        else if (tokens[0].equals("ENDLESS")) {
            // Ask player for their name and description
            player = MenuController.requestPlayer();
            state = State.ENDLESS;
        }
        // Continue from a save
        else if (tokens[0].equals("CONTINUE") && account != null) {
            File file = new File(FileConstants.SAVE_FOLDER_PATH + account.getUsername() +
                    "game." + GameFormat.CSV);
            game = GameLoader.loadGame(file);
            state = State.CONTINUE;
        }
    }

    public Controller run() {
        reset();
        accountService.save(FileConstants.ACCOUNT_PATH);
        while (true) {
            // Prints out different menus depending on whether user is logged in
            System.out.println(account == null ? GUEST_MENU_TXT : USER_MENU_TXT);

            // Grabs user inputs and processes
            String input = scanner.nextLine();
            processInput(input);

            // Changes controller once we are moving out of the menu
            if (state == State.CONTINUE || state == State.NEW) {
                return new GameController(account, game);
            }
            else if (state == State.SPECTATE) {
                return new SpectatorController(map);
            }
            else if (state == State.ENDLESS) {
                game = new EndlessGame(player);
                return new GameController(account, game);
            }
        }
    }

    public void reset() {
        player = null;
        map = null;
        state = null;
    }
}
