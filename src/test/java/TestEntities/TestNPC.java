package TestEntities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.platform.commons.annotation.Testable;

import entities.NPC;
import entities.Stats;

@Testable
public class TestNPC {

    public NPC createMan(){
        Stats stats = new Stats(100, 10, 10);
        NPC guy = new NPC("Guy", "He", stats);
        return guy;
    }

    @Test
    public void testNPCCreated(){
        // setup
        NPC guy = createMan();
        String expected = "N";

        // invoke
        String actual = guy.toString();

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void testNPCTakeDamageWithDEF(){
        // setup
        NPC guy = createMan();
        int expected = 99;

        // invoke
        guy.takeDamage(11);
        Stats s = guy.getStats();
        int actual = s.health;

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void testNPCTakeDamageTooMuchDEF(){
        // setup
        NPC guy = createMan();
        int expected = 99;

        // invoke
        guy.takeDamage(9);
        Stats s = guy.getStats();
        int actual = s.health;

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void testNPCdead(){
        // setup
        NPC guy = createMan();
        boolean expected = true;

        // invoke
        guy.takeDamage(110);
        boolean actual = guy.isDead();

        // analyze
        assertEquals(expected, actual);
    }
}
