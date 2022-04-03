package controller;

import java.util.Scanner;

import model.Game;
import model.Statistic;
import model.entities.Player;
import model.env.Location;
import model.env.Map;
import model.items.EquipTag;
import persistence.FileConstants;
import persistence.accounts.Account;
import util.*;

import static java.lang.Integer.parseInt;

public class GameController extends Controller {
    private Scanner scanner = new Scanner(System.in);
    public static final String TITLE_SCREEN_STRING = TextLoader.loadText(FileConstants.TITLE_SCREEN_PATH);
    public static final String CONTROLS_STRING = TextLoader.loadText(FileConstants.CONTROLS_PATH);

    private Object view;
    private Game game;
    private Player player = null;
    private Map map = null;

    private Account account;

    public GameController(Account account, Game game) {
        this.account = account;
        this.game = game;
        this.player = game.getPlayer();
        this.map = game.getMap();
        view = game.getCurrRoom();

        // Prints out game start message
        System.out.println(GameController.TITLE_SCREEN_STRING);
        System.out.println("Welcome " + player.getName() + ", prepare to start a grand adventure!");
    }

    public void processInput(String input) {
        input = input.toUpperCase();
        String[] tokens = input.split(" ");

        // Display controls
        if (tokens[0].equals("CONTROLS")) {
            System.out.println(CONTROLS_STRING);
        }
        // Save the game
        else if (tokens[0].equals("SAVE")) {
            CSVSaver.savePlayer(player, FileConstants.SAVE_FOLDER_PATH + account.getUsername() + "player.csv");
            CSVSaver.saveMap(map, FileConstants.SAVE_FOLDER_PATH + account.getUsername() + "map.csv");
            System.out.println("Game saved!");
        }
        // Use exit
        else if (tokens[0].equals("EXIT")) {
            Location playerLoc = player.getLocation();
            // Look for nearby exits to use, using it if found
            boolean breakLoop = false;
            for (int x = playerLoc.getX() - 1; x <= playerLoc.getX() + 1 && !breakLoop; x++) {
                for (int y = playerLoc.getY() - 1; y <= playerLoc.getY() + 1 && !breakLoop; y++) {
                    breakLoop = game.useExit(x, y);
                }
            }
            view = game.getCurrRoom();
        }
        // Movements by direction
        else if (tokens[0].equals("W")) {
            Location playerLoc = player.getLocation();
            game.doMove(playerLoc.getX(), playerLoc.getY() - 1);
        } else if (tokens[0].equals("A")) {
            Location playerLoc = player.getLocation();
            game.doMove(playerLoc.getX() - 1, playerLoc.getY());
        } else if (tokens[0].equals("S")) {
            Location playerLoc = player.getLocation();
            game.doMove(playerLoc.getX(), playerLoc.getY() + 1);
        } else if (tokens[0].equals("D")) {
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
        else if (tokens[0].startsWith("EQUIP")) {
            game.equipItem(parseInt(tokens[1]) - 1, parseInt(tokens[2]) - 1);
        }
        // Unequip item
        else if (tokens[0].startsWith("UNEQUIP")) {
            game.unequipItem(EquipTag.valueOf(tokens[1]));
        }
        // Consume item
        else if (tokens[0].startsWith("C")) {
            game.consumeItem(parseInt(tokens[1]) - 1, parseInt(tokens[2]) - 1);
        }
        // Swap Bags
        else if (tokens[0].startsWith("SWAP")) {
            game.swapBags(parseInt(tokens[1]), parseInt(tokens[2]) - 1, parseInt(tokens[3]) - 1);
        }
        // Destroy item
        else if (tokens[0].equals("DESTROY"))
            game.destroyItem(parseInt(tokens[1]) - 1, parseInt(tokens[2]) - 1);
        // Return to room view
        else if (tokens[0].startsWith("R") || tokens[0].startsWith("B"))
            view = game.getCurrRoom();
        // Finish game
        else if (tokens[0].equals("FINISH"))
            game.finishGame();
        else
            System.out.println("You entered an incorrect input! Please try again.");
    }

    public Controller run() {
        while (true) {
            // Display the current view then read input
            System.out.println("Enter controls for controls!");
            System.out.println(view.toString());

            String input = scanner.nextLine();

            // TODO: Finished games should be deleted, endless games should be saved

            // Game has been won
            if (game.getGameState() == Game.GameState.VICTORY) {
                System.out.println("Congratulations, you beat the game!");
                account.addToData(Statistic.GAMES_PLAYED, 1);
                return null;
            }
            // Game has been lost
            else if (game.getGameState() == Game.GameState.LOSS) {
                System.out.println("You died...");
                account.addToData(Statistic.GAMES_PLAYED, 1);
                account.addToData(Statistic.LIVES_LOST, 1);
                return null;
            }

            processInput(input);
        }
    }
}