package PhysicsEngine.entities;

import PhysicsEngine.Formulas;

public class CollidableCircle extends CollidableObject {

    float radius;

    public CollidableCircle(Vec2 p, float r)
    {
        super(p, r);
        this.radius = r;
    }

    public void checkCollision(CollidableCircle circle)
    {
        float radiusSum = radius + circle.radius; // distance between the two circles when touching
        float dx = position.x - circle.position.x; // x distance
        float dy = position.y - circle.position.y; // ydistance

        // Are they closer than the distance when touching?
        boolean collided =  (radiusSum * radiusSum) > (dx*dx + dy*dy);

        // If no collision happened, do nothing
        if(!collided) return;

        // Get the unit vector between the two circles
        Vec2 normal = new Vec2(circle.position.x - position.x, circle.position.y - position.y);
        normal.normalize();

        Collision collision = new Collision(this, circle, normal);
        collision.applyImpulse();
    }

    public float getWidth(){ return radius*2; }
    public float getHeight(){ return radius*2; }

    public void checkCollision(CollidableBox box){ }
}
