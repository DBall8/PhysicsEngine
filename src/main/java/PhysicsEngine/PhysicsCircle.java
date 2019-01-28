package PhysicsEngine;

import PhysicsEngine.math.Formulas;
import PhysicsEngine.math.Vec2;

/**
 * Class for calculating collisions for a circle
 */
class PhysicsCircle extends PhysicsObject {

    private float radius;

    PhysicsCircle(WorldSettings worldSettings, Vec2 p, float r)
    {
        super(worldSettings, p, Material.Wood, (float)(Math.PI * r * r));
        shapeType = ShapeType.CIRCLE;
        this.radius = r;
    }

    PhysicsCircle(WorldSettings worldSettings, Vec2 p, float r, Material material)
    {
        super(worldSettings, p, material, (float)(Math.PI * r * r));
        shapeType = ShapeType.CIRCLE;
        this.radius = r;
    }

    @Override
    void checkCollision(PhysicsPolygon polygon)
    {
        polygon.checkCollision(this);
    }

    void checkCollision(PhysicsCircle circle)
    {
        float radiusSum = radius + circle.radius; // distance between the two circles when touching
        float dx = position.x - circle.position.x; // x distance
        float dy = position.y - circle.position.y; // ydistance

        // Are they closer than the distance when touching?
        float distanceSquared = dx*dx + dy*dy;
        boolean collided =  (radiusSum * radiusSum) > distanceSquared;

        // If no collision happened, do nothing
        if(!collided) return;

        Collision collision;
        if(distanceSquared < TINY_AMOUNT)
        {
            Vec2 normal = new Vec2(0, 1);
            collision = new Collision(this, circle, normal, radius);
        }
        else {
            float penetration = (float) (radiusSum - Math.sqrt(distanceSquared));
            // Get the unit vector between the two circles
            Vec2 normal = new Vec2(circle.position.x - position.x, circle.position.y - position.y);
            normal.normalize();

            collision = new Collision(this, circle, normal, penetration);
        }
        collision.applyImpulse();
    }

    public boolean isTouching(PhysicsPolygon polygon){
        return polygon.isTouching(this);
    }

    public boolean isTouching(PhysicsCircle circle){
        float radiusSum = radius + circle.radius; // distance between the two circles when touching
        float dx = position.x - circle.position.x; // x distance
        float dy = position.y - circle.position.y; // ydistance

        // Are they closer than the distance when touching?
        float distanceSquared = dx*dx + dy*dy;
        return  (radiusSum * radiusSum) >= distanceSquared;
    }

    public float getRadius(){ return radius; }
}
