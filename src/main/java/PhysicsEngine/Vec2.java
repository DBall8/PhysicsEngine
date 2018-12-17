package PhysicsEngine;

public class Vec2 {
    public float x;
    public float y;

    public Vec2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void mult(float scalar)
    {
        x *= scalar;
        y *= scalar;
    }

    public boolean equals(Vec2 other) {
        return (x == other.x) && (y == other.y);
    }

    public float magnitude()
    {
        return (float)Math.sqrt(x*x + y*y);
    }

    public void normalize()
    {
        float magnitude = magnitude();
        x /= magnitude;
        y /= magnitude;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }
}
