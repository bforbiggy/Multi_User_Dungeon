package model.env;

import model.GameObject;

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
