package TestEntities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.platform.commons.annotation.Testable;

import entities.Player;
import entities.Stats;

@Testable
public class TestPlayer {
    
    @Test
    public void testCreatePlayer(){
        // setup
        String name = "He";
        String desc = "When the he";
        String expected = "P";
        
        // invoke
        Player player = new Player(name, desc);
        String actual = player.toString();

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void testPlayerTakeDamageNoDEF(){
        // setup
        String name = "He";
        String desc = "When the he";
        Player player = new Player(name, desc);
        int expected = 1;

        // invoke
        player.takeDamage(99);
        Stats stats = player.getStats();

        int actual = stats.health;

        // analyze
        assertEquals(expected, actual);
    }

    @Test
    public void testPlayerDead(){
        // setup
        String name = "He";
        String desc = "When the he";
        Player player = new Player(name, desc);
        boolean expected = true;

        // invoke
        player.takeDamage(100);

        boolean actual = player.isDead();

        // analyze
        assertEquals(expected, actual);
    }
}
