package model.entities;

import model.env.Location;
import model.env.Tile;
import model.items.Bag;
import model.items.Inventory;

public class Player extends Entity {
    public static Stats DEFAULT_STATS = new Stats(100, 10, 0);
    public static int BAG_CAPACITY = 6;

    private static final Bag defaultBag = new Bag("Bag of poggers", "This bag is so poggers omg!!!!! yassss!!!", 5, 6);

    public Player(String name, String description, Stats stats) {
        super(name, description, stats);
        inventory = new Inventory(BAG_CAPACITY);
        inventory.addItem(defaultBag);
    }

    public Player(String name, String description, Stats stats, Inventory inventory) {
        super(name, description, stats);
        this.inventory = inventory;
    }

    // Given player location and destination, determine if player can make said move
    public boolean validateMove(Location from, Tile toTile) {
        Location to = toTile.getLocation();
        boolean inRange = Math.abs(from.getX() - to.getX()) <= 1 && Math.abs(from.getY() - to.getY()) <= 1;
        boolean unOccupied = toTile.occupant == null;
        return inRange && unOccupied;
    }

    @Override
    public String toString() {
        return "P";
    }
}
