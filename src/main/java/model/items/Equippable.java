package model.items;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import model.entities.Entity;
import model.entities.Stats;

public class Equippable extends Item
{
    private Stats stats;
    private EquipTag tag;

    public Equippable(String name, String description, int value, Stats stats, EquipTag tag)
    {
        super(name, description, value);
        this.stats = stats;
        this.tag = tag;
    }

    public void equip(Entity target)
    {
        target.getStats().plus(stats);
    }

    public void unequip(Entity target)
    {
        target.getStats().subtract(stats);
    }

    public static Equippable generateItem()
    {
        EquipTag tag = EquipTag.values()[randy.nextInt(EquipTag.values().length)];
        Stats stats = new Stats(randy.nextInt(10-5)+5, randy.nextInt(5-1)+1, randy.nextInt(5-1)+1);

        String noun = tag.toString();
        String verb = verbs[randy.nextInt(verbs.length)];
        String adjective = adjectives[randy.nextInt(adjectives.length)];

        String name = String.format("%s %s of %s", adjective, noun, verb);
        String description = String.format("I sure do hope this %s of %s doesn't debuff me like crazy!", noun, verb);
        int value = randy.nextInt(40-10)+10;

        return new Equippable(name, description, value, stats, tag);
    }

    public Stats getStats(){
        return stats;
    }

    public EquipTag getEquipTag(){
        return tag;
    }

    @Override
    public Element createMemento(Document doc) {
        Element itemElem = super.createMemento(doc);
        itemElem.setAttribute("type", "equippable");
        itemElem.appendChild(stats.createMemento(doc));
        return itemElem;
    }

    @Override
    public Equippable loadMemento(Element element){
        return null;
    }
}
