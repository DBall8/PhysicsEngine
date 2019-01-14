package PhysicsEngine.math;

public class Formulas {

    static final float TWO_PI = (float)(2.0 * Math.PI);

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

    public static float getXComponent(float magnitude, float angleInRadians)
    {
        return (float)(magnitude * Math.sin(angleInRadians));
    }

    public static float getYComponent(float magnitude, float angleInRadians)
    {
        return (float)(-1.0f * magnitude * Math.cos(angleInRadians));
    }

    public static float cross(Vec2 v1, Vec2 v2)
    {
        return (v1.getX() * v2.getY()) - (v2.getX() * v1.getY());
    }

    public static float normalizeAngle(float angleInRads)
    {

        while(angleInRads < 0)
        {
            angleInRads += TWO_PI;
        }

        while(angleInRads > TWO_PI)
        {
            angleInRads -= TWO_PI;
        }

        return angleInRads;
    }
}
