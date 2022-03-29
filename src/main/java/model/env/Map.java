package model.env;

import java.util.ArrayList;

import model.entities.*;

public class Map 
{
    public Room currRoom;
    public ArrayList<Room> rooms = new ArrayList<>();

    /**
     * Upon an entity reaching the exit, perform room change operations.
     * 
     * This includes moving the entity to the exit in the other room,
     * as well as updating the current room the player is in if applicable.
     * 
     * @param entity The entity entering the exit
     * @param exit the exit being used
     */
    public void entityUseExit(Entity entity, Exit exit)
    {
        Room prevRoom = exit.getCurRoom();
        Room nextRoom = exit.getOtherRoom();
        Tile nextTile = nextRoom.findExit(exit.getId());

        prevRoom.setOccupant(entity.getLocation(), null);
        nextRoom.setOccupant(nextTile.getLocation(), entity);

        if(entity instanceof Player)
            currRoom = nextRoom;
    }

    /**
     * Given a list of unconnected exits,
     * connect exit to its exit counterpart.
     */
    public static void connectRooms(ArrayList<Exit> exits)
    {
        for (Exit exit : exits) {
            // If exit has no other room, find other room
            if(exit.getOtherRoom() == null)
            {
                for(Exit other : exits){
                    if(exit != other && exit.getId() == other.getId())
                    {
                        exit.connectRoom(other.getCurRoom());
                        other.connectRoom(exit.getCurRoom());
                        break;
                    }
                }
            }
        }
    }
}
