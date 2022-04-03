package model.env;

import java.util.Random;

import model.items.Bag;
import model.items.Consumable;
import model.items.Equippable;
import model.items.Inventory;
import model.items.Item;

public class Chest
{
    private static Random randy = new Random();
    private Inventory inventory;

    public Chest()
    {
        inventory = new Inventory(1);
        inventory.addItem(Bag.INFINITE_BAG.copy());
    }

    public Chest(Inventory inventory) {
        this.inventory = inventory;
    }

    public static Inventory generateLoot()
    {
        Inventory inv = new Inventory(1);
        inv.addItem(Bag.INFINITE_BAG.copy());

        // Randomly generate 1-5 items
        for(int i = 0; i < randy.nextInt(5-1)+1; i++)
        {
            // Decides what item to create
            double roll = randy.nextDouble();
            if(roll < 0.1)
                inv.addItem(Bag.generateBag());
            else if(roll < 0.3)
                inv.addItem(Equippable.generateItem());
            else if(roll < 0.5)
                inv.addItem(Consumable.generateItem());
            else
                inv.addItem(Item.generateItem());
        }

        return inv;
    }

    public Inventory getInventory()
    {
        return inventory;
    }

    @Override
    public String toString()
    {
        return "C";
    }
}