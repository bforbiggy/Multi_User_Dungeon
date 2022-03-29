package model.items;

import model.entities.Entity;
import model.entities.Stats;
import model.events.PlayerTurnEndListener;
import model.events.PlayerTurnEnd;

public class Consumable extends Item implements PlayerTurnEndListener
{
    protected static String[] consumable_noun = {"banana", "flesh", "juice", "milk", "liquid hydrogen"};
    private Entity target;

    private int duration; // Set as 0 for permanent buff
    private Stats stats;

    public Consumable(String name, String description, int value, Stats effect, int duration)
    {
        super(name, description, value);
        this.stats = effect;
        this.duration = duration;
    }

    public void consume(Entity target)
    {
        this.target = target;
        Stats playerStats = target.getStats();
        playerStats.plus(stats);
    }

    /**
     * Listener for the PlayerTurnEnd event.
     * When the duration of this temporary consumable ends,
     * the effect applied onto the player is ended.
     */
    public void onPlayerTurnEnd(PlayerTurnEnd playerTurnEnd)
    {
        duration--;
        if(duration == 0)
        {
            playerTurnEnd.scheduleRemoveListener(this);
            Stats playerStats = target.getStats();
            playerStats.subtract(stats);
        }
    }

    public int getDuration(){
        return duration;
    }

    public Stats getStats() {
        return stats;
    }

    public boolean isBuff(){
        return duration != 0;
    }

    public static Consumable generateItem()
    {
        int duration = randy.nextBoolean() ? 0 : randy.nextInt(10-5)+5;
        Stats effect = duration == 0 ? new Stats(randy.nextInt(20-10)+10, 0, 0) : new Stats(randy.nextInt(5), randy.nextInt(5), randy.nextInt(5));

        String noun = consumable_noun[randy.nextInt(consumable_noun.length)];
        String verb = verbs[randy.nextInt(verbs.length)];
        String adjective = adjectives[randy.nextInt(adjectives.length)];

        String name = String.format("%s %s of %s", adjective, noun, verb);
        String description = String.format("Chug jug this %s to %s!!", noun, verb);
        int value = randy.nextInt(20-5)+5;

        return new Consumable(name, description, value, effect, duration);
    }
}
