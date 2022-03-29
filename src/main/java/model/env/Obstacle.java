package model.env;

public class Obstacle
{
    private String name;
    private String obsString;

    public Obstacle()
    {
        this.name = "obstacle";
        this.obsString = "@";
    }

    public Obstacle(String name, String obsString)
    {
        this.name = name;
        this.obsString = obsString;
    }

    @Override
    public String toString()
    {
        return obsString;
    }
}
