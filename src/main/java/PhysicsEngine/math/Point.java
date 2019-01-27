package PhysicsEngine.math;

/**
 * Class used for storing points. Cannot be modified once created
 */
public class Point {

    private float x;
    private float y;

    public Point(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Point mult(float scalar)
    {
        return new Point(x * scalar, y * scalar);
    }

    public Point add(Vec2 other)
    {
        return new Point(x + other.x, y + other.y);
    }

    public Point add(float x, float y)
    {
        return new Point(this.x + x, this.y + y);
    }

    public boolean equals(Point other) {
        return (x == other.x) && (y == other.y);
    }

    public Point copy()
    {
        return new Point(x, y);
    }

    public Vec2 getVec() { return new Vec2(x, y); }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }
}
