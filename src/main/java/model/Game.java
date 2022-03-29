package model;

import java.util.ArrayList;

import model.entities.*;
import model.env.*;
import model.events.*;
import model.items.*;
import model.items.Equippable.Equip_Tag;

// This class acts as model-view AND controller AND view
public class Game implements PlayerTurnEndListener {
    public enum GameState {
        ONGOING,
        VICTORY,
        LOSS
    }

    protected GameState gameState = GameState.ONGOING;

    protected Player player;
    protected Inventory inventory;
    protected Map map;

    protected DayCycle dayCycle;
    protected PlayerTurnEnd playerTurnEnd;

    protected Game() {
    }

    /**
     * Initialization process for the game.
     * 
     * @param player Player
     * @param map    Map
     */
    public Game(Player player, Map map) {
        this.player = player;
        this.inventory = this.player.getInventory();
        this.map = map;

        dayCycle = new DayCycle();
        playerTurnEnd = new PlayerTurnEnd(this);
        playerTurnEnd.addListener(this);

        roomChange(map.currRoom);
    }

    public void onPlayerTurnEnd(PlayerTurnEnd playerTurnEnd) {
        if (player.isDead()) {
            gameState = GameState.LOSS;

            // Convert player into a corpse
            NPC corpse = new NPC(player.getName(), player.getDescription(), new Stats(0, 0, 0), true, new Inventory(1));
            corpse.getInventory().addItem(Bag.INFINITE_BAG.copy());

            // Transfer all items to corpse
            ArrayList<Item> itemsLeft = new ArrayList<Item>();
            for (Bag bag : player.getInventory().bags)
                for (Item item : bag.items)
                    itemsLeft.add(item);
            for (Item item : itemsLeft)
                Inventory.TransferItem(player.getInventory(), corpse.getInventory(), item);

            // Replace player at tile with corpse
            Tile tile = map.currRoom.getTileAtLocation(player.getLocation());
            corpse.setLocation(player.getLocation());
            tile.setContent(corpse);
            tile.setOccupant(null);
        }
    }

    /**
     * Performs the necessary room changing operations.
     * This updates npc subscriptions to the day/night cycle/.
     * The room monitoring player movements is also changed.
     * 
     * @param newRoom
     */
    protected void roomChange(Room newRoom) {
        dayCycle.removeAllListeners();

        // Remove old room & its npcs from appropriate events
        playerTurnEnd.removeListener(map.currRoom);
        for (NPC npc : map.currRoom.getNPCs())
            playerTurnEnd.removeListener(npc);

        // Add new room and its npcs to appropriate events
        playerTurnEnd.addListener(newRoom);
        for (NPC npc : newRoom.getNPCs()) {
            dayCycle.addListener(npc);
            playerTurnEnd.addListener(npc);
        }
    }

    /**
     * Attempts to use a nearby exit.
     * 
     * @param x x location of exit
     * @param y y location of exit
     * @return whether or not an exit was successfully used
     */
    public boolean useExit(int x, int y) {
        Room room = map.currRoom;
        Tile tile = room.getTileAtLocation(new Location(x, y));
        if (tile != null && tile.content instanceof Exit exit) {
            roomChange(exit.getOtherRoom());
            map.entityUseExit(player, exit);
            return true;
        }
        return false;
    }

    /**
     * Given a x,y location, attempts to move player to destination.
     * Fails if location is out of bounds or invalid player movement.
     * 
     * @param x x
     * @param y y
     */
    public void doMove(int x, int y) {
        Room currRoom = map.currRoom;
        Location dest = new Location(x, y);
        if (!currRoom.inBounds(dest))
            return;

        // If a move is valid, make that move
        Tile destTile = currRoom.getTileAtLocation(dest);
        if (player.validateMove(player.getLocation(), destTile)) {
            // Clear previous tile and move player to new tile
            currRoom.moveEntity(dest, player);
        }

        playerTurnEnd.notifyAllListeners();
    }

    /**
     * Given a x,y location, attempts to attack destination.
     * Fails if location has no living npc.
     * 
     * @param x x
     * @param y y
     */
    public void doAttack(int x, int y) {
        Room currRoom = map.currRoom;
        Location dest = new Location(x, y);
        Tile destTile = currRoom.getTileAtLocation(dest);

        // If there is an attackable entity at target location, attack it
        Object occupant = destTile.occupant;
        if (occupant != null && occupant instanceof NPC target) {
            int damageDealt = player.dealDamage(target);
            System.out.println("You dealt " + damageDealt + " damage!");

            // If target dies, perform appropriate death actions
            if (target.isDead()) {
                dayCycle.removeListener(target);
                currRoom.setContent(dest, target);
                currRoom.setOccupant(dest, null);
                System.out.println(target.getName() + " has been killed!");
            }
        }

        playerTurnEnd.notifyAllListeners();
    }

    /**
     * Attempts to return the inventory of whatever is on current location.
     * Fails if there is nothing at player location with a viewable inventory.
     */
    public Inventory viewLoot() {
        Room currRoom = map.currRoom;
        Tile tile = currRoom.getTileAtLocation(player.getLocation());

        // If there is an lootable object at target location, open its inventory
        if (tile.content instanceof Chest chest) {
            Inventory inv = chest.getInventory();
            inventory.gold += inv.gold;
            inv.gold = 0;
            return inv;
        } else if (tile.content instanceof Entity entity) {
            Inventory inv = entity.getInventory();
            inventory.gold += inv.gold;
            inv.gold = 0;
            return inv;
        }
        return null;
    }

    /**
     * Attempts to loot the current inventory the player is on.
     * Fails if there is nothing at player location with a viewable inventory.
     * Also fails if the bag/item index is invalid.
     * 
     * @param bagIndex  the bag containing target item
     * @param itemIndex the item index of target item in bag
     */
    public void doLoot(int bagIndex, int itemIndex) {
        Inventory targetInv = null;

        // Access inventory of current tile contents
        Room currRoom = map.currRoom;
        Tile tile = currRoom.getTileAtLocation(player.getLocation());
        if (tile.content instanceof Chest chest) {
            targetInv = chest.getInventory();
        } else if (tile.content instanceof Entity entity && tile.content != player) {
            targetInv = entity.getInventory();
        }

        // If inventory could be accessed, loot gold and item if possible
        if (targetInv != null) {
            inventory.gold += targetInv.gold;
            targetInv.gold = 0;

            Item item = targetInv.getItem(bagIndex, itemIndex);
            if (item != null)
                Inventory.TransferItem(targetInv, player.getInventory(), item);
        }
    }

    /**
     * Attempts to view the shop items
     * 
     * @return the array of items in shop
     */
    public Inventory viewShop() {
        Tile tile = map.currRoom.getTileAtLocation(player.getLocation());
        if (tile.content instanceof Merchant merchant) {
            return merchant.getInventory();
        }
        return null;
    }

    /**
     * Attempts to buy an item from a shop.
     * Will not work if there are enemies remaining
     * 
     * @param itemIndex the item to attempt to buy
     * @return the item that was bought
     */
    public Item doBuy(int itemIndex) {
        Tile tile = map.currRoom.getTileAtLocation(player.getLocation());
        if (tile.content instanceof Merchant merchant) {
            return merchant.buyItem(inventory, itemIndex);
        }
        return null;
    }

    /**
     * Attempts to buy an item from a shop.
     * Will not work if there are enemies remaining
     * 
     * @param bagIndex  the bag containing target item
     * @param itemIndex the item index of target item in bag
     * @return the item that was sold
     */
    public boolean doSell(int bagIndex, int itemIndex) {
        Item item = inventory.getItem(bagIndex, itemIndex);
        Tile tile = map.currRoom.getTileAtLocation(player.getLocation());
        if (tile.content instanceof Merchant merchant) {
            merchant.sellItem(inventory, item);
            return true;
        }
        return false;
    }

    public boolean equipItem(int bagIndex, int itemIndex) {
        Item item = inventory.getItem(bagIndex, itemIndex);
        if (item instanceof Equippable equippable) {
            inventory.equipItem(player, equippable);
            return true;
        }
        return false;
    }

    public void unequipItem(Equip_Tag unequipTag) {
        inventory.unequipItem(player, unequipTag);
    }

    public void consumeItem(int bagIndex, int itemIndex) {
        Item item = inventory.getItem(bagIndex, itemIndex);
        if (item instanceof Consumable consumable)
            inventory.consumeItem(playerTurnEnd, player, consumable);
    }

    public boolean destroyItem(int bagIndex, int itemIndex) {
        return inventory.removeItem(bagIndex, itemIndex);
    }

    public void swapBags(int oldBagIndex, int bagIndex, int itemIndex) {
        Item item = inventory.getItem(bagIndex, itemIndex);
        if (item instanceof Bag newBag) {
            Bag oldBag = inventory.bags.get(oldBagIndex);
            Bag.swapItems(oldBag, newBag);
        }
    }

    public void finishGame() {
        if (map.currRoom.getType() == 2) {
            gameState = GameState.VICTORY;
        }
    }

    public Map getMap() {
        return map;
    }

    public Room getCurrRoom() {
        return map.currRoom;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Player getPlayer() {
        return player;
    }
}
