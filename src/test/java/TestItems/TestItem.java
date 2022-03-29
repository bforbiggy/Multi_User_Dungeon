package TestItems;

import items.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class TestItem {
    @Test
    public void createItem() {
        // setup
        String name = "Egg";
        String desc = "It's an egg";
        int value = 5;

        // invoke
        Item egg = new Item(name, desc, value);
        String actualName = egg.getName();
        String actualDesc = egg.getDescription();
        int actualValue = egg.getValues();

        // analyze

        assertEquals(name, actualName);
        assertEquals(desc, actualDesc);
        assertEquals(value, actualValue);
    }
}
