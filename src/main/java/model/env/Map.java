package model.env;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import model.entities.*;
import util.Originator;

public class Map implements Originator {
    public Room currRoom;
    public ArrayList<Room> rooms = new ArrayList<>();

    /**
     * Upon an entity reaching the exit, perform room change operations.
     * 
     * This includes moving the entity to the exit in the other room, as well as updating the current room the player is in if applicable.
     * 
     * @param entity The entity entering the exit
     * @param exit the exit being used
     */
    public void entityUseExit(Entity entity, Exit exit) {
        Room prevRoom = exit.getCurRoom();
        Room nextRoom = exit.getOtherRoom();
        Tile nextTile = nextRoom.findExit(exit.getId());

        prevRoom.setOccupant(entity.getLocation(), null);
        nextRoom.setOccupant(nextTile.getLocation(), entity);

        if (entity instanceof Player)
            currRoom = nextRoom;
    }

    /**
     * Given a list of unconnected exits, connect exit to its exit counterpart.
     */
    public static void connectRooms(ArrayList<Exit> exits) {
        for (Exit exit : exits) {
            // If exit has no other room, find other room
            if (exit.getOtherRoom() == null) {
                for (Exit other : exits) {
                    if (exit != other && exit.getId() == other.getId()) {
                        exit.connectRoom(other.getCurRoom());
                        other.connectRoom(exit.getCurRoom());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public Element createMemento(Document doc) {
        Element map = doc.createElement("map");
        for (Room room : rooms) {
            map.appendChild(room.createMemento(doc));
        }
        return map;
    }

    public static Map loadMemento(Element memento) {
        Map map = new Map();
        ArrayList<Room> rooms = map.rooms;

        NodeList mapNodes = memento.getChildNodes();

        // Iterates throgh all map node's children nodes
        for (int i = 0; i < mapNodes.getLength(); i++) {
            Node node = mapNodes.item(i);
            if (node instanceof Element element) {
                // Load room node as room
                Room room = Room.loadMemento(element);
                rooms.add(room);

                // If player is in this room, update currRoom to match
                if (map.currRoom == null) {
                    for (Tile[] row : room.getTiles())
                        for (Tile tile : row)
                            if (tile.occupant instanceof Player)
                                map.currRoom = room;
                }
            }
        }

        return map;
    }
}
