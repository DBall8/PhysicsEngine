package PhysicsEngine;

import PhysicsEngine.math.Point;
import PhysicsEngine.math.Vec2;

/**
 * Class for calculating collisions for a circle
 */
class PhysicsCircle extends PhysicsObject {

    // CONSTRUCTORS ----------------------------------------------------------------------------------------------------
    PhysicsCircle(WorldSettings worldSettings, Vec2 p, float r)
    {
        super(worldSettings, p, Material.Wood, (float)(Math.PI * r * r));
        commonInit(r);
    }

    PhysicsCircle(WorldSettings worldSettings, Vec2 p, float r, Material material)
    {
        super(worldSettings, p, material, (float)(Math.PI * r * r));
        commonInit(r);
    }

    /**
     * Portion of the constructor common to each
     * @param r radius of the circle
     */
    private void commonInit(float r)
    {
        shapeType = ShapeType.CIRCLE;
        this.broadPhaseRadius = r;
    }
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Checks for a collision between a circle and a polygon
     * @param polygon
     */
    @Override
    Collision checkCollision(PhysicsPolygon polygon, float margin)
    {
        // Pass to the polygon's circle collision function
        return polygon.checkCollision(this, margin);
    }

    /**
     * Check for a collision between this circle and another
     * @param circle
     */
    @Override
    Collision checkCollision(PhysicsCircle circle, float margin)
    {
        float radiusSum = broadPhaseRadius + circle.broadPhaseRadius; // distance between the two circles when touching
        float dx = position.x - circle.position.x; // x distance
        float dy = position.y - circle.position.y; // ydistance

        // Are they closer than the distance when touching?
        float distanceSquared = dx*dx + dy*dy;
        boolean collided =  (radiusSum * radiusSum) > distanceSquared;

        // If no collision happened, do nothing
        if(!collided) return null;

        Collision collision;
        // If the distance is tiny, they are essentially in the same location, so just move the objects apart in any
        // direction
        if(distanceSquared < TINY_AMOUNT)
        {
            Vec2 normal = new Vec2(0, 1);
            collision = new Collision(this, circle, normal, broadPhaseRadius);
            collision.contactPoint = new Point(normal.x * getRadius() + position.x, normal.y *getRadius() +position.y);
        }
        // Push the circles away from each other
        else {
            float penetration = (float) (radiusSum - Math.sqrt(distanceSquared));
            // Get the unit vector between the two circles
            Vec2 normal = new Vec2(circle.position.x - position.x, circle.position.y - position.y);
            normal.normalize();

            collision = new Collision(this, circle, normal, penetration);
            collision.contactPoint = new Point(normal.x * getRadius() + position.x, normal.y *getRadius() + position.y);
        }
        return collision;
    }

    /**
     * Checks if the circle is touching the given polygon
     * @param polygon
     * @return true if they are touching
     */
    @Override
    public boolean isTouching(PhysicsPolygon polygon){
        // Pass to the polygon's circle touching method
        return polygon.isTouching(this);
    }

    /**
     * Checks if the circle is touching a given circle
     * @param circle
     * @return true if they are touching
     */
    @Override
    public boolean isTouching(PhysicsCircle circle){
        float radiusSum = broadPhaseRadius + circle.broadPhaseRadius; // distance between the two circles when touching
        float dx = position.x - circle.position.x; // x distance
        float dy = position.y - circle.position.y; // ydistance

        // Are they closer than the distance when touching?
        float distanceSquared = dx*dx + dy*dy;
        return  (radiusSum * radiusSum - TOUCHING_AMOUNT) >= distanceSquared;
    }

    @Override
    float findMaxRadius(){ return  broadPhaseRadius; }

    // Store the radius as the broad phase radius
    public float getRadius(){ return broadPhaseRadius; }
}
