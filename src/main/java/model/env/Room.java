package model.env;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import model.GameObject;
import model.entities.*;
import model.events.PlayerTurnEnd;
import model.events.PlayerTurnEndListener;
import util.Originator;

public class Room implements PlayerTurnEndListener, Originator {
    private static final String[] ROOM_TYPES = {"library", "closet", "hall", "bedroom", "cave", "ruin"};
    private EnumMap<Direction, Tile> neighbors = new EnumMap<>(Direction.class);
    public HashSet<Entity> entities = new HashSet<>();

    private Tile[][] tiles;
    private int height;
    private int width;

    /** 0 = default, 1 = start, 2 = exit */
    private int type;
    private String desc;

    // Generates an empty room given a room size, room sizes are squared
    public Room(int height, int width, int type) {
        this.height = height;
        this.width = width;
        this.type = type;
        tiles = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Location loc = new Location(x, y);
                Tile tile = new Tile(loc);
                tiles[y][x] = tile;
            }
        }
    }

    /**
     * Given a location, this sets the corresponding tile's content to the object.
     * 
     * @param location the location of the tile
     * @param occupant the object to set as the tile's content
     */
    public void setContent(Location location, GameObject content) {
        Tile tile = getTileAtLocation(location);
        tile.content = content;
    }

    /**
     * Given a location, this sets the corresponding tile's occupant to the object. If the occupant is an entity, this will also update the entity's
     * location
     * 
     * @param location the location of the tile
     * @param occupant the object to set as the tile's occupant
     */
    public void setOccupant(Location location, GameObject occupant) {
        Tile tile = getTileAtLocation(location);
        tile.occupant = occupant;

        if (occupant instanceof Entity entity) {
            entity.setLocation(location);
            entities.add(entity);
        }
    }

    /**
     * Given a location, this sets an obj to the corresponding tile this will override whatever was previously on the tile.
     * 
     * @param location location to add obj to
     * @param obj obj to add to tile
     */
    public void forceSetData(Location location, GameObject obj) {
        if(obj instanceof Obstacle){
            setOccupant(location, obj);
        }
        else if(obj instanceof Entity entity){
            if(entity.isDead() || entity instanceof Merchant)
                setContent(location, obj);
            else
                setOccupant(location, obj);
            entity.setLocation(location);
            entities.add(entity);
        }
        else{
            setContent(location, obj);
        }
    }

    /**
     * Given a location, this will move the entity to the destination. This will update the entity's location and clear previous tile's content/occupant.
     * 
     * @param dest destination of the entity
     * @param entity entity to move
     */
    public void moveEntity(Location dest, Entity entity) {
        Tile old = getTileAtLocation(entity.getLocation());
        old.occupant = null;

        Tile tile = getTileAtLocation(dest);
        tile.occupant = entity;
        entity.setLocation(dest);
    }

    /**
     * Performs signifncant game operations. Triggers all events for nearby traps/NPC actions.
     * 
     * @param playerTurnEnd the PlayerTurnEnd event
     */
    public void onPlayerTurnEnd(PlayerTurnEnd playerTurnEnd) {
        Player player = playerTurnEnd.getGame().getPlayer();
        Location playerLoc = player.getLocation();
        Tile playerTile = getTileAtLocation(playerLoc);

        int[] offsets = {-1, 0, 1};
        for (int xOffset : offsets) {
            for (int yOffset : offsets) {
                int x = playerLoc.getX() + xOffset;
                int y = playerLoc.getY() + yOffset;

                Tile tile = getTileAtLocation(new Location(x, y));

                // Trigger trap/Detect trap
                if (tile != null && tile.content instanceof Trap trap) {
                    // Ignore disabled traps
                    if (trap.getDisabled())
                        continue;

                    // Traps we step on or detect are attempted to be disarmed
                    if (tile == playerTile || trap.detectTrap())
                        trap.disarmTrap(player);
                }
            }
        }
    }

    /**
     * Given a location, determine if it is in bounds.
     * 
     * @param loc location to check validity of
     * @return whether or not the location is in bounds
     */
    public boolean inBounds(Location loc) {
        return 0 <= loc.getX() && loc.getX() < width && 0 <= loc.getY() && loc.getY() < height;
    }

    /**
     * Given an exit id, attempt to find the corresponding exit in this room.
     * 
     * @param id the exit id
     * @return Tile containing the exit with the matching id
     */
    public Tile findExit(int id) {
        for (Tile exitTile : neighbors.values())
            if (exitTile.content instanceof Exit exit && exit.getId() == id)
                return exitTile;
        return null;
    }

    public boolean isCleared() {
        for (Tile[] row : tiles)
            for (Tile tile : row)
                if (tile.occupant instanceof NPC)
                    return false;
        return true;
    }


    public Tile getTileAtLocation(Location loc) {
        if (!inBounds(loc))
            return null;
        return tiles[loc.getY()][loc.getX()];
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public int getType() {
        return type;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getDesc() {
        return desc;
    }

    public EnumMap<Direction, Tile> getNeighbors() {
        return neighbors;
    }

    public void setType(int type) {
        this.type = type;
    }

    public HashSet<Entity> getEntities() {
        return entities;
    }

    public String generateDesc() {
        // Determine room type
        double ratio = ((double) height) / width;
        ratio = ratio > 1 ? 1 / ratio : ratio;
        String length;
        if (ratio == 1)
            length = "square";
        else if (ratio <= 0.65)
            length = "long";
        else
            length = "regular";

        // Determine room size
        double area = height * width;
        String size;
        if (area >= 32)
            size = "large";
        else if (area >= 18)
            size = "medium";
        else
            size = "small";

        // Read through room for its contents
        int chestCount = 0;
        int exitCount = 0; // Use hashmap<Tile,String> to store cardinality, can't simply build string as we need to know total number of exits for proper plurality
        ArrayList<String> dead = new ArrayList<String>();
        ArrayList<String> alive = new ArrayList<String>();
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (tile.content instanceof Chest)
                    chestCount++;
                else if (tile.content instanceof Exit)
                    exitCount++;
                else if (tile.content instanceof NPC)
                    dead.add(((NPC) tile.content).toString());
                else if (tile.occupant instanceof NPC)
                    alive.add(((NPC) tile.occupant).toString());
            }
        }

        String npcString;
        if (alive.isEmpty()) {
            if (dead.isEmpty())
                npcString = "The room is empty. ";
            else
                npcString = "There is no monster left alive in this room. ";
        }
        else
            npcString = "With " + dead.size() + " monsters dead, you see the following: a " + String
                    .join(", a", alive) + ". ";

        String output = String.format("A %s %s room has %s exit(s). ", size, length, exitCount);
        if (chestCount != 0)
            output += "There are " + chestCount + " chest(s). ";
        output += npcString;

        this.desc = output;
        return output;
    }

    @Override
    public Element createMemento(Document doc){
        Element room = doc.createElement("room");
        room.setAttribute("type", Integer.toString(type));
        room.setAttribute("height", Integer.toString(height));
        room.setAttribute("width", Integer.toString(width));
        room.setAttribute("desc", desc);

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if(tile.content != null || tile.occupant != null)
                    room.appendChild(tile.createMemento(doc));
            }
        }

        return room;
    }

    @Override
    public Room loadMemento(Element element){
        return this;
    }

    @Override
    public String toString() {
        StringBuilder board = new StringBuilder("   ");

        // Print out column indexes
        for (int x = 0; x < width; x++)
            board.append(String.format(" %d ", x));
        board.append("\n");

        for (int y = 0; y < height; y++) {
            Tile[] tileRow = tiles[y];
            board.append(String.format(" %d ", y));
            for (Tile tile : tileRow) {
                if (tile.occupant != null)
                    board.append(String.format("[%s]", tile.occupant));
                else if (tile.content != null)
                    board.append(String.format("[%s]", tile.content));
                else
                    board.append("[ ]");
            }
            board.append("\n");
        }
        return board.toString();
    }
}
