package model.env;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import model.GameObject;
import model.Originator;

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
        Element tileElem = doc.createElement("tile");
        tileElem.setAttribute("x", Integer.toString(location.getX()));
        tileElem.setAttribute("y", Integer.toString(location.getY()));
        if(content != null){
            Element contentNode = doc.createElement("content");
            contentNode.appendChild(content.createMemento(doc));
            tileElem.appendChild(contentNode);
        }
        if(occupant != null){
            Element occupantNode = doc.createElement("occupant");
            occupantNode.appendChild(occupant.createMemento(doc));
            tileElem.appendChild(occupantNode);
        }
        return tileElem;
    }

    public Tile loadMemento(Element element){
        location.setX(Integer.parseInt(element.getAttribute("x")));
        location.setY(Integer.parseInt(element.getAttribute("y")));

        NodeList contentNodes = element.getElementsByTagName("content");
        if (contentNodes.getLength() >= 1) {
            Element contentNode = (Element) contentNodes.item(0);
            content = GameObject.convertMemento((Element) contentNode.getFirstChild());
        }

        NodeList occupantNodes = element.getElementsByTagName("occupant");
        if (occupantNodes.getLength() >= 1) {
            Element occupantNode = (Element) occupantNodes.item(0);
            occupant = GameObject.convertMemento((Element)occupantNode.getFirstChild());
        }

        return this;
    }

    @Override
    public String toString() {
        return " ";
    }

}
