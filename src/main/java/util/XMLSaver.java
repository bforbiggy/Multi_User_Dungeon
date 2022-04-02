package util;

import java.io.File;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import model.Game;
import model.entities.*;
import model.env.Map;
import model.env.Room;
import model.items.*;

public class XMLSaver {
    public static Element createElemWithText(Document document, String tagName, String data) {
        Element elem = document.createElement(tagName);
        elem.appendChild(document.createTextNode(data));
        return elem;
    }

    // Converts stats to element
    public static Element statsToElement(Document document, Stats stats) {
        Element statElem = document.createElement("stats");
        statElem.appendChild(createElemWithText(document, "health", Integer.toString(stats.health)));
        statElem.appendChild(createElemWithText(document, "attack", Integer.toString(stats.attack)));
        statElem.appendChild(createElemWithText(document, "defense", Integer.toString(stats.defense)));
        return statElem;
    }

    // Converts item to element
    public static Element itemToElement(Document document, Item item) {
        Element itemElem = document.createElement("item");

        // Add item name, description value
        itemElem.appendChild(createElemWithText(document, "name", item.getName()));
        itemElem.appendChild(createElemWithText(document, "description", item.getDescription()));
        itemElem.appendChild(createElemWithText(document, "value", Integer.toString(item.getValue())));

        // Adds type-specific data
        if (item instanceof Bag bag) {
            itemElem.setAttribute("type", "bag");

            // Add bag capacity
            itemElem.appendChild(createElemWithText(document, "capacity", Integer.toString(bag.getCapacity())));
        } else if (item instanceof Consumable consumable) {
            itemElem.setAttribute("type", "consumable");

            // Add consumable stats and duration attribute if applicable
            itemElem.appendChild(statsToElement(document, consumable.getStats()));
            if (consumable.getDuration() != 0)
                itemElem.setAttribute("duration", Integer.toString(consumable.getDuration()));
        } else if (item instanceof Equippable equippable) {
            itemElem.setAttribute("type", "equippable");

            // Add equipment stat and equipment type
            itemElem.appendChild(statsToElement(document, equippable.getStats()));
            itemElem.setAttribute("equipType", equippable.getEquipTag().name());
        } else {
            itemElem.setAttribute("type", "item");
        }
        return itemElem;
    }

    // Includes name, desc, stats, items
    public static Element entityToElement(Document document, Entity entity) {
        Element entityElem = document.createElement("entity");

        // Add entity name and description
        entityElem.appendChild(createElemWithText(document, "name", entity.getName()));
        entityElem.appendChild(createElemWithText(document, "description", entity.getDescription()));

        // Add entity stats
        Element statElem = statsToElement(document, entity.getStats());
        entityElem.appendChild(statElem);

        // Add entity items
        Element itemsElem = document.createElement("items");
        Inventory inventory = entity.getInventory();
        for (Bag bag : inventory.bags)
            for (Item item : bag.items)
                itemsElem.appendChild(itemToElement(document, item));
        entityElem.appendChild(itemsElem);

        // Add entity type attribute
        if (entity instanceof NPC)
            entityElem.setAttribute("type", "npc");
        else if (entity instanceof Player)
            entityElem.setAttribute("type", "player");
        else
            entityElem.setAttribute("type", "entity");

        return entityElem;
    }

    public static void writeDocument(Document document, String outputPath)
            throws TransformerFactoryConfigurationError, TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(outputPath));
        transformer.transform(source, result);
    }

    public static void savePlayer(Player player, String outputPath) {
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = docBuilder.newDocument();

            // Create basic player element
            Element playerElem = entityToElement(document, player);

            // Add player's equipments
            Inventory inventory = player.getInventory();
            Element equips = document.createElement("equips");
            for (Equippable equippable : inventory.equipment.values())
                equips.appendChild(itemToElement(document, equippable));
            playerElem.appendChild(equips);

            // Add player's bags
            Element bagsElem = document.createElement("bags");
            for (Bag bag : inventory.bags)
                bagsElem.appendChild(itemToElement(document, bag));
            playerElem.appendChild(bagsElem);

            document.appendChild(playerElem);

            // Writes document to file
            writeDocument(document, outputPath);
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Element roomToElem(Document document, Room room) {
        Element roomElem = document.createElement("room");
        return roomElem;
    }

    public static void saveMap(Map map, String outputPath) {
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = docBuilder.newDocument();

            // Create root element
            Element mapElem = document.createElement("map");

            // Convert each room into element for map
            for (Room room : map.rooms) {
                mapElem.appendChild(roomToElem(document, room));
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
