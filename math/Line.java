package physicsEngine.math;

public class Line {
    private float slope;
    private float b;
    private boolean infiniteSlope;

    public Line(float slope, float b)
    {
        this.slope = slope;
        this.b = b;
        infiniteSlope = false;
    }

    public Line(float b)
    {
        this.b = b;
        this.infiniteSlope = true;
    }

    public Point findIntersection(Line line)
    {

        if((infiniteSlope && line.infiniteSlope) ||
            (slope == line.slope && !infiniteSlope && !line.infiniteSlope))
        {
            // both lines have the same slope and will not intersect
            return null;
        }

        float x;
        float y;
        // If this line is vertical
        if(infiniteSlope)
        {
            x = b;
            y = (line.slope * x) + line.b;
        }
        // If the other line is vertical
        else if(line.infiniteSlope)
        {
            x = line.b;
            y = (slope * x) + b;
        }
        // If neither line is vertical
        else
        {
            x = (line.b - b) / (slope - line.slope);
            y = (slope * x) + b;
        }

        return new Point(x, y);
    }

    // TODO add exceptions
    public float getYAt(float x)
    {
        // Infinite slope
        if(infiniteSlope)
        {
            // EXCEPTION
            return 0;
        }

        return (slope * x) + b;
    }

    // TODO add exceptions
    public float getXAt(float y)
    {

        if(slope == 0)
        {
            // EXCEPTION
            return 0;
        }

        return (y - b) / slope;
    }

    public boolean isVertical() {
        return infiniteSlope;
    }
    public float getYIntercept(){ return b; }
    public float getSlope(){ return slope; }
}
