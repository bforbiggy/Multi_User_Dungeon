package util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface Originator {
    public abstract Element createMemento(Document doc);
    public abstract Object loadMemento(Element memento);
}
