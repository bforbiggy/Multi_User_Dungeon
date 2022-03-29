package TestEnv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.platform.commons.annotation.Testable;

import env.Location;
import env.Tile;

@Testable
public class TestTile {
    @Test
    public void testCreateTile(){
        // setup
        Location l = new Location(1, 1);
        Tile t = new Tile(null, l);
        String expected = " ";

        // invoke
        String actual = t.toString();

        // analyze
        assertEquals(expected, actual);
    }
}
