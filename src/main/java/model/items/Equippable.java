package model.items;

import model.entities.Entity;
import model.entities.Stats;

public class Equippable extends Item
{
    public enum Equip_Tag
    {
        ARMOR("armor"),
        WEAPON("weapon");
        public static final Equip_Tag[] tags = Equip_Tag.values();

        private String tagName;
        private Equip_Tag(String tagName){this.tagName = tagName;}
        public String toString(){return tagName;}

        public static int tagToIndex(Equip_Tag tag)
        {
            for(int i = 0; i < tags.length; i++)
                if(tag == tags[i])
                    return i;
            return -1;
        }
    }

    private Stats stats;
    private Equip_Tag tag;

    public Equippable(String name, String description, int value, Stats stats, Equip_Tag tag)
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
        Equip_Tag tag = Equip_Tag.tags[randy.nextInt(Equip_Tag.tags.length)];
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

    public Equip_Tag getEquipTag(){
        return tag;
    }
}
