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

    void checkCollision(PhysicsBox box)
    {
        // Vector between circle and box centers
        Vec2 normal = new Vec2(box.getX() - position.x,
                               box.getY() - position.y);

        // Box's centers
        float xExtent = box.width / 2.0f;
        float yExtent = box.height / 2.0f;

        Vec2 closestPoint = new Vec2(Formulas.clamp(-xExtent, xExtent, normal.x),
                                     Formulas.clamp(-yExtent, yExtent, normal.y));

        boolean inside = false;
        // If the normal equals the closest point, then the circle is inside the box
        if(normal.equals(closestPoint))
        {
            inside = true;

            if(Math.abs(normal.x) > Math.abs(normal.y))
            {
                if(closestPoint.x > 0)
                    closestPoint.x = xExtent;
                else
                    closestPoint.x = -xExtent;
            }
            else
            {
                if(closestPoint.y > 0)
                    closestPoint.y = yExtent;
                else
                    closestPoint.y = -yExtent;
            }
        }

        normal = new Vec2(normal.x - closestPoint.x, normal.y - closestPoint.y);

        float distanceSquared = normal.getX() * normal.getX() + normal.getY() * normal.getY();

        if((distanceSquared > radius * radius) && !inside) return;

        // If the centers are in the same place
        Collision collision;
        if(distanceSquared < TINY_AMOUNT)
        {
            normal = new Vec2(0, 1);
            collision = new Collision(this, box, normal, radius);
        }
        else
        {
            float distance = (float)(Math.sqrt(distanceSquared));

            normal.normalize();
            if(inside)
            {
                normal.mult(-1);
            }
            collision = new Collision(this, box, normal, radius - distance);
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
    public boolean isTouching(PhysicsBox box){
        // Vector between circle and box centers
        Vec2 normal = new Vec2(box.getX() - position.x,
                box.getY() - position.y);

        // Box's centers
        float xExtent = box.width / 2.0f;
        float yExtent = box.height / 2.0f;

        Vec2 closestPoint = new Vec2(Formulas.clamp(-xExtent, xExtent, normal.x),
                Formulas.clamp(-yExtent, yExtent, normal.y));

        // If the normal equals the closest point, then the circle is inside the box
        if(normal.equals(closestPoint))
        {
            return true;
        }

        normal = new Vec2(normal.x - closestPoint.x, normal.y - closestPoint.y);

        float distanceSquared = normal.getX() * normal.getX() + normal.getY() * normal.getY();

        return distanceSquared < radius * radius;
    }

    public float getRadius(){ return radius; }
}
