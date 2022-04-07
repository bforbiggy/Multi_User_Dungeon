package model;

import java.util.Random;
import java.util.Map.Entry;
import model.entities.*;
import model.env.*;
import model.events.*;
import model.tracking.StatTracker;
import model.tracking.TrackedStat;
import util.MapGenerator;

// Create initial room with exits that have null destinations
// Generate empty room on demand when necessary
public class EndlessGame extends Game {
    private static final Random randy = new Random();
    private static int nextExitID = 0;

    private int roomsPassed;
    private Shrine lastShrine;

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
        room.setType(1);
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
            room.setContent(loc, exit);
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

        // Generate room contents and shrine for roughly 1-in-10 rooms
        if(randy.nextInt(10) == 0){
            int x = randy.nextInt(room.getWidth()-2)+1;
            int y = randy.nextInt(room.getHeight()-2)+1;
            Shrine shrine = new Shrine();
            room.setContent(new Location(x,y), shrine);
        }
        MapGenerator.generateRoomContents(room);
        return room;
    }

    public void useShrine(){
        // If a shrine can be found, use it to save game state
        Tile tile = map.currRoom.getTileAtLocation(player.getLocation());
        if(tile.content instanceof Shrine shrine){
            shrine.use(this);
            lastShrine = shrine;
        }
    }
    
    @Override
    public void onPlayerTurnEnd(PlayerTurnEnd playerTurnEnd){
        if(player.isDead()){
            // If shrine was used, return game to saved state
            if(lastShrine != null)
                loadMemento(lastShrine.getGameMemento());
            // Otherwise, resume normal game logic
            else
                super.onPlayerTurnEnd(playerTurnEnd);
        }
    }

    @Override
    public void finishGame() {
        // Endless Game can end at start room
        if (map.currRoom.getType() != 0) {
            gameState = GameState.VICTORY;
            StatTracker.getTracker().addValue(TrackedStat.GAMES_PLAYED, 1);
        }
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
                // Find the direction associated with exit
                for (Entry<Direction, Tile> entry : currRoom.getNeighbors().entrySet()) {
                    Direction dir = entry.getKey();
                    Tile exitTile = entry.getValue();
                    Exit targetExit = (Exit) exitTile.content;

                    // If direction is found, create room connected from that direction
                    if (exit.equals(targetExit)) {
                        Room room = connectNewRandomRoom(dir, exit);
                        map.rooms.add(room);
                    }
                }
            }

            // Move player to new room
            roomChange(exit.getOtherRoom());
            map.entityUseExit(player, exit);

            // Respawn dead enemies in all rooms
            roomsPassed++;
            if (roomsPassed % 5 == 0) {
                for (Room room : map.rooms) {
                    for(Entity entity : room.getEntities()){
                        // If NPC is dead, "respawn" NPC by healing it so it is alive again
                        if(entity.isDead() && entity instanceof NPC npc){
                            entity.getStats().health = randy.nextInt(151-50)+50;
                            playerTurnEnd.addListener(npc);
                        }
                    }
                }
            }
            return true;
        }

        return false;
    }
}
