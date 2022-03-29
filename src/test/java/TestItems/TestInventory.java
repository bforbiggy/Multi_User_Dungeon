package TestItems;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.platform.commons.annotation.Testable;

import items.Inventory;
import items.Item;
import items.Bag;

@Testable
public class TestInventory {
    @Test
    public void testInventoryCreation(){
        // setup
        Inventory i = new Inventory();
        String expected = "[Inventory: 0/0 used spaces, 0 value]\n";

        // invoke
        String actual = i.toString();

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void testAddBag(){
        // setup
        Inventory i = new Inventory();
        Bag bag = Bag.generateItem();
        boolean expected = true;

        // invoke
        boolean actual = i.addItem(bag);

        // analyze
        assertEquals(expected, actual);
    }

    @Test 
    public void testAddManyBags(){
        // setup
        Inventory i = new Inventory();
        Bag bag1 = new Bag("F", "f", 0, 1);
        Bag bag2 = new Bag("F", "f", 0, 1);
        Bag bag3 = new Bag("F", "f", 0, 1);
        Bag bag4 = new Bag("F", "f", 0, 1);
        Bag bag5 = new Bag("F", "f", 0, 1);
        Bag bag6 = new Bag("F", "f", 0, 1);
        Bag bag7 = new Bag("F", "f", 0, 1);
        Bag[] bags = {bag1, bag2, bag3, bag4, bag5, bag6};
        String expected = "[Inventory: 1/6 used spaces, 0 value]\n"+
                            "Bag #1: 1/1 used, 0 value\n"+
                            "===========================\n"+
                            "F: f\n"+
                            "Bag #2: 0/1 used, 0 value\n"+
                            "===========================\n"+
                            "Bag #3: 0/1 used, 0 value\n"+
                            "===========================\n"+
                            "Bag #4: 0/1 used, 0 value\n"+
                            "===========================\n"+
                            "Bag #5: 0/1 used, 0 value\n"+
                            "===========================\n"+
                            "Bag #6: 0/1 used, 0 value\n"+
                            "===========================\n";

        // invoke
        for(int x = 0; x < bags.length; x++){
            i.addItem(bags[x]);
        }
        i.addItem(bag7);
        String actual = i.toString();

        // analyze
        assertEquals(expected, actual);
    } 

    @Test
    public void testAddItemSuccess(){
        // setup
        Inventory i = new Inventory();
        Bag bag = new Bag("F", "f", 0, 1);
        Item thing = new Item("H", "h", 69);
        boolean expected = true;

        // invoke
        i.addItem(bag);
        boolean actual = i.addItem(thing);

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void testAddItemFailure(){
        // setup
        Inventory i = new Inventory();
        Bag bag = new Bag("F", "f", 0, 1);
        Item thing1 = new Item("E", "e", 69);
        Item thing2 = new Item("H", "h", 69);
        boolean expected = false;

        // invoke
        i.addItem(bag);
        i.addItem(thing1);
        boolean actual = i.addItem(thing2);

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveItemEmpty(){
        // setup
        Inventory i = new Inventory();
        Item thing = new Item("E", "e", 69);
        boolean expected = false;

        // invoke
        boolean actual = i.removeItem(thing);

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveItemSuccess(){
        // setup
        Inventory i = new Inventory();
        Bag bag = new Bag("H", "h", 0, 1);
        Item thing = new Item("E", "e", 69);
        boolean expected = true;

        // invoke
        i.addItem(bag);
        i.addItem(thing);
        boolean actual = i.removeItem(thing);

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveItemFailure(){
        // setup
        Inventory i = new Inventory();
        Bag bag = new Bag("H", "h", 0, 1);
        Item thing1 = new Item("E", "e", 69);
        Item thing2 = new Item("E", "e", 69);
        boolean expected = false;

        // invoke
        i.addItem(bag);
        i.addItem(thing1);
        boolean actual = i.removeItem(thing2);

        // analyze
        assertEquals(expected, actual);
    }
}
