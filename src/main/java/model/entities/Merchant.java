package model.entities;

import java.util.Random;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import model.items.*;

public class Merchant extends Entity{
    private static final String[] adjectives = {"shady", "sussy", "average", "scummy", "strange"};

    private static final int ITEM_COUNT = 3;
    private static final double SELL_RATE = 0.5;
    private static final Random randy = new Random();

    public Merchant(String name, String description, Stats stats, Inventory inventory){
        super(name, description, stats);
        this.inventory = inventory;
    }

    /**
     * Allows an inventory to purchase an item with its gold
     * This operation updates both inventories accordingly.
     * Returns the item that was successfully bought.
     * 
     * @param buyerInventory the inventory of the buying target
     * @param itemIndex the index of item to buy
     * @return the item bought, null if buy operation has failed
     */
    public Item buyItem(Inventory buyerInventory, int itemIndex){
        if(itemIndex < ITEM_COUNT && buyerInventory.hasSpace())
        {
            Item item = inventory.bags.get(0).getItem(itemIndex);
            if(item != null && buyerInventory.gold >= item.getValue())
            {
                buyerInventory.gold -= item.getValue();
                Inventory.TransferItem(inventory, buyerInventory, item);
                return item;
            }
        }
        return null;
    }

    /**
     * Allows an inventory to sell an item for gold value.
     * This operation updates both inventories accordingly.
     * 
     * @param sellerInventory the inventory of seller
     * @param item item being sold by seller
     * @return whether or not item was successfully sold
     */
    public boolean sellItem(Inventory sellerInventory, Item item){
        if(Inventory.TransferItem(sellerInventory, Inventory.TRASH, item))
        {
            sellerInventory.gold += item.getValue() * SELL_RATE;
            return true;
        }
        return false;
    }

    public static Inventory generateLoot()
    {
        Inventory inventory = Inventory.getInfiniteInventory();
        for(int i = 0; i < ITEM_COUNT; i++)
        {
            // Decides what item to create
            double roll = randy.nextDouble();
            Item item = null;
            if(roll < 0.2)
                item = Bag.generateBag();
            else if(roll < 0.7)
                item = Equippable.generateItem();
            else
                item = Consumable.generateItem();
            inventory.addItem(item);
        }

        return inventory;
    }

    public static Merchant generateMerchant(){
        Stats stats = new Stats(1,1,1);
        String noun = "merchant";
        String adjective = adjectives[randy.nextInt(adjectives.length)];

        String name = String.format("%s %s", adjective, noun);
        String description = String.format("Think carefully before buying from this %s...", name);

        return new Merchant(name, description, stats, generateLoot());
    }

    @Override
    public Element createMemento(Document doc) {
        Element entityElem = super.createMemento(doc);
        entityElem.setAttribute("type", "merchant");
        return entityElem;
    }

    @Override
    public String toString(){
        return "M";
    }
}
