package model.env;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import model.GameObject;

public class Obstacle implements GameObject
{
    private String name;
    private String representation;

    public Obstacle()
    {
        this.name = "obstacle";
        this.representation = "@";
    }

    public Obstacle(String name, String representation)
    {
        this.name = name;
        this.representation = representation;
    }

    @Override
    public Element createMemento(Document doc){
        Element obstacle = doc.createElement("obstacle");
        obstacle.setAttribute("name", name);
        obstacle.setAttribute("representation", representation);
        return obstacle;
    }

    @Override
    public Obstacle loadMemento(Element element){
        return this;
    }

    @Override
    public String toString()
    {
        return representation;
    }
}
