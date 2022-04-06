package model;

import java.util.EnumMap;
import java.util.Random;
import java.util.Map.Entry;
import model.entities.*;
import model.env.*;
import model.events.*;

import util.MapGenerator;

// Create initial room with exits that have null destinations
// Generate empty room on demand when necessary
public class EndlessGame extends Game {
    private static final Random randy = new Random();

    private static int nextExitID = 0;

    /**
     * Initialization process for the game.
     * 
     * @param player Player
     * @param map Map
     */
    public EndlessGame(Player player) {
        this.player = player;
        this.inventory = this.player.getInventory();

        dayCycle = new DayCycle();
        playerTurnEnd = new PlayerTurnEnd(this);

        // Create endless map starting room
        map = new Map();
        Room room = createRoom();
        map.rooms.add(room);
        map.currRoom = room;

        // Place player in center
        Location center = new Location(room.getWidth() / 2, room.getHeight() / 2);
        room.setOccupant(center, player);
        player.setLocation(center);

        roomChange(map.currRoom);
    }

    private static Room createRoom() {
        Room room = new Room(3 + randy.nextInt(6), 3 + randy.nextInt(6), 0);
        for (Direction dir : Direction.values()) {
            // Generate exit using next available id
            Exit exit = new Exit(nextExitID++);
            exit.setCurRoom(room);

            // Place exit on map
            Location loc = MapGenerator.getFreeExitLoc(room, dir);
            Tile tile = room.getTileAtLocation(loc);
            tile.setContent(exit);
            room.getNeighbors().put(dir, tile);
        }
        return room;
    }

    private Room connectNewRandomRoom(Direction orgDir, Exit orgExit) {
        Room room = createRoom();

        // Create exit-related data
        Direction dir = orgDir.getOpposite();
        Tile exitTile = room.getNeighbors().get(dir);
        Exit exit = (Exit) exitTile.content;

        // Connect rooms together
        orgExit.connectRoom(room);
        exit.setCurRoom(room);
        exit.connectRoom(orgExit.getCurRoom());
        exit.setID(orgExit.getId());

        MapGenerator.generateRoomContents(room);
        return room;
    }

    /**
     * Attempts to use exit at specified location
     * 
     * @param x x location of exit
     * @param y y location of exit
     * @return whether or not an exit was successfully used
     */
    @Override
    public boolean useExit(int x, int y) {
        Room currRoom = map.currRoom;
        Tile tile = currRoom.getTileAtLocation(new Location(x, y));
        if (tile != null && tile.content instanceof Exit exit) {
            // If there is no room, generate new room and connect it
            if (exit.getOtherRoom() == null) {
                EnumMap<Direction, Tile> neighbors = currRoom.getNeighbors();
                for (Entry<Direction, Tile> entry : neighbors.entrySet()) {
                    Direction dir = entry.getKey();
                    Tile exitTile = entry.getValue();

                    Exit targetExit = (Exit) exitTile.content;
                    if (exit.equals(targetExit)) {
                        Room room = connectNewRandomRoom(dir, exit);
                        map.rooms.add(room);
                    }
                }
            }

            roomChange(exit.getOtherRoom());
            map.entityUseExit(player, exit);
            return true;
        }

        return false;
    }
}
