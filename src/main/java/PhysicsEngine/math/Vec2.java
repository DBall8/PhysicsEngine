package PhysicsEngine.math;

public class Vec2 {
    public float x;
    public float y;

    public Vec2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Vec2 mult(float scalar)
    {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public Vec2 add(Vec2 other)
    {
        x += other.x;
        y += other.y;
        return this;
    }

    public Vec2 add(float x, float y)
    {
        this.x += x;
        this.y += y;
        return this;
    }

    public void zero()
    {
        x = 0;
        y = 0;
    }

    public boolean equals(Vec2 other) {
        return (x == other.x) && (y == other.y);
    }

    public float magnitude()
    {
        return (float)Math.sqrt(x*x + y*y);
    }

    public Vec2 normalize()
    {
        float magnitude = magnitude();
        if(magnitude == 0) return this;
        x /= magnitude;
        y /= magnitude;

        return this;
    }

    public Vec2 copy()
    {
        return new Vec2(x, y);
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
