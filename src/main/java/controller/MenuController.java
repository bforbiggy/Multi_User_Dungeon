package controller;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import model.EndlessGame;
import model.Game;
import model.entities.Player;
import model.env.Map;
import persistence.FileConstants;
import persistence.accounts.*;
import util.CSVLoader;
import util.TextLoader;

public class MenuController extends Controller {
    private enum State {
        NEW, CONTINUE, ENDLESS, SPECTATE
    }

    private static final String GUEST_MENU_TXT = TextLoader.loadText(FileConstants.ASSETS_PATH + "guestMenu.txt");
    private static final String USER_MENU_TXT = TextLoader.loadText(FileConstants.ASSETS_PATH + "userMenu.txt");

    private AccountService accountService;
    private List<File> maps;

    private Account account;
    private Player player;
    private Map map;

    private State state;

    public MenuController() {
        // Loads in all accounts
        accountService = AccountService.load(FileConstants.ACCOUNT_PATH);

        // Loads in map files
        File targetFolder = new File(FileConstants.ASSETS_PATH);
        FilenameFilter filter = (file, name)->{return name.endsWith(".map");};
        maps = Arrays.asList(targetFolder.listFiles(filter));
    }

    public void processInput(String input) {
        String[] tokens = input.split(" ");
        tokens[0] = tokens[0].toUpperCase();
        if (tokens[0].equals("LOGIN") && tokens.length >= 3) {
            account = accountService.authenticate(tokens[1], tokens[2]);

            // Authentication success: Autoloads game in progress (if available)
            if (account != null) {
                System.out.println("Successfully logged in!");
            }
            // Authentication fail: Inform user
            else {
                System.out.println("Invalid username/password combination!");
            }
        } else if (tokens[0].equals("REGISTER") && tokens.length >= 2) {
            account = new Account(tokens[1], tokens[2]);
            if(accountService.addAccount(account)){
                accountService.save(FileConstants.ACCOUNT_PATH);
            }
            else{
                account = null;
            }
        } else if (tokens[0].equals("HISTORY") && account != null) {
            System.out.println("[Player History]");
            for(AccountStat stat : account.getKeySet()){
                System.out.println(String.format("%s: %d", AccountStat.enumToString(stat), account.getData(stat)));
            }
        } 
        else if (tokens[0].equals("MAPS")) {
            // Display all maps
            for (int i = 0; i < maps.size(); i++) {
                File file = maps.get(i);
                System.out.println(String.format("Map %d: %s", i + 1, file.getName()));
            }
        } else if (tokens[0].equals("START") && tokens.length >= 2) {
            // Ask player for their name and description if unavailable
            if (account != null && player == null) {
                System.out.println("You chose to start a new adventure\nPlease give your Player's name:");
                String name = scanner.nextLine();
                System.out.println("Please give a basic description about them: ");
                String description = scanner.nextLine();
                this.player = new Player(name, description, Player.DEFAULT_STATS.copy());
            }

            map = CSVLoader.loadMap(player, FileConstants.ASSETS_PATH + "map" + tokens[1] + ".map");
            System.out.println("Successfully loaded map" + tokens[1] + ".map!");

            if (account == null)
                state = State.SPECTATE;
            else
                state = State.NEW;
        } else if (tokens[0].equals("ENDLESS")) {
            // Ask player for their name and description
            System.out.println("You chose to start a new adventure\nPlease give your Player's name:");
            String name = scanner.nextLine();
            System.out.println("Please give a basic description about them: ");
            String description = scanner.nextLine();
            this.player = new Player(name, description, Player.DEFAULT_STATS.copy());
            state = State.ENDLESS;
        } else if (tokens[0].equals("CONTINUE") && account != null) {
            player = CSVLoader.loadPlayer(FileConstants.SAVE_FOLDER_PATH + account.getUsername() + "player.csv");
            map = CSVLoader.loadMap(player, FileConstants.SAVE_FOLDER_PATH + account.getUsername() + "map.csv");
            state = State.CONTINUE;
        }
    }

    public Controller run() {
        player = null;
        map = null;
        state = null;
        accountService.save(FileConstants.ACCOUNT_PATH);
        while (true) {
            // Prints out different menus depending on whether user is logged in
            System.out.println(account == null ? GUEST_MENU_TXT : USER_MENU_TXT);

            // Grabs user inputs and processes
            String input = scanner.nextLine();
            processInput(input);

            // Changes controller once we are moving out of the menu
            if (state == State.CONTINUE || state == State.NEW) {
                Game game = new Game(player, map);
                return new GameController(account, game);
            } else if (state == State.SPECTATE) {
                return new SpectatorController(map);
            } else if (state == State.ENDLESS) {
                EndlessGame game = new EndlessGame(player);
                return new GameController(account, game);
            }
        }
    }
}
