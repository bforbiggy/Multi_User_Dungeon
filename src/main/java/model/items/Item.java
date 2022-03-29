package model.items;

import java.util.Random;

public class Item 
{
    protected static Random randy = new Random();
    protected static String[] nouns = {"kneecap", "bones", "token", "marker", "laptop"};
    protected static String[] adjectives = {"awesome", "swaggalicious", "poggers", "zany", "epic"};
    protected static String[] verbs = {"doing things", "kicking", "escaping", "smiling", "programming"};

    protected String name;
    protected String description;
    protected int value;

    public Item(String name, String description, int value)
    {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getValue(){
        return value;
    }

    public static Item generateItem()
    {
        String noun = nouns[randy.nextInt(nouns.length)];
        String verb = verbs[randy.nextInt(verbs.length)];
        String adjective = adjectives[randy.nextInt(adjectives.length)];

        String name = String.format("%s %s of %s", adjective, noun, verb);
        String description = String.format("So you mean to tell me this %s can %s? Ain't no way!", noun, verb);
        int value = randy.nextInt(30);

        return new Item(name, description, value);
    }

    @Override
    public String toString()
    {
        return name + ": " + description;
    }
}
