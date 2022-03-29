package model.entities;

import model.env.Location;
import model.items.Inventory;

public abstract class Entity 
{
    protected String name;
    protected String description;
    protected Stats stats;
    protected Inventory inventory;
    protected Location location;

    public Entity(String name, String description, Stats stats)
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
