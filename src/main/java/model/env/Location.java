package model.env;

public class Location {
    private int x;
    private int y;

    public Location(int x, int y){
        this.x = x;
        this.y = y; 
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static double getDistance(Location from, Location to){
        int a = from.getX() - to.getX();
        int b = from.getY() - to.getY();
        return Math.sqrt(a*a + b*b);
    }

    @Override
    public boolean equals(Object o){
        if(this.x == ((Location) o).getX() && this.y == ((Location) o).getY()){
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        return String.format("X: %s, Y: %s", x, y);
    }
}
