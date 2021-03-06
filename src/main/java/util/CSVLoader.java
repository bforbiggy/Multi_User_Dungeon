package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Scanner;

import static java.lang.Integer.parseInt;
import model.Game;
import model.GameObject;
import model.entities.*;
import model.env.*;
import model.items.*;
import persistence.FileConstants;

public class CSVLoader {
    /**
     * Given a string representation of an item, converts it into an item.
     * 
     * Normal Items: itemType.name.description.value Equippable Items: itemType.name.description.value.health.attack.defense.equipType Consumable Items:
     * itemType.name.description.value.health.attack.defense.duration Bag Items: itemType.name.description.value.capacity
     * 
     * @param item item to convert to string
     * @return loadable string representation of item
     */
    public static Item stringToItem(String input) {
        String[] tokens = input.split(FileConstants.PERIOD_REGEX);
        // Item conversion
        if (tokens[0].startsWith("I")) {
            if (tokens.length == 1)
                return Item.generateItem();
            return new Item(tokens[1], tokens[2], parseInt(tokens[3]));
        }
        // Consumable conversion
        else if (tokens[0].startsWith("C")) {
            if (tokens.length == 1)
                return Consumable.generateItem();
            Stats stats = new Stats(parseInt(tokens[4]), parseInt(tokens[5]), parseInt(tokens[6]));
            int duration = parseInt(tokens[7]);
            return new Consumable(tokens[1], tokens[2], parseInt(tokens[3]), stats, duration);
        }
        // Equippable conversion
        else if (tokens[0].startsWith("E")) {
            if (tokens.length == 1)
                return Equippable.generateItem();
            Stats stats = new Stats(parseInt(tokens[4]), parseInt(tokens[5]), parseInt(tokens[6]));
            EquipTag tag = EquipTag.valueOf(tokens[7]);
            return new Equippable(tokens[1], tokens[2], parseInt(tokens[3]), stats, tag);
        }
        // Bag conversion
        else if (tokens[0].startsWith("B")) {
            if (tokens.length == 1)
                return Bag.generateItem();
            int capacity = parseInt(tokens[4]);
            return new Bag(tokens[1], tokens[2], parseInt(tokens[3]), capacity);
        }
        return null;
    }

    /**
     * Given a string representation of a chest's inventory, convert the string into a chest.
     * 
     * @param invString string representation of chest inventory
     * @return chest with all inventory items added
     */
    public static Chest stringToChest(String invString) {

        if (invString.equals("R"))
            return new Chest(Chest.generateLoot());

        Chest chest = new Chest();
        Inventory inv = chest.getInventory();
        for (String itemString : invString.split(FileConstants.COMMA_REGEX)) {
            Item item = stringToItem(itemString);
            inv.addItem(item);
        }
        return chest;
    }

    /**
     * Given a string representation of a npc, convert it into a npc.
     * 
     * name,description,health,attack,defense,isDiurnal item1, item2, item3, item4
     * 
     * @param npc npc to convert to string
     * @return string representation of npc
     */
    public static NPC stringToNPC(String npcData, String invString) {
        if (npcData.equals("R"))
            return NPC.generateNPC();

        // Read in NPC inventory
        Inventory inv = Inventory.getInfiniteInventory();
        for (String itemString : invString.split(FileConstants.COMMA_REGEX))
            inv.addItem(stringToItem(itemString));

        // Construct NPC
        String[] tokens = npcData.split(FileConstants.COMMA_REGEX);
        Stats stats = new Stats(parseInt(tokens[2]), parseInt(tokens[3]), parseInt(tokens[4]));
        return new NPC(tokens[0], tokens[1], stats, Boolean.parseBoolean(tokens[5]), inv);
    }

    public static Merchant stringToMerchant(String merchantData, String shopString) {
        if (merchantData.equals("R"))
            return Merchant.generateMerchant();

        // Read in merchant shop items
        Inventory inv = Inventory.getInfiniteInventory();
        for (String itemString : shopString.split(FileConstants.COMMA_REGEX))
            inv.addItem(stringToItem(itemString));

        // Construct NPC
        String[] tokens = merchantData.split(FileConstants.COMMA_REGEX);
        Stats stats = new Stats(parseInt(tokens[2]), parseInt(tokens[3]), parseInt(tokens[4]));
        return new Merchant(tokens[0], tokens[1], stats, inv);
    }


    /**
     * Given a string representation of a tile's string objects, convert each string into the corresponding object. Then, add converted object to the tile.
     * 
     * (A | is used if a tile contains more than one object) 
     * Blank Tile: 0 Obstacle Tile: O 
     * NPC/Chest Tile: Iindex (ex. I5) 
     * Trap Tile: Tattack (ex. T100 OR TR) 
     * Exit Tile: Eid (ex. E5) 
     * Player Tile: P
     * 
     * @param input input to process
     * @param room room of tile
     * @param tile location to set tile of
     * @param runningList list to read each heavy data object from
     */
    public static void writeStringToTile(String input, Room room, Location loc,
            ArrayList<GameObject> runningList) {
        // Add everything on tile to the tile
        for (String objectString : input.split(FileConstants.VERT_BAR_REGEX)) {
            GameObject obj = null;

            // Obstacle
            if (objectString.startsWith("O"))
                obj = new Obstacle();
            // Exit
            else if (objectString.startsWith("E"))
                obj = new Exit(parseInt(objectString.substring(1)));
            // Trap
            else if (objectString.startsWith("T")) {
                objectString = objectString.substring(1);
                obj = objectString.equals("R") ? new Trap() : new Trap(parseInt(objectString));
            }
            // Chest/NPC/Merchant/Player
            else if (objectString.startsWith("I")) {
                int index = parseInt(objectString.substring(1)) - 1;
                obj = runningList.get(index);
                    
            }

            if(obj != null)
                room.forceSetData(loc, obj);
        }
    }

    /**
     * Given an output path, load the player from the output path. The player is formatted as follows: 
     * name,description health,attack,defense 
     * equipment1,equipment2,equipment3,equipment4
     * bagItem1,bagItem2,bagItem3.....,bagItem6 
     * item1,item2,item3,item4....
     * 
     * @param playerSavePath path to read file from
     */
    public static Player loadPlayer(Scanner scanner) {
        Player player = null;
        // Read in player data
        String[] playerTokens = scanner.nextLine().split(FileConstants.COMMA_REGEX);
        Stats stats = new Stats(parseInt(playerTokens[2]), parseInt(playerTokens[3]), parseInt(
                playerTokens[4]));

        // Read in equipped items
        Inventory inventory = new Inventory(Player.BAG_CAPACITY);
        EnumMap<EquipTag, Equippable> equipment = inventory.getEquipment();
        String[] equipTokens = scanner.nextLine().split(FileConstants.COMMA_REGEX);
        for (String token : equipTokens) {
            if (token.isBlank())
                continue;
            Equippable equippable = (Equippable) stringToItem(token);
            if (equippable != null)
                equipment.put(equippable.getEquipTag(), equippable);
        }

        // Read in inventory bags
        String[] tokens = scanner.nextLine().split(FileConstants.COMMA_REGEX);
        for (String token : tokens)
            inventory.addItem(stringToItem(token));

        // Read in all items
        tokens = scanner.nextLine().split(FileConstants.COMMA_REGEX);
        for (String token : tokens)
            inventory.addItem(stringToItem(token));

        player = new Player(playerTokens[0], playerTokens[1], stats, inventory);
        return player;
    }

    /**
     * Given a map, load the map at the output path. Each heavy data object is prefixed by its type. (ex. ROOM, CHEST, NPC)
     * 
     * @param map
     * @param outputPath
     */
    public static Map loadMap(Scanner scanner) {
        Map map = new Map();
        ArrayList<Exit> exits = new ArrayList<Exit>();
        ArrayList<GameObject> objects = new ArrayList<>();

        // If there's data left, we have more data to parse
        while (scanner.hasNextLine()) {
            // Properly parse next chunk according to item data type
            String dataType = scanner.nextLine();

            // NPC parsing
            if (dataType.startsWith("NPC")) {
                NPC npc = stringToNPC(scanner.nextLine(), scanner.nextLine());
                objects.add(npc);
            }
            // Merchant parsing
            else if (dataType.startsWith("MERCHANT")) {
                Merchant merchant = stringToMerchant(scanner.nextLine(), scanner.nextLine());
                objects.add(merchant);
            }
            // Chest parsing
            else if (dataType.startsWith("CHEST")) {
                Chest chest = stringToChest(scanner.nextLine());
                objects.add(chest);
            }
            // Player parsing
            else if(dataType.startsWith("PLAYER")){
                Player player = loadPlayer(scanner);
                objects.add(player);
            }
            // Room parsing
            else if (dataType.startsWith("ROOM")) {
                // Create basic room given parameters
                String[] tokens = scanner.nextLine().split(FileConstants.COMMA_REGEX);
                Room room = new Room(parseInt(tokens[0]), parseInt(tokens[1]), parseInt(tokens[2]));

                Tile[][] tiles = room.getTiles();
                // Read each row of the room
                for (int h = 0; h < room.getHeight(); h++) {
                    // Split each row into individual tiles
                    tokens = scanner.nextLine().split(FileConstants.COMMA_REGEX);

                    // Read each tile of the row
                    for (int w = 0; w < room.getWidth(); w++) {
                        Tile tile = tiles[h][w];
                        writeStringToTile(tokens[w], room, tile.getLocation(), objects);

                        // If tile has a player, update current room
                        if (tile.occupant instanceof Player)
                            map.currRoom = room;

                        // If tile has an exit, add it to list of exits to connect
                        if (tile.content instanceof Exit exit) {
                            exits.add(exit);
                        }
                    }
                }

                room.generateDesc();
                map.rooms.add(room);
                objects.clear();
            }
        }

        Map.connectRooms(exits);

        return map;
    }

    /**
     * Given a file, load game using csv with said file.
     * Program automatically uses first living player as player.
     * @param gameSavePath location of csv file
     * @return Game containing loaded data
     */
    public static Game loadGame(String gameSavePath) {
        File file = new File(gameSavePath);
        try (Scanner scanner = new Scanner(file)) {
            Map map = loadMap(scanner);

            for(Entity entity : map.currRoom.getEntities()){
                if (entity instanceof Player player && !player.isDead()) {
                    return new Game(player, map);
                }
            }
                
                    
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Game loadNewGame(File file, Player player){
        try (Scanner scanner = new Scanner(file)) {
            Map map = loadMap(scanner);

            for (Entity entity : map.currRoom.getEntities()) {
                if (entity instanceof Player oldPlayer && !oldPlayer.isDead()) {
                    map.currRoom.forceSetData(oldPlayer.getLocation(), player);
                    return new Game(player, map);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
