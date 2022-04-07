package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import model.entities.NPC;
import model.entities.Player;
import model.env.*;

public class MapGenerator {

    public static ArrayList<Direction> getFreeDirections(Room room) {
        ArrayList<Direction> freeDirs = new ArrayList<Direction>();
        Collection<Direction> collection = room.getNeighbors().keySet();
        for (Direction dir : Direction.values()) {
            if (!collection.contains(dir))
                freeDirs.add(dir);
        }
        return freeDirs;
    }

    private static Random randy = new Random();

    /**
     * Given a room and a wall, choose a random location on said wall.
     * 
     * @param room The room to choose a wall location for.
     * @param dir  Wall direction relative to center
     * @return Location of a potential exit slot on that wall.
     */
    public static Location getFreeExitLoc(Room room, Direction dir) {
        if (dir == Direction.NORTH)
            return new Location(randy.nextInt(room.getWidth() - 2) + 1, 0);
        else if (dir == Direction.SOUTH)
            return new Location(randy.nextInt(room.getWidth() - 2) + 1, room.getHeight() - 1);
        else if (dir == Direction.WEST)
            return new Location(0, randy.nextInt(room.getHeight() - 2) + 1);
        else if (dir == Direction.EAST)
            return new Location(room.getWidth() - 1, randy.nextInt(room.getHeight() - 2) + 1);
        else
            return null;
    }

    private static void connectRooms(Room start, Room end, int exitId, Direction dirToTarget) {
        // Connect start room to end room
        Exit startExit = new Exit(exitId);
        Location startLoc = getFreeExitLoc(start, dirToTarget);
        startExit.setCurRoom(start);
        startExit.connectRoom(end);
        start.setContent(startLoc, startExit);
        
        // Use up wall from start room
        start.getNeighbors().put(dirToTarget, start.getTileAtLocation(startLoc));

        // Connect end room to start room
        Exit endExit = new Exit(exitId);
        Location endLoc = getFreeExitLoc(end, dirToTarget.getOpposite());
        endExit.setCurRoom(end);
        endExit.connectRoom(start);
        end.setContent(endLoc, endExit);

        // Use up wall from end room
        end.getNeighbors().put(dirToTarget.getOpposite(), end.getTileAtLocation(endLoc));
    }

    /**
     * Given blank exitless rooms, connect the rooms.
     * This is random, but each room will always have at least one connection.
     * 
     * @param rooms A list of blank rooms
     */
    private static void generateAllRoomConnections(ArrayList<Room> rooms) {
        int exitId = 1;

        // List of rooms that are added in the map and can hold extra connections
        ArrayList<Room> available = new ArrayList<Room>();
        available.add(rooms.get(0));

        // List of rooms that are unconnected and aren't in the map yet
        LinkedList<Room> remainingRooms = new LinkedList<Room>();
        for (int i = 1; i < rooms.size(); i++)
            remainingRooms.add(rooms.get(i));

        // While there are rooms left, connect room to next random available room
        while (!remainingRooms.isEmpty()) {
            Room from = available.get(randy.nextInt(available.size()));
            Room to = remainingRooms.pop();
            available.add(to);

            // Select next available wall from room
            ArrayList<Direction> freeDirs = getFreeDirections(from);
            Direction dir = freeDirs.remove(randy.nextInt(freeDirs.size()));
            if (freeDirs.isEmpty())
                available.remove(from);

            // Connect the two rooms
            connectRooms(from, to, exitId, dir);

            exitId++;
        }
    }

    /**
     * Given a room, procedurally add things to the room. Avoids overriding existing objects.
     * This includes all items that belong in rooms (Chest, NPCs, Traps, etc.)
     * 
     * @param room Blank room
     */
    public static void generateRoomContents(Room room) {
        double frequency = 6.0;
        PerlinNoise2D.resetPermutations();
        Tile[][] tiles = room.getTiles();
        for (int y = 0; y < room.getHeight(); y++) {
            for (int x = 0; x < room.getWidth(); x++) {
                Tile tile = tiles[y][x];
                Location loc = tile.getLocation();
                double val = PerlinNoise2D.noise(frequency * (x + 1) / room.getWidth(),
                        frequency * (y + 1) / room.getHeight());
                if (tile.content == null && tile.occupant == null) {
                    // Place world object here
                    if (val >= 0.3) {
                        double objectType = randy.nextDouble();
                        // Place NPC
                        if (objectType <= 0.3) {
                            NPC npc = NPC.generateNPC();
                            npc.setLocation(loc);
                            room.setOccupant(loc, npc);
                        }
                        // Place Chest
                        else if (objectType <= 0.45) {
                            Chest chest = new Chest(Chest.generateLoot());
                            room.setContent(loc, chest);
                        }
                        // Place Trap
                        else {
                            room.setContent(loc, new Trap());
                        }
                    }
                    // Obstacle
                    else if (val >= 0.1)
                        room.setOccupant(loc, new Obstacle());
                }
            }
        }
    }

    /**
     * This function generates an entire map from scratch.
     * This includes all rooms in the map.
     * 
     * @param player The player
     * @return An entirely procedurally generated map
     */
    public static Map generateMap(Player player) {
        Map map = new Map();

        // Create random amount of empty rooms
        ArrayList<Room> rooms = map.rooms;
        for (int roomCount = 5 + randy.nextInt(5); roomCount != 0; roomCount--) {
            Room room = new Room(3 + randy.nextInt(6), 3 + randy.nextInt(6), 0);
            rooms.add(room);
        }
        rooms.get(0).setType(1); // Set start room
        rooms.get(rooms.size() - 1).setType(2); // Set end room

        // Connect the rooms
        generateAllRoomConnections(rooms);

        // Spawn player into center of map
        Room start = rooms.get(0);
        start.setOccupant(new Location(start.getWidth() / 2, start.getHeight() / 2), player);
        map.currRoom = start;

        // Generate contents of each room
        for (Room room : rooms)
            generateRoomContents(room);

        return map;
    }
}
