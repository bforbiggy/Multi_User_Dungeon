package controller;

import model.Game.GameState;
import model.env.*;

public class SpectatorController extends Controller {
    private Room viewRoom;
    private Map map;
    private GameState gameState = GameState.ONGOING;

    public SpectatorController(Map map) {
        this.map = map;
        viewRoom = this.map.rooms.get(0);
    }

    private void processInput(String input) {
        input = input.toUpperCase();
        if (input.equals("W")) {
            Exit exit = (Exit) viewRoom.neighbors.get(Direction.NORTH).content;
            if (exit != null) {
                viewRoom = exit.getOtherRoom();
            }
        } else if (input.equals("A")) {
            Exit exit = (Exit) viewRoom.neighbors.get(Direction.WEST).content;
            if (exit != null) {
                viewRoom = exit.getOtherRoom();
            }
        } else if (input.equals("S")) {
            Exit exit = (Exit) viewRoom.neighbors.get(Direction.SOUTH).content;
            if (exit != null) {
                viewRoom = exit.getOtherRoom();
            }
        } else if (input.equals("D")) {
            Exit exit = (Exit) viewRoom.neighbors.get(Direction.EAST).content;
            if (exit != null) {
                viewRoom = exit.getOtherRoom();
            }
        } else if (input.equals("QUIT")) {
            gameState = GameState.VICTORY;
        } else {
            System.out.println("Invalid input!");
        }
    }

    public Controller run() {
        while (true) {
            System.out.println(viewRoom.toString());
            String input = scanner.nextLine();
            processInput(input);

            if (gameState == GameState.VICTORY) {
                return null;
            }
        }
    }
}
