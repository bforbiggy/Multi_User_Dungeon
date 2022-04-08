package model.env;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import model.Game;
import model.GameObject;

public class Shrine implements GameObject {
    private Document gameDocument;

    /**
     * When a shrine is prayed at, the shrine saves game state
     * This game state can be used to rewind game to said state
     * @param game game to save the state of
     */
    public void use(Game game) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            gameDocument = builder.newDocument();
            gameDocument.appendChild(game.createMemento(gameDocument));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Element getGameMemento() {
        return (Element) gameDocument.getFirstChild();
    }

    @Override
    public Element createMemento(Document doc) {
        return doc.createElement("shrine");
    }

    public static Shrine convertMemento(Element memento) {
        return new Shrine();
    }

    @Override
    public String toString() {
        return "S";
    }
}
