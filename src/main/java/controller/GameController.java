package controller;

import java.util.Scanner;
import model.EndlessGame;
import model.Game;
import model.entities.Player;
import model.env.Location;
import model.items.EquipTag;
import persistence.FileConstants;
import persistence.accounts.Account;
import util.*;

import static java.lang.Integer.parseInt;
import java.io.File;
import java.io.FilenameFilter;

public class GameController extends Controller {
    private Scanner scanner = new Scanner(System.in);
    public static final String TITLE_SCREEN_STRING = TextLoader.loadText(
            FileConstants.TITLE_SCREEN_PATH);
    public static final String CONTROLS_STRING = TextLoader.loadText(FileConstants.CONTROLS_PATH);

    private Object view;
    private Game game;

    private Account account;

    public GameController(Account account, Game game) {
        this.account = account;
        this.game = game;
        view = game.getCurrRoom();

        // Prints out game start message
        System.out.println(GameController.TITLE_SCREEN_STRING);
        System.out.println("Welcome " + game.getPlayer().getName() +
                ", prepare to start a grand adventure!");
    }

    public void processInput(String input) {
        input = input.toUpperCase();
        String[] tokens = input.split(" ");

        // Default values for function
        view = game.getCurrRoom();
        Player player = game.getPlayer();


        // Display controls
        if (tokens[0].equals("CONTROLS")) {
            System.out.println(CONTROLS_STRING);
        }
        // Save the game in specified format
        else if (tokens[0].equals("SAVE") && tokens.length >= 2) {
            GameFormat format = GameFormat.valueOf(tokens[1].toUpperCase());
            if (format != null) {
                if (format == GameFormat.CSV) {
                    File dest = new File(FileConstants.SAVE_FOLDER_PATH + account.getUsername() +
                            "game." + GameFormat.CSV.name());
                    GameSaver.saveGame(game, dest);
                }
                else if(format == GameFormat.XML){
                    File dest = new File(FileConstants.SAVE_FOLDER_PATH + "test.xml");
                    GameSaver.saveGame(game, dest);
                }

                System.out.println("Game saved!");
            }

        }
        // Use exit
        else if (tokens[0].equals("EXIT")) {
            // Use specific exit
            if (tokens.length >= 3) {
                game.useExit(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
            }
            // Otherwise, use closest exit
            else {
                Location playerLoc = player.getLocation();
                boolean breakLoop = false;
                for (int x = playerLoc.getX() - 1; x <= playerLoc.getX() + 1 && !breakLoop; x++) {
                    for (int y = playerLoc.getY() - 1; y <= playerLoc.getY() + 1 &&
                            !breakLoop; y++) {
                        breakLoop = game.useExit(x, y);
                    }
                }

            }
            view = game.getCurrRoom();
        }
        // Movements by direction
        else if (tokens[0].equals("W")) {
            Location playerLoc = player.getLocation();
            game.doMove(playerLoc.getX(), playerLoc.getY() - 1);
        }
        else if (tokens[0].equals("A")) {
            Location playerLoc = player.getLocation();
            game.doMove(playerLoc.getX() - 1, playerLoc.getY());
        }
        else if (tokens[0].equals("S")) {
            Location playerLoc = player.getLocation();
            game.doMove(playerLoc.getX(), playerLoc.getY() + 1);
        }
        else if (tokens[0].equals("D")) {
            Location playerLoc = player.getLocation();
            game.doMove(playerLoc.getX() + 1, playerLoc.getY());
        }
        // Attacking
        else if (tokens[0].startsWith("ATTACK")) {
            game.doAttack(parseInt(tokens[1]), parseInt(tokens[2]));
        }
        // Inventory open (self)
        else if (tokens[0].startsWith("I")) {
            view = player.getInventory();
        }
        // Inventory open (current tile ex. chests, dead npcs)
        else if (tokens[0].startsWith("L")) {
            // If the user specifies an item, try to loot said item
            if (tokens.length >= 3)
                game.doLoot(parseInt(tokens[1]) - 1, parseInt(tokens[2]) - 1);
            else
                view = game.viewLoot();
        }
        // Merchant access/looting
        else if (tokens[0].startsWith("SHOP")) {
            // If the user specifies an item, try to buy said item
            if (tokens.length >= 2)
                game.doBuy(parseInt(tokens[1]) - 1);
            else
                view = game.viewShop();
        }
        // Equip item
        else if (tokens[0].startsWith("EQUIP") && tokens.length >= 3) {
            game.equipItem(parseInt(tokens[1]) - 1, parseInt(tokens[2]) - 1);
        }
        // Unequip item
        else if (tokens[0].startsWith("UNEQUIP") && tokens.length >= 2) {
            game.unequipItem(EquipTag.valueOf(tokens[1]));
        }
        // Consume item
        else if (tokens[0].startsWith("C") && tokens.length >= 3) {
            game.consumeItem(parseInt(tokens[1]) - 1, parseInt(tokens[2]) - 1);
        }
        // Swap Bags
        else if (tokens[0].startsWith("SWAP") && tokens.length >= 4) {
            game.swapBags(parseInt(tokens[1]), parseInt(tokens[2]) - 1, parseInt(tokens[3]) - 1);
        }
        // Destroy item
        else if (tokens[0].equals("DESTROY") && tokens.length >= 3)
            game.destroyItem(parseInt(tokens[1]) - 1, parseInt(tokens[2]) - 1);
        // Return to room view
        else if (tokens[0].startsWith("R") || tokens[0].startsWith("B"))
            view = game.getCurrRoom();
        // Finish game
        else if (tokens[0].equals("FINISH"))
            game.finishGame();
        // Use shrine
        else if (tokens[0].equals("SHRINE") && game instanceof EndlessGame endlessGame) {
            if (endlessGame.useShrine())
                System.out.println("Praying at the shrine, you know death will not be the end.");
        }
        else
            System.out.println("You entered an incorrect input! Please try again.");
    }

    public Controller run() {
        while (true) {
            // Display the current view then read input
            System.out.println("Enter controls for controls!");
            System.out.println(view.toString());

            String input = scanner.nextLine();

            // Game has ended
            if (game.getGameState() != Game.GameState.ONGOING) {
                // Delete the saves (no need to update account stats, menuController autosaves)
                FilenameFilter filter = (file, name) -> name.startsWith(account.getUsername()) &&
                        name.contains("game");
                File saveFolder = new File(FileConstants.SAVE_FOLDER_PATH);
                for (File saveFile : saveFolder.listFiles(filter)) {
                    saveFile.delete();
                }
                return null;
            }

            processInput(input);
        }
    }
}
