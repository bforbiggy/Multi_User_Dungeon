package model.env;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import model.GameObject;
import util.Originator;

public class Tile implements Originator{
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
    public Element createMemento(Document doc) {
        Element tile = doc.createElement("tile");
        tile.setAttribute("x", Integer.toString(location.getX()));
        tile.setAttribute("y", Integer.toString(location.getY()));
        if(content != null)
            tile.appendChild(content.createMemento(doc));
        if(occupant != null)
            tile.appendChild(occupant.createMemento(doc));
        return tile;
    }

    @Override
    public Tile loadMemento(Element element){
        return this;
    }

    @Override
    public String toString() {
        return " ";
    }

}
