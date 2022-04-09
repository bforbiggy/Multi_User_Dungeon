package model.env;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import model.GameObject;

public class Obstacle implements GameObject {
    private String representation;

    public Obstacle() {
        this.representation = "@";
    }

    @Override
    public Element createMemento(Document doc) {
        Element obstacle = doc.createElement("obstacle");
        return obstacle;
    }

    public static Obstacle convertMemento(Element element) {
        return new Obstacle();
    }

    @Override
    public String toString() {
        return representation;
    }
}
