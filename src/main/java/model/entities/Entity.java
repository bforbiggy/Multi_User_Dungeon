package model.entities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import model.GameObject;
import model.env.Location;
import model.items.Inventory;

public abstract class Entity implements GameObject
{
    protected String name;
    protected String description;
    protected Stats stats;
    protected Inventory inventory;
    protected Location location;

    protected Entity(String name, String description, Stats stats)
    {
        this.name = name;
        this.description = description;
        this.stats = stats;
    }

    public Inventory getInventory(){
        return inventory;
    }

    /**
     * Causes this player to take damage.
     * Damage always deals a minimum of 1.
     * @param damage damage to take
     * @return the overall damage taken after calculations
     */
    public int takeDamage(int damage){
        int actualDamage = Math.max(1, damage - stats.defense);
        stats.health -= actualDamage;
        return actualDamage;
    }

    public int dealDamage(Entity other){
        return other.takeDamage(stats.attack);
    }

    @Override
    public Element createMemento(Document doc){
        Element entityElem = doc.createElement("entity");
        entityElem.setAttribute("name", name);
        entityElem.setAttribute("description", description);
        entityElem.appendChild(stats.createMemento(doc));
        entityElem.appendChild(inventory.createMemento(doc));
        return entityElem;
    }

    public static Entity convertMemento(Element element){
        if (element.getAttribute("type").equalsIgnoreCase(NPC.class.getSimpleName()))
            return NPC.convertMemento(element);
        if (element.getAttribute("type").equalsIgnoreCase(Merchant.class.getSimpleName()))
            return Merchant.convertMemento(element);
        if (element.getAttribute("type").equalsIgnoreCase(Player.class.getSimpleName()))
            return Player.convertMemento(element);
        return null;
    }

    public Location getLocation() {
        return location;
    }

    public Stats getStats(){
        return stats;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public void setLocation(Location loc){
        this.location = loc;
    }

    public boolean isDead(){
        return stats.health <= 0;
    }
}
