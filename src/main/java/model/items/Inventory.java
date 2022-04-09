package model.items;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import model.Originator;
import model.entities.Entity;
import model.events.PlayerTurnEnd;

public class Inventory implements Originator, Iterable<Bag> {
    public static final Inventory TRASH = getInfiniteInventory();

    public int gold = 0;
    private int capacity;
    public ArrayList<Bag> bags = new ArrayList<Bag>();
    private EnumMap<EquipTag, Equippable> equipment = new EnumMap<>(EquipTag.class);

    public Inventory(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Given an inventory, find the bag that contains a specific item
     * 
     * @param inv Inventory of bags to search through
     * @param item To find in the inventory
     * @return Bag containing item
     */
    private Bag itemSearch(Item goal) {
        for (Bag bag : bags)
            for (Item item : bag)
                if (item.equals(goal))
                    return bag;
        return null;
    }

    /**
     * Finds the first bag in this inventory that has space
     * 
     * @return first free bag
     */
    private Bag firstFreeBag() {
        for (Bag bag : bags)
            if (bag.hasSpace())
                return bag;
        return null;
    }

    public boolean hasSpace() {
        for (Bag bag : bags)
            if (bag.hasSpace())
                return true;
        return false;
    }

    /**
     * Attempts to retrive specific item in inventory by indexes.
     * 
     * @param bagIndex The bag index to search for item.
     * @param itemIndex The item index to retrieve item from.
     * @return the retrieved item.
     */
    public Item getItem(int bagIndex, int itemIndex) {
        return bags.get(bagIndex).getItem(itemIndex);
    }

    /**
     * Adds an item to the next available bag slot. If the item is a bag, it is automatically equipped if possible.
     * 
     * @param item item to add to inventory
     * @return Whether or not the item was sucessfully added.
     */
    public boolean addItem(Item item) {
        // Special Case: Item is a bag we can try to autoequip
        if (item instanceof Bag && bags.size() < capacity) {
            bags.add((Bag) (item));
            return true;
        }

        // Attempts to add the item to next available slot
        Bag bag = firstFreeBag();
        if (bag == null)
            return false;
        return bag.addItem(item);
    }

    /**
     * Attempts to remove an item from the inventory.
     * 
     * @param item item to remove
     * @return Whether or not the item was sucessfully removed.
     */
    public boolean removeItem(Item item) {
        Bag bag = itemSearch(item);
        if (bag == null)
            return false;

        bag.removeItem(item);
        return true;
    }

    /**
     * Attempts to remove specific item in inventory by indexes.
     * 
     * @param bagIndex The bag index to search for item.
     * @param itemIndex The item index of item to remove
     * @return Whether or not the item succesfully removed
     */
    public boolean removeItem(int bagIndex, int itemIndex) {
        if (bagIndex > bags.size())
            return false;
        Bag bag = bags.get(bagIndex);
        return bag.removeItem(itemIndex);
    }

    /**
     * Attempts to swap a bag in inventory with equipped bag.
     * This will move all items in bag to new bag
     * 
     * @param bagIndex index of bag in inventory to swap out
     * @param newBag bag to swap in and store contents in
     * @return Whether or not the bags were swapped
     */
    public boolean swapBags(int bagIndex, int targetBagIndex, int targetItemIndex) {
        Bag oldBag = bags.get(bagIndex);
        Item item = bags.get(targetBagIndex).getItem(targetItemIndex);

        // If items were successfully swapped to new bag, update inventory list
        if(item instanceof Bag newBag && Bag.swapItems(oldBag, newBag)){
            bags.set(bagIndex, newBag);
            return true;
        }
        return false;
    }

    /**
     * Attempts to equip an item to the target, changing its stats in the process. This operation unequips previously equipped items if it exists.
     * 
     * @param target the target the item is being equipped to
     * @param equippable equippable to equip
     */
    public void equipItem(Entity target, Equippable equippable) {
        Equippable previous = equipment.put(equippable.getEquipTag(), equippable);
        if (previous != null) {
            addItem(previous);
            previous.unequip(target);
        }

        removeItem(equippable);
        equippable.equip(target);
    }

    /**
     * Attempts to unequip an item to the target, changing its stats in the process.
     * 
     * @param target the target the item is being unequipped from
     * @param item item to attempt to equip to target
     */
    public Equippable unequipItem(Entity target, EquipTag tag) {
        Equippable removed = equipment.remove(tag);
        if (removed != null) {
            addItem(removed);
            removed.unequip(target);
        }
        return removed;
    }

    /**
     * Attempts to use a consumable on the target, changing its stats in the process. If this consumable has a temporary effect, the consumable is subscribed to the playerTurnEnd
     * event.
     * 
     * @param playerTurnEnd The PlayerTurnEnd event to subscribe to
     * @param target The target the consumable is applied to
     * @param consumable The consumable to consume
     */
    public void consumeItem(PlayerTurnEnd playerTurnEnd, Entity target, Consumable consumable) {
        // Consume item
        consumable.consume(target);
        removeItem(consumable);

        if (consumable.isBuff())
            playerTurnEnd.addListener(consumable);
    }

    /**
     * Transfer an item from one inventory to another if possible
     * 
     * @param from The inventory to take item from
     * @param to The inventory to deposit item to
     * @param item The item being transferred
     * @return Whether the item was successfully added or not
     */
    public static boolean transferItem(Inventory from, Inventory to, Item item) {
        return from.removeItem(item) && to.addItem(item);
    }

    /**
     * Generates a bag of infinite capacity
     * 
     * @return an inventory with one bag of infinite capacity
     */
    public static Inventory getInfiniteInventory() {
        Inventory inventory = new Inventory(1);
        inventory.addItem(Bag.INFINITE_BAG.copy());
        return inventory;
    }

    

    public Element createMemento(Document doc) {
        Element element = doc.createElement("inventory");
        element.setAttribute("gold", Integer.toString(gold));
        element.setAttribute("capacity", Integer.toString(capacity));

        // List all bags equipped
        for (Bag bag : bags) {
            Element bagElem = bag.createMemento(doc);
            // List all items in bag
            for (Item item : bag)
                bagElem.appendChild(item.createMemento(doc));
            element.appendChild(bagElem);
        }

        // List all equipped items
        Element equippedElem = doc.createElement("equipment");
        for (Equippable equippable : equipment.values())
            equippedElem.appendChild(equippable.createMemento(doc));
        element.appendChild(equippedElem);
        return element;
    }

    // TODO: TEST INVENTORY MEMENTO CONVERSION
    public static Inventory convertMemento(Element element) {
        // Create inventory with initial gold and capacity values
        Inventory inventory = new Inventory(Integer.parseInt(element.getAttribute("capacity")));
        inventory.gold = Integer.parseInt(element.getAttribute("gold"));

        // Parse bags into inventory, then parse items into bags
        NodeList bagNodes = element.getElementsByTagName("item");
        for (int i = 0; i < bagNodes.getLength(); i++) {
            Element bagElem = (Element) bagNodes.item(i);
            Bag bag = Bag.convertMemento(bagElem);
            inventory.addItem(bag);

            // Parse each bag's item into bag
            NodeList itemNodes = bagElem.getElementsByTagName("item");
            for (int j = 0; j < itemNodes.getLength(); j++) {
                if (itemNodes.item(j) instanceof Element itemElem) {
                    Item item = Item.convertMemento(itemElem);
                    bag.addItem(item);
                }
            }
        }

        // Parse all equipped items
        Element equippedElems = (Element) element.getElementsByTagName("equipment").item(0);
        NodeList equipNodes = equippedElems.getElementsByTagName("item");
        for (int i = 0; i < equipNodes.getLength(); i++) {
            if (equipNodes.item(i) instanceof Element equipElem) {
                Equippable equippable = Equippable.convertMemento(equipElem);
                inventory.equipment.put(equippable.getEquipTag(), equippable);
            }
        }

        return null;
    }

    public EnumMap<EquipTag, Equippable> getEquipment() {
        return equipment;
    }

    @Override
    public Iterator<Bag> iterator() {
        return bags.iterator();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        int usedSpace = 0;
        int availableSpace = 0;
        int value = 0;
        for (int i = 0; i < bags.size(); i++) {
            Bag bag = bags.get(i);

            // Accumulate bag data
            usedSpace += bag.size();
            availableSpace += bag.capacity();
            value += bag.getValues();

            // Print out the bag information
            output.append(String.format("Bag #%d: %d/%d used, %d value", (i + 1), bag.size(), bag
                    .capacity(), bag.getValues()));
            output.append("\n===========================\n");

            // Print out items in the bag
            for (Item item : bag) {
                output.append(item.toString() + "\n");
            }
        }

        return String.format("[Inventory: %d/%d used spaces, %s value]%n", usedSpace,
                availableSpace, value) + output.toString();
    }
}
