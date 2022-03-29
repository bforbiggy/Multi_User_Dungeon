package TestEntities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.platform.commons.annotation.Testable;

import entities.Stats;

@Testable
public class TestStats {
    
    @Test
    public void createStats(){
        // setup
        int health = 1;
        int attack = 1;
        int defense = 1;

        // invoke
        Stats stats = new Stats(health, attack, defense);
        int actualHealth = stats.health;
        int actualAttack = stats.attack;
        int actualDefense = stats.defense;

        assertEquals(health, actualHealth);
        assertEquals(attack, actualAttack);
        assertEquals(defense, actualDefense);

    }
}
