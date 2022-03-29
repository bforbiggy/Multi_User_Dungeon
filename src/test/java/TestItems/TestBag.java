package TestItems;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.platform.commons.annotation.Testable;

import items.Bag;
import items.Item;

@Testable
public class TestBag {
    
    @Test
    public void bagValue(){
        // setup 
        String name = "Bag";
        String desc = "A basic goods carrier";
        int value = 1;
        int capacity = 1;

        int expected = value;

        // invoke 
        Bag bag = new Bag(name, desc, value, capacity);
        int actual = bag.getValues();

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void addItemsSuccessful(){
        // setup
        String name = "Bag";
        String desc = "A basic goods carrier";
        int value = 1;
        int capacity = 1;

        Bag bag = new Bag(name, desc, value, capacity);
        Item item = new Item("Test", "Test", 1);
        boolean expected = false;

        // invoke
        boolean actual = bag.addItem(item);

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void addItemsFailure(){
        // setup
        String name = "Bag";
        String desc = "A basic goods carrier";
        int value = 1;
        int capacity = 1;

        Bag bag = new Bag(name, desc, value, capacity);
        Item item = new Item("Test", "Test", 1);
        boolean expected = true;

        // invoke
        bag.addItem(item);
        boolean actual = bag.addItem(item);

        // analyze
        assertEquals(expected, actual);
    }
}
