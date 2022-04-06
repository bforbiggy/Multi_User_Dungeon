package model.items;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Bag extends Item
{
    public static final Bag INFINITE_BAG = new Bag("Infinite Bag", "ooooh ohhhhh bag is infinite oooh so infinite wow", 50, Integer.MAX_VALUE);

    private int capacity;
    public ArrayList<Item> items = new ArrayList<Item>();

    public Bag(String name, String description, int value, int capacity)
    {
        super(name, description, value);
        this.capacity = capacity;
    }

    /**
     * Whether or not this bag still has space
     * @return Whether or not this bag still has space
     */
    public boolean hasSpace()
    {
        return capacity > items.size();
    }

    /**
     * Attempt to add an item to this bag.
     * Will fail if item is null or bag has no remaining space.
     * 
     * @param item item to add to this bag
     * @return successful operation of bag removal
     */
    public boolean addItem(Item item)
    {
        if(item == null) return false;
        if(hasSpace())
        {
            items.add(item);
            return false;
        }
        return true;
    }

    /**
     * Attempt to remove an item from this bag.
     * @param item item to remove
     * @return Whether or not the item was removed
     */
    public boolean removeItem(Item item)
    {
        return items.remove(item);
    }

    /**
     * Attempt to remove an item from this bag.
     * @param index index of item to remove
     * @return Whether or not the item was removed
     */
    public boolean removeItem(int index)
    {   
        if(index >= items.size()) return false;
        items.remove(index);
        return true;
    }

    /**
     * Attempt to get an item from this bag.
     * @param index index of item to get
     * @return the retrieved item, null if item was not found
     */
    public Item getItem(int index)
    {
        return 0 <= index && index <= items.size() ? items.get(index) : null;
    }

    /**
     * Given two bags, swap the contents of both bag.
     * Will fail if the other bag doesn't have enough space.
     * 
     * @param from the bag to swap from
     * @param to the bag to swap to
     * @return Whether or not the swapping was successful
     */
    public static boolean swapItems(Bag from, Bag to)
    {
        ArrayList<Item> fromList = from.items;
        ArrayList<Item> toList = to.items;
        if(toList.size() < fromList.size())
            return false;

        from.items = toList;
        to.items = fromList;
        return true;
    }

    public Bag copy(){
        return new Bag(name, description, value, capacity);
    }

    /**
     * Returns the value of this bag, excluding its own value.
     * @return the value
     */
    public int getValues()
    {
        int sum = value;
        for(Item item : items)
            sum += item.getValue();
        return sum;
    }

    public int getCapacity() {
        return capacity;
    }

    public static Bag generateBag()
    {
        int capacity = randy.nextInt(8-3)+3;

        String noun = "bag";
        String verb = verbs[randy.nextInt(verbs.length)];
        String adjective = adjectives[randy.nextInt(adjectives.length)];

        String name = String.format("%s %s of %s", adjective, noun, verb);
        String description = String.format("Holy guacamole this %s %s can hold %d items!!", adjective, noun, capacity);
        int value = randy.nextInt(30-10)+10;

        return new Bag(name, description, value, capacity);
    }

    @Override
    public Element createMemento(Document doc) {
        Element itemElem = super.createMemento(doc);
        itemElem.setAttribute("type", "bag");
        itemElem.setAttribute("capacity", Integer.toString(capacity));
        return itemElem;
    }

    @Override
    public Bag loadMemento(Element element){
        return this;
    }
}
