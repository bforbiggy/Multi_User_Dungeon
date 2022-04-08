package model.entities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import model.Originator;

public class Stats implements Originator
{
    public static final Stats ZERO = new Stats(0, 0, 0);

    public int health;
    public int attack;
    public int defense;

    public Stats(int health, int attack, int defense)
    {
        this.health = health;
        this.attack = attack;
        this.defense = defense;
    }

    public Stats plus(Stats other)
    {
        health += other.health;
        attack += other.attack;
        defense += other.defense;
        return this;
    }

    public Stats subtract(Stats other)
    {
        health -= other.health;
        attack -= other.attack;
        defense -= other.defense;
        return this;
    }

    // Ideally, stats contains double values that're converted to integers
    // This way we can multiply stats appropriately
    public Stats multiply(double[] multipliers)
    {
        health *= multipliers[0];
        attack *= multipliers[1];
        defense *= multipliers[2];
        return this;
    }

    public static Stats plus(Stats x, Stats y){
        return new Stats(x.health + y.health, x.attack + y.attack, x.defense + y.defense);
    }

    public static Stats subtract(Stats x, Stats y){
        return new Stats(x.health - y.health, x.attack - y.attack, x.defense - y.defense);
    }

    public Stats copy(){
        return new Stats(health, attack, defense);
    }

    @Override
    public Element createMemento(Document doc){
        Element statsElem = doc.createElement("stats");
        statsElem.setAttribute("health", Integer.toString(health));
        statsElem.setAttribute("attack", Integer.toString(attack));
        statsElem.setAttribute("defense", Integer.toString(defense));
        return statsElem;
    }

    public static Stats convertMemento(Element element){
        int health = Integer.parseInt(element.getAttribute("health"));
        int attack = Integer.parseInt(element.getAttribute("attack"));
        int defense = Integer.parseInt(element.getAttribute("defense"));
        return new Stats(health, attack, defense);
    }

    public String[] toStringArray()
    {
        String[] stringArray = {
            "Health: = " + health,
            "Attack: = " + attack,
            "Defense: = " + defense
        };
        return stringArray;
    }
}
