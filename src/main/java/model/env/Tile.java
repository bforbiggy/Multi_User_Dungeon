package model.env;

import model.GameObject;
import model.entities.Entity;
import model.entities.Merchant;

public class Tile {
    public GameObject occupant;
    public GameObject content;
    private Location location;

    public Tile(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    /**
     * Forcibly adds an object to this tile's occupant/content accordingly.
     * This will replace whatever was previously on the tile.
     * @param obj object to add to this tile
     */
    public void forceAdd(GameObject obj)
    {
        //TODO: Consider just keeping this in csvloader..
        if(obj instanceof Entity entity)
        {
            if(entity.isDead() || entity instanceof Merchant)
                content = entity;
            else
                occupant = entity;
            entity.setLocation(location);
        }
        else if(obj instanceof Obstacle)
            occupant = obj;
        else
            content = obj;
    }

    public void setContent(GameObject content){
        this.content = content;
    }

    public void setOccupant(GameObject occupant) {
        this.occupant = occupant;
    }

    @Override
    public String toString() {
        return " ";
    }
}
