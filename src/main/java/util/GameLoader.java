package util;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import model.Game;
import model.entities.Player;

public class GameLoader {
    public static Document readDocument(String filePath){
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filePath));
            return doc;
        } catch (ParserConfigurationException | SAXException | IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static Game loadGame(File file) {
        String filePath = file.getAbsolutePath();
        if (filePath.toUpperCase().endsWith(GameFormat.CSV.name())) {
            return CSVLoader.loadGame(filePath);
        }
        else if (filePath.toUpperCase().endsWith(GameFormat.XML.name())) {
            
        }
        else if (filePath.toUpperCase().endsWith(GameFormat.JSON.name())) {

        }
        return null;
    }

    public static Game loadNewGame(File file, Player player){
        String filePath = file.getAbsolutePath();
        if (filePath.toUpperCase().endsWith(GameFormat.CSV.name())) {
            return CSVLoader.loadNewGame(file, player);
        }
        else if (filePath.toUpperCase().endsWith(GameFormat.XML.name())) {
            
        }
        else if (filePath.toUpperCase().endsWith(GameFormat.JSON.name())) {

        }
        return null;
    }
}
