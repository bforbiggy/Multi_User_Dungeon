package TestEnv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.platform.commons.annotation.Testable;

import env.Trap;
import entities.*;

// Note: None of these tests will pass consistantly due to the internal random of the related functions.

@Testable
public class TestTrap {
    @Test
    public void testTrapTriggered(){
        // setup
        Player guy = new Player("He", "When the");
        Trap trap = new Trap(69420);
        boolean expected = false;

        // invoke
        boolean actual = trap.disarmTrap(guy); 

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void testTrapDisarmed(){
        // setup
        Player guy = new Player("He", "When the");
        Trap trap = new Trap(69420);
        boolean expected = true;

        // invoke
        boolean actual = trap.disarmTrap(guy); 

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void testDetectTrapFailed(){
        // setup
        Trap trap = new Trap(69420);
        boolean expected = false;

        // invoke
        boolean actual = trap.detectTrap(); 

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void testDetectTrapSuccess(){
        // setup
        Trap trap = new Trap(69420);
        boolean expected = true;

        // invoke
        boolean actual = trap.detectTrap(); 

        // analyze
        assertEquals(expected, actual);
    }
}
