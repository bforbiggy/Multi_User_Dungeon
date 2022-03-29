package model.env;

import java.util.Random;

import model.entities.Entity;

public class Trap
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
    public String toString()
    {
        // Uncomment if we want traps to be invisible
        // return detected ? "x" : "X";
        return disabled ? "x" : "X";
    }
}
