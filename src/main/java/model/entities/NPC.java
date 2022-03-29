package model.entities;

import java.util.Random;

import model.env.Location;
import model.env.Room;
import model.events.DayCycle;
import model.events.DayCycleListener;
import model.events.PlayerTurnEnd;
import model.events.PlayerTurnEndListener;
import model.items.*;

public class NPC extends Entity implements DayCycleListener, PlayerTurnEndListener
{
    private static Random randy = new Random();
    private static String[] nouns = {"ogre", "gremlin", "frog", "bird", "unicorn"};
    private static String[] adjectives = {"angry", "calm", "fast", "stupid", "heterosexual"};

    private boolean diurnal;
    private static double[] buffMultiplier = {1.2, 1.2, 1.2};
    private static double[] debuffMultiplier = {0.8, 0.8, 0.8};

    public NPC(String name, String description, Stats stats)
    {
        super(name, description, stats);
        description = generateDescription(this);

        diurnal = randy.nextBoolean();
        inventory = new Inventory(1);
        inventory.addItem(Bag.INFINITE_BAG.copy());
    }

    public NPC(String name, String description, Stats stats, boolean diurnal, Inventory inventory)
    {
        super(name, description, stats);
        description = generateDescription(this);
        this.diurnal = diurnal;
        this.inventory = inventory;
    }

    public static Inventory generateLoot()
    {
        Inventory inv = new Inventory(1);
        inv.addItem(Bag.INFINITE_BAG.copy());

        // Randomly generate 0-2 items
        for(int i = 0; i < randy.nextInt(3); i++)
        {
            // Decides what item to create
            double roll = randy.nextDouble();
            if(roll < 0.1)
                inv.addItem(Bag.generateBag());
            else if(roll < 0.6)
                inv.addItem(Equippable.generateItem());
            else if(roll < 0.8)
                inv.addItem(Consumable.generateItem());
            else
                inv.addItem(Item.generateItem());
        }

        inv.gold += randy.nextInt(100-10)+10;

        return inv;
    }

    /**
     * Upon the day turning to night or vice versa, 
     * change npc's stats depending on if npc is diurnal or nocturnal.
     * 
     * @param dayCycle the dayCycle event
     */
    public void DayCycleChange(DayCycle dayCycle)
    {
        // Dead characters don't get buffed/debuffed
        if(isDead()) return;

        boolean isDay = dayCycle.isDay();
        String output = (isDay ? "Night turns to day" : "Day turns to night") + " and the " + name + "'s stats ";

        // Character is being debuffed
        if(isDay ^ diurnal)
        {
            stats = stats.multiply(buffMultiplier);
            output += "decrease by 20%";
        }
        // Character is being buffed
        else
        {
            stats = stats.multiply(debuffMultiplier);
            output += "increase by 20%";
        }

        System.out.println(output);
    }

    private static String generateDescription(NPC npc)
    {
        String output = "";
        output += npc.name + " has stats: ";
        output += String.join(",", npc.stats.toStringArray());
        return output;
    }

    public static NPC generateNPC()
    {
        Stats stats = new Stats(randy.nextInt(151-50)+50, randy.nextInt(16-5)+5, randy.nextInt(11));

        String noun = nouns[randy.nextInt(nouns.length)];
        String adjective = adjectives[randy.nextInt(adjectives.length)];

        String name = String.format("%s %s", adjective, noun);
        String description = String.format("I don't know about you but this %s sure is %s", noun, adjective);

        return new NPC(name, description, stats, randy.nextBoolean(), generateLoot());
    }

    public boolean getDiurnal(){
        return diurnal;
    }
    
    @Override
    public String toString(){
        return isDead() ? "n" : "N";
    }

    @Override
    public void onPlayerTurnEnd(PlayerTurnEnd playerTurnEnd) {
        Player player = playerTurnEnd.getGame().getPlayer();
        Location playerLoc = player.getLocation();
        Room room = playerTurnEnd.getGame().getCurrRoom();
        if(isDead())
        {
            room.setContent(location, this);
            room.setOccupant(location, null);
            playerTurnEnd.scheduleRemoveListener(this);
        }
        else if(Location.getDistance(location, playerLoc) <= Math.sqrt(2))
        {
            int actualDamage = dealDamage(player);
            System.out.println(name + " dealt " + actualDamage + " damage!");
        }
    }
}
