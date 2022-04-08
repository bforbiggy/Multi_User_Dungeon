package model.env;

import java.util.Random;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import model.GameObject;
import model.entities.Entity;

public class Trap implements GameObject
{
    private static Random randy = new Random();

    private boolean detected = false;
    private boolean disabled = false;
    private int attack;

    public Trap(){
        this.attack = randy.nextInt(10-1)+1;
    }
    public Trap(int attackVal) {
        this.attack = attackVal;
    }
    private Trap(int attackVal, boolean detected, boolean disabled) {
        this.attack = attackVal;
        this.disabled = disabled;
        this.detected = detected;
    }

    /**
     * The action of disarming a trap, native to the trap file
     * Takes a 1/2 chance and if failed, forces the player to take damage
     * 
     * @param player The player standing near the trap
     * @return If the trap was disarmed
     */
    public boolean disarmTrap(Entity player) {
        boolean chance = randy.nextBoolean();
        disabled = true;
        detected = true;
        if (chance) {
            System.out.println("You disarm the trap!");
            return true;
        }
        // The trap was not successfully disarmed
        else {
            player.takeDamage(attack);
            System.out.println("You failed to disarm the trap and it deals " + attack + " damage!");
            return false;
        }
    }

    /**
     * Perform trap detection on this trap.
     * @return Whether or not the trap was detected
     */
    public boolean detectTrap() {
        detected = randy.nextBoolean();
        if (detected) {
            System.out.println("You detected a trap and try to disarm it!");
            return true;
        }
        return false;
    }

    public boolean getDisabled() {
        return disabled;
    }

    public boolean getDetected(){
        return detected;
    }

    public int getAttack(){
        return attack;
    }

    @Override
    public Element createMemento(Document doc) {
        Element trapElem = doc.createElement("trap");
        trapElem.setAttribute("attack", Integer.toString(attack));
        trapElem.setAttribute("detected", Boolean.toString(detected));
        trapElem.setAttribute("disabled", Boolean.toString(disabled));
        return trapElem;
    }

    public static Trap convertMemento(Element element){
        int attack = Integer.parseInt(element.getAttribute("attack"));
        boolean detected = Boolean.parseBoolean(element.getAttribute("detected"));
        boolean disabled = Boolean.parseBoolean(element.getAttribute("disabled"));
        return new Trap(attack, detected, disabled);
    }

    @Override
    public String toString(){
        if(!detected) return " ";
        return disabled ? "x" : "X";
    }
    
}
