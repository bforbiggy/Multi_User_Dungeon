package controller;

import java.util.EnumMap;
import model.Game.GameState;
import model.env.*;

public class SpectatorController extends Controller {
    private Room viewRoom;
    private Map map;
    private boolean quit = false;

    public SpectatorController(Map map) {
        this.map = map;
        viewRoom = this.map.rooms.get(0);
    }

    private void processInput(String input) {
        input = input.toUpperCase();
        EnumMap<Direction, Tile> neighbors = viewRoom.getNeighbors();
        if (input.equals("W")) {
            Tile exitTile = neighbors.get(Direction.NORTH);
            if (exitTile != null && exitTile.content instanceof Exit exit) {
                viewRoom = exit.getOtherRoom();
            }
        } else if (input.equals("A")) {
            Tile exitTile = neighbors.get(Direction.WEST);
            if (exitTile != null && exitTile.content instanceof Exit exit) {
                viewRoom = exit.getOtherRoom();
            }
        } else if (input.equals("S")) {
            Tile exitTile = neighbors.get(Direction.SOUTH);
            if (exitTile != null && exitTile.content instanceof Exit exit) {
                viewRoom = exit.getOtherRoom();
            }
        } else if (input.equals("D")) {
            Tile exitTile = neighbors.get(Direction.EAST);
            if (exitTile != null && exitTile.content instanceof Exit exit) {
                viewRoom = exit.getOtherRoom();
            }
        } else if (input.equals("QUIT")) {
            quit = true;
        } else {
            System.out.println("Invalid input!");
        }
    }

    public Controller run() {
        while (true) {
            System.out.println(viewRoom.toString());
            String input = scanner.nextLine();
            processInput(input);

            if (quit) {
                return null;
            }
        }
    }
}
