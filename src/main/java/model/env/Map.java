package model.env;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import model.Originator;
import model.entities.*;
import util.MapGenerator;

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
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            Element roomElem = room.createMemento(doc);
            roomElem.setAttribute("id", Integer.toString(i));
            map.appendChild(roomElem);
        }
        return map;
    }

    public static Map convertMemento(Element memento) {
        Boolean isRandom = Boolean.valueOf(memento.getAttribute("random"));
        if (isRandom != null && isRandom)
            return MapGenerator.generateMap();

        Map map = new Map();
        ArrayList<Room> rooms = map.rooms;
        ArrayList<Exit> exits = new ArrayList<>();

        // Iterates throgh all map node's children nodes
        NodeList mapNodes = memento.getChildNodes();
        for (int i = 0; i < mapNodes.getLength(); i++) {
            Node node = mapNodes.item(i);
            if (node instanceof Element element) {
                // Load room node as room
                Room room = Room.convertMemento(element);
                rooms.add(room);

                // If player is in this room, update currRoom to match
                if (map.currRoom == null) {
                    for (Entity entity : room.getEntities())
                        if (entity instanceof Player)
                            map.currRoom = room;
                }

                // Add room's exits to exits list
                for (Tile tile : room.getNeighbors().values())
                    if (tile.content instanceof Exit exit)
                        exits.add(exit);
            }
        }

        connectRooms(exits);
        return map;
    }
}
