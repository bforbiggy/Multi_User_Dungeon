package util;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import model.entities.*;
import model.env.*;
import model.items.*;
import persistence.FileConstants;

public class CSVSaver {
    /**
     * Given a stat object, return an arraylist of all stats in object.
     * 
     * @param stats stat object
     * @return list of all stats
     */
    public static ArrayList<String> statsToString(Stats stats) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(Integer.toString(stats.health));
        list.add(Integer.toString(stats.attack));
        list.add(Integer.toString(stats.defense));
        return list;
    }

    /**
     * Given an item, converts it to the following loadable formats (depending on item type) Normal
     * Items: itemType.name.description.value Equippable Items:
     * itemType.name.description.value.health.attack.defense.equipType Consumable Items:
     * itemType.name.description.value.health.attack.defense.duration Bag Items:
     * itemType.name.description.value.capacity
     * 
     * @param item item to convert to string
     * @return loadable string representation of item
     */
    public static String itemToString(Item item) {
        ArrayList<String> tokens = new ArrayList<String>();
        tokens.add(item.getName());
        tokens.add(item.getDescription());
        tokens.add(Integer.toString(item.getValue()));
        if (item instanceof Bag bag) {
            tokens.add(0, "B");
            tokens.add(Integer.toString(bag.getCapacity()));
        }
        else if (item instanceof Equippable equippable) {
            tokens.add(0, "E");
            tokens.addAll(statsToString(equippable.getStats()));
            tokens.add(equippable.getEquipTag().name());
        }
        else if (item instanceof Consumable consumable) {
            tokens.add(0, "C");
            tokens.addAll(statsToString(consumable.getStats()));
            tokens.add(Integer.toString(consumable.getDuration()));
        }
        else
            tokens.add(0, "I");
        return String.join(FileConstants.PERIOD, tokens);
    }

    /**
     * Given an inventory, convert it to the following format: item1,item2,item3,item4....
     * 
     * @param inventory inventory to convert to string
     * @return string representation of inventory
     */
    public static String invToString(Inventory inventory) {
        String output = "";
        // Add all inventory items
        ArrayList<String> stringItems = new ArrayList<String>();
        for (Bag bag : inventory.bags)
            for (Item item : bag.items)
                stringItems.add(itemToString(item));
        output += String.join(FileConstants.COMMA, stringItems);
        return output;
    }

    /**
     * Given an npc, convert it to the following loadable format:
     * name,description,health,attack,defense,isDiurnal item1, item2, item3, item4
     * 
     * @param npc npc to convert to string
     * @return string representation of npc
     */
    public static String npcToString(NPC npc) {
        // Add npc details
        ArrayList<String> tokens = new ArrayList<String>();
        tokens.add(npc.getName());
        tokens.add(npc.getDescription());
        tokens.addAll(statsToString(npc.getStats()));
        tokens.add(Boolean.toString(npc.getDiurnal()));

        String invString = invToString(npc.getInventory());

        return String.join(FileConstants.COMMA, tokens) + "\n" + invString;
    }

    /**
     * Given a merchant, convert it to the following loadable format:
     * name,description,health,attack,defense item1, item2, item3, item4
     * 
     * @param merchant merchant to convert to string
     * @return string representation of merchant
     */
    public static String merchantToString(Merchant merchant) {
        // Add npc details
        ArrayList<String> tokens = new ArrayList<String>();
        tokens.add(merchant.getName());
        tokens.add(merchant.getDescription());
        tokens.addAll(statsToString(merchant.getStats()));

        ArrayList<String> stringItems = new ArrayList<String>();
        for (Item item : merchant.getInventory().bags.get(0).items)
            stringItems.add(itemToString(item));
        String invString = String.join(FileConstants.COMMA, stringItems);

        return String.join(FileConstants.COMMA, tokens) + "\n" + invString;
    }

    /**
     * Given a player, save the player to the output path. The player is formatted as follows:
     * name,description health,attack,defense equipment1,equipment2,equipment3,equipment4
     * bagItem1,bagItem2,bagItem3.....,bagItem6 item1,item2,item3,item4....
     * 
     * @param player player to convert to string
     * @param outputPath path to save file to
     */
    public static void savePlayer(Player player, String outputPath) {
        try {
            String output = "";
            File outputFile = new File(outputPath);
            outputFile.createNewFile();
            FileWriter writer = new FileWriter(outputFile);

            Inventory inventory = player.getInventory();

            // Get list of equipped items
            ArrayList<String> unequipStrings = new ArrayList<String>();
            for (Equippable equippable : inventory.equipment.values())
                unequipStrings.add(itemToString(equippable));

            // Player stats
            Stats stats = player.getStats();
            String[] playerData = {player.getName(), player.getDescription(), Integer.toString(
                    stats.health),
                    Integer.toString(stats.attack), Integer.toString(stats.defense)};

            // Get all player bags and items
            ArrayList<String> bags = new ArrayList<String>();
            ArrayList<String> items = new ArrayList<String>();
            for (Bag bag : inventory.bags) {
                bags.add(itemToString(bag));
                for (Item item : bag.items)
                    items.add(itemToString(item));
            }

            output += String.join(FileConstants.COMMA, playerData) + "\n";
            output += String.join(FileConstants.COMMA, unequipStrings) + "\n";
            output += String.join(FileConstants.COMMA, bags) + "\n";
            output += String.join(FileConstants.COMMA, items);

            writer.write(output);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Given a tile, converts its contents according to the below formats. If the content contains a
     * "heavy data" object (ex. NPC or Chest), the object is also added to a runningList, with a
     * Iindex replacing said object. (A | is used if a tile contains more than one object)
     * 
     * Blank Tile: 0 Obstacle Tile: O NPC/Chest Tile: Iindex (ex. I5) Trap Tile: Tattack (ex. T100
     * OR TR) Exit Tile: Eid (ex. E5) Player Tile: P
     * 
     * @param tile tile to convert to string
     * @param runningList list to add each heavy data object to
     * @return string representation of tile
     */
    public static String tileToString(Tile tile, ArrayList<Object> runningList) {
        ArrayList<String> objectStrings = new ArrayList<String>();
        for (Object tileData : new Object[] {tile.content, tile.occupant}) {
            if (tileData instanceof Obstacle)
                objectStrings.add("O");
            else if (tileData instanceof Trap trap)
                objectStrings.add("T" + Integer.toString(trap.getAttack()));
            else if (tileData instanceof Exit exit)
                objectStrings.add("E" + exit.getId());
            else if (tileData instanceof Player)
                objectStrings.add("P");
            else if (tileData instanceof Chest || tileData instanceof NPC ||
                    tileData instanceof Merchant) {
                runningList.add(tileData);
                objectStrings.add("I" + runningList.size());
            }
        }

        // If there is nothing on the tile, default representation is 0
        return objectStrings.isEmpty() ? "0" : String.join(FileConstants.VERT_BAR, objectStrings);
    }

    /**
     * Given a room, convert it into the following room format: Room height, Room width, Room Type,
     * Room description Tile data,Tile data,Tile data Tile data,Tile data,Tile data Tile data,Tile
     * data,Tile data Tile data,Tile data,Tile data
     * 
     * @param room room to convert to string
     * @return string representation of room
     */
    public static String roomToString(Room room) {
        Tile[][] tiles = room.getTiles();
        ArrayList<Object> storedObjects = new ArrayList<Object>();

        // Convert all tiles into string
        String roomString = String.format("ROOM%n%d,%d,%d,%s%n", room.getHeight(), room.getWidth(),
                room.getType(),
                room.getDesc());
        for (int h = 0; h < tiles[0].length; h++) {
            for (int w = 0; w < tiles[0].length; w++) {
                roomString += tileToString(tiles[h][w], storedObjects);

                // Add a comma for each tile (other than the last)
                if (w != tiles[0].length - 1)
                    roomString += FileConstants.COMMA;
            }
            roomString += "\n";
        }

        // Convert all objects into string
        String objectsString = "";
        for (Object obj : storedObjects) {
            if (obj instanceof NPC npc) {
                objectsString += "NPC\n";
                objectsString += npcToString(npc) + "\n";
            }
            else if (obj instanceof Chest chest) {
                objectsString += "CHEST\n";
                objectsString += invToString(chest.getInventory()) + "\n";
            }
            else if (obj instanceof Merchant merchant) {
                objectsString += "MERCHANT\n";
                objectsString += merchantToString(merchant) + "\n";
            }
        }

        return objectsString + roomString;
    }

    /**
     * Given a map, save the map to the output path. Each heavy data object is prefixed by its type.
     * (ex. ROOM, CHEST, NPC)
     * 
     * @param map
     * @param outputPath
     */
    public static void saveMap(Map map, String outputPath) {
        try {
            File outputFile = new File(outputPath);
            outputFile.createNewFile();
            FileWriter writer = new FileWriter(outputFile);

            for (Room room : map.rooms) {
                writer.write(roomToString(room));
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
