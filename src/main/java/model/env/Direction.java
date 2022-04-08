package model.env;

public enum Direction {
    NORTH, SOUTH, WEST, EAST;
    
    public Direction getOpposite(){
        switch(this){
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case WEST: return EAST;
            case EAST: return WEST;
            default: return null;
        }
    }

    public static Direction locToDirection(Location location, int width, int height)
    {
        if(location.getY() == 0){
            return NORTH;
        }
        else if(location.getY() == height-1){
            return SOUTH;
        }
        else if(location.getX() == 0){
            return WEST;
        }
        else if(location.getX() == width-1){
            return EAST;
        }
        else{
            return null;
        }
    }
}