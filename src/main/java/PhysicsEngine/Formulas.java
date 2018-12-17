package PhysicsEngine;

import PhysicsEngine.entities.Vec2;

public class Formulas {

    public static float dotProduct(Vec2 vector1, Vec2 vector2)
    {
        return (vector1.getX() * vector2.getX()) + (vector1.getY() * vector2.getY());
    }
}
