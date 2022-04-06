package util;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
}
