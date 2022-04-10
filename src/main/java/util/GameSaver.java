package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.css.DocumentCSS;
import model.Game;

public class GameSaver {
    public static void writeDocument(Document document, StreamResult destStream) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            transformer.transform(source, destStream);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static Document gameToDoc(Game game) {
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = docBuilder.newDocument();
            document.appendChild(game.createMemento(document));
            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File saveGame(Game game, File dest) {
        String filePath = dest.getAbsolutePath();
        if (filePath.toUpperCase().endsWith(GameFormat.CSV.name())) {
            try (FileWriter fileWriter = new FileWriter(dest)){
                fileWriter.write(CSVSaver.gameToString(game));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (filePath.toUpperCase().endsWith(GameFormat.XML.name())) {
            try {
                DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document document = docBuilder.newDocument();
                document.appendChild(game.createMemento(document));

                StreamResult destStream = new StreamResult(dest);
                writeDocument(document, destStream);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
        else if (filePath.toUpperCase().endsWith(GameFormat.JSON.name())) {

        }
        return null;
    }
}
