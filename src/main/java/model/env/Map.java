package model.env;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import model.Originator;
import model.entities.*;
import model.events.DayCycle;
import model.events.PlayerTurnEnd;
import util.MapGenerator;

public class Map implements Originator {
    public Room currRoom;
    public ArrayList<Room> rooms = new ArrayList<>();

    /**
     * Upon an entity reaching the exit, perform room change operations.
     * This moves the entity to the exit in the other room, and updating current room if applicable.
     * (THIS DOES NOT UPDATE LISTENERS)
     * 
     * @param entity The entity entering the exit
     * @param exit the exit being used
     */
    public void entityUseExit(Entity entity, Exit exit) {
        // Move entity to new room
        Room prevRoom = exit.getCurRoom();
        Room nextRoom = exit.getOtherRoom();
        Tile nextTile = nextRoom.findExit(exit.getId());

        prevRoom.setOccupant(entity.getLocation(), null);
        nextRoom.setOccupant(nextTile.getLocation(), entity);

        // Update "current" room when player moves to new room
        if (entity instanceof Player && !entity.isDead()) {
            currRoom = nextRoom;
        }
    }

    /**
     * Performs appropriate room change operations involving npc subscriptions to events.
     * @param dayCycle dayCycle event
     * @param playerTurnEnd playerTurnEnd event
     * @param oldRoom old room
     * @param newRoom new room
     */
    public void roomChange(DayCycle dayCycle, PlayerTurnEnd playerTurnEnd, Room oldRoom,
            Room newRoom) {
        // Perform event operations
        dayCycle.removeAllListeners();

        // Remove old room & its npcs from appropriate events
        if(oldRoom != null){
            playerTurnEnd.removeListener(oldRoom);
            for (Entity target : oldRoom.getEntities())
                if (target instanceof NPC npc)
                    playerTurnEnd.removeListener(npc);
        }
        

        // Add new room and its npcs to appropriate events
        if(newRoom != null){
            playerTurnEnd.addListener(newRoom);
            for (Entity target : newRoom.getEntities()) {
                if (target instanceof NPC npc && !npc.isDead()) {
                    dayCycle.addListener(npc);
                    playerTurnEnd.addListener(npc);
                }
            }
        }
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
