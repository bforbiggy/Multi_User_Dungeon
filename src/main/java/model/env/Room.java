package model.env;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import model.GameObject;
import model.Originator;
import model.entities.*;
import model.events.PlayerTurnEnd;
import model.events.PlayerTurnEndListener;

public class Room implements PlayerTurnEndListener, Originator {
    // private static final String[] ROOM_TYPES = {"library", "closet", "hall", "bedroom", "cave", "ruin"};
    private EnumMap<Direction, Tile> neighbors = new EnumMap<>(Direction.class);
    private HashSet<Entity> entities = new HashSet<>();

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

        if (content instanceof Entity entity) {
            entity.setLocation(location);
            entities.add(entity);
        }

        if (content instanceof Exit exit) {
            exit.setCurRoom(this);
            neighbors.put(Direction.locToDirection(location, width, height), tile);
        }
    }

    /**
     * Given a location, this sets the corresponding tile's occupant to the object. 
     * If the occupant is an entity, this will also update the entity's location
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
     * Given a location, this sets an obj to the corresponding tile. 
     * This will override whatever was previously on the tile.
     * 
     * @param location location to add obj to
     * @param obj obj to add to tile
     */
    public void forceSetData(Location location, GameObject obj) {
        if (obj instanceof Obstacle) {
            setOccupant(location, obj);
        }
        else if (obj instanceof Entity entity) {
            if (entity.isDead() || entity instanceof Merchant)
                setContent(location, obj);
            else
                setOccupant(location, obj);
        }
        else {
            setContent(location, obj);
        }
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

    /**
     * Returns true if there are no LIVING HOSTILE entities in room.
     * 
     * @return whether or not there isn't a living hostile entity
     */
    public boolean isCleared() {
        for (Entity entity : entities)
            if (entity instanceof NPC npc && !npc.isDead())
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
        int area = height * width;
        String size;
        if (area >= 32)
            size = "large";
        else if (area >= 18)
            size = "medium";
        else
            size = "small";

        // Read through room for its contents
        int chestCount = 0;
        ArrayList<String> dead = new ArrayList<String>();
        ArrayList<String> alive = new ArrayList<String>();
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (tile.content instanceof Chest)
                    chestCount++;
                else if (tile.content instanceof NPC npc)
                    dead.add(npc.getName());
                else if (tile.occupant instanceof NPC npc)
                    alive.add(npc.getName());
            }
        }

        String npcString;
        if (alive.isEmpty()) {
            if (dead.isEmpty())
                npcString = "The room has no monsters.";
            else
                npcString = "There is no monster left alive in this room. ";
        }
        else {
            npcString = "With " + dead.size() + " monsters dead, you see the following: ";
            npcString += String.join(", ", "a " + alive);
        }


        String output = String.format("A %s %s room has %s exit(s). ", size, length, neighbors
                .keySet().size());
        if (chestCount != 0)
            output += "There are " + chestCount + " chest(s). ";
        output += npcString;

        this.desc = output;
        return output;
    }

    @Override
    public Element createMemento(Document doc) {
        Element roomElem = doc.createElement("room");
        roomElem.setAttribute("type", Integer.toString(type));
        roomElem.setAttribute("height", Integer.toString(height));
        roomElem.setAttribute("width", Integer.toString(width));
        roomElem.setAttribute("desc", desc);

        // Convert all tiles to elements
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                roomElem.appendChild(tile.createMemento(doc));
            }
        }

        return roomElem;
    }

    public static Room convertMemento(Element element) {
        // Creates template room
        int type = Integer.parseInt(element.getAttribute("type"));
        int width = Integer.parseInt(element.getAttribute("width"));
        int height = Integer.parseInt(element.getAttribute("height"));
        Room room = new Room(height, width, type);

        // Iterates throgh all room node's children nodes
        NodeList roomNodes = element.getChildNodes();
        for (int i = 0; i < roomNodes.getLength(); i++) {
            Node tileNode = roomNodes.item(i);
            if (tileNode instanceof Element tileElem) {
                Location loc = new Location(Integer.parseInt(tileElem.getAttribute("x")), Integer
                        .parseInt(tileElem.getAttribute("y")));
                Tile tile = room.getTileAtLocation(loc);

                // Load data into tile
                tile.loadMemento(tileElem);

                // Set tile data using room methods
                if (tile.occupant != null) {
                    room.setOccupant(loc, tile.occupant);
                }
                if (tile.content != null) {
                    room.setContent(loc, tile.content);
                }
            }
        }

        return room;
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
