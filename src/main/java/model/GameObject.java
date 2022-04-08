package model;

import org.w3c.dom.Element;
import model.entities.*;
import model.env.*;

// Only used to limit what can actually belong on tiles
public interface GameObject extends Originator{
    public static GameObject convertMemento(Element element){
        String objectType = element.getNodeName();
        if(objectType.equalsIgnoreCase(Obstacle.class.getSimpleName())){
            return Obstacle.convertMemento(element);
        }
        else if(objectType.equalsIgnoreCase(Exit.class.getSimpleName())){
            return Exit.convertMemento(element);
        }
        else if(objectType.equalsIgnoreCase(Entity.class.getSimpleName())){
            return Entity.convertMemento(element);
        }
        else if(objectType.equalsIgnoreCase(Trap.class.getSimpleName())){
            return Trap.convertMemento(element);
        }
        else if(objectType.equalsIgnoreCase(Chest.class.getSimpleName())){
            return Chest.convertMemento(element);
        }
        else if(objectType.equalsIgnoreCase(Shrine.class.getSimpleName())){
            return Shrine.convertMemento(element);
        }
        throw new IllegalArgumentException("Unknown object type: " + objectType);
    }
}
 