package model.env;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import model.GameObject;

public class Exit implements GameObject
{
    private Room curRoom;
    private Room otherRoom;
    private int id;
    
    public Exit(int id){
        this.id = id;
    }

    public void connectRoom(Room otherRoom){
        this.otherRoom = otherRoom;
    }

    public void setCurRoom(Room curRoom) {
        this.curRoom = curRoom;
    }

    public void setID(int id){
        this.id = id;
    }

    public Room getCurRoom() {
        return curRoom;
    }

    public int getId() {
        return id;
    }

    public Room getOtherRoom() {
        return otherRoom;
    }

    @Override
    public Element createMemento(Document doc) {
        Element exit = doc.createElement("exit");
        exit.setAttribute("id", Integer.toString(id));
        return exit;
    }

    @Override
    public Exit loadMemento(Element element){
        return this;
    }

    @Override
    public String toString()
    {
        return "E";
    }
}
