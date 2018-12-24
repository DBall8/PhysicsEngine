package PhysicsEngine;

public class Formulas {

    public static float dotProduct(Vec2 vector1, Vec2 vector2)
    {
        return (vector1.getX() * vector2.getX()) + (vector1.getY() * vector2.getY());
    }

    public static float clamp(float minRange, float maxRange, float value)
    {
        return Math.max(minRange, Math.min(maxRange, value));
    }

    public static Vec2 vecMult(Vec2 v, float scalar)
    {
        return new Vec2(v.x * scalar, v.y * scalar);
    }

    public static Vec2 vecAdd(Vec2 v1, Vec2 v2)
    {
        return new Vec2(v1.x + v2.x, v1.y + v2.y);
    }

    public static Vec2 vecAdd(Vec2 v, float scalar)
    {
        return new Vec2(v.x + scalar, v.y + scalar);
    }
}
