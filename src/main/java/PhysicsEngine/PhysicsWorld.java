package PhysicsEngine;

import PhysicsEngine.math.MalformedPolygonException;
import PhysicsEngine.math.Point;
import PhysicsEngine.math.Polygon;
import PhysicsEngine.math.Vec2;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for containing all physics calculations for a set of objects with a set of settings.
 */
public class PhysicsWorld {

    private final static int INITIAL_FRAMERATE = 120; // default frame rate
    private final static float INITIAL_COLLISION_PRECISION = 50;
    private final static float TIME_SCALE_FACTOR = 120; // factor for making time run faster and adjusting forces (might need to seperate)

    private final static float TIME_STEP = 1.0f / INITIAL_FRAMERATE; // amount of time to step forward

    // List of all physics objects to simulate
    private List<PhysicsObject> objects = new ArrayList<>();
    private List<BroadPair> broadPhase = new ArrayList<>();

    // Value for accumulating needed physics updates
    private float accumulator = 0;

    // Class for holding the various settings used in this world, such as gravity strength etc.
    private WorldSettings worldSettings = new WorldSettings();

    // CONSTRUCTORS ----------------------------------------------------------------------------------------------------
    public PhysicsWorld(float gravity, boolean friction){
        worldSettings.setGravity(gravity);
        worldSettings.setFriction(friction);
        setCollisionPrecision(INITIAL_COLLISION_PRECISION);
        setUpdatesPerFrame(INITIAL_FRAMERATE);
    }
    public PhysicsWorld(float gravity){
        worldSettings.setGravity(gravity);
        setCollisionPrecision(INITIAL_COLLISION_PRECISION);
        setUpdatesPerFrame(INITIAL_FRAMERATE);
    }
    public PhysicsWorld()
    {
        setCollisionPrecision(INITIAL_COLLISION_PRECISION);
        setUpdatesPerFrame(INITIAL_FRAMERATE);
    }
    // -----------------------------------------------------------------------------------------------------------------

    // Object creation methods -----------------------------------------------------------------------------------------
    public PhysicsObject addCircle(float x, float y, float radius)
    {
        PhysicsCircle c = new PhysicsCircle(worldSettings, new Vec2(x, y), radius);
        objects.add(c);
        return c;
    }

    public PhysicsObject addCircle(float x, float y, float radius, Material material )
    {
        PhysicsCircle c = new PhysicsCircle(worldSettings, new Vec2(x, y), radius, material);
        objects.add(c);
        return c;
    }

    public PhysicsObject addBox(float centerx, float centery, float width, float height)
    {
        try {
            Polygon polygon = new Polygon(new Point[]{
                    new Point(0, 0),
                    new Point(width, 0),
                    new Point(width, height),
                    new Point(0, height),
            });
            PhysicsPolygon b = new PhysicsPolygon(worldSettings, new Vec2(centerx, centery), polygon);
            objects.add(b);
            return b;
        }
        catch (MalformedPolygonException e)
        {
            e.printMessage();
            return null;
        }
    }

    public PhysicsObject addBox(float centerx, float centery, float width, float height, Material material)
    {
        try {
            Polygon polygon = new Polygon(new Point[]{
                    new Point(0, 0),
                    new Point(width, 0),
                    new Point(width, height),
                    new Point(0, height),
            });
            PhysicsPolygon b = new PhysicsPolygon(worldSettings, new Vec2(centerx, centery), polygon, material);
            objects.add(b);
            return b;
        }
        catch (MalformedPolygonException e)
        {
            e.printMessage();
            return null;
        }
    }

    public PhysicsObject addPolygon(float centerx, float centery, Point[] points)
    {
        try
        {
            PhysicsPolygon p = new PhysicsPolygon(worldSettings, new Vec2(centerx, centery), new Polygon(points));
            objects.add(p);
            return p;
        }
        catch (MalformedPolygonException e)
        {
            System.err.println("ERROR: Points do not form a valid polygon.");
            return null;
        }
    }

    public PhysicsObject addPolygon(float centerx, float centery, Point[] points, Material material)
    {
        try
        {
            PhysicsPolygon p = new PhysicsPolygon(worldSettings, new Vec2(centerx, centery), new Polygon(points), material);
            objects.add(p);
            return p;
        }
        catch (MalformedPolygonException e)
        {
            System.err.println("ERROR: Points do not form a valid polygon.");
            return null;
        }
    }

    public PhysicsObject addPolygon(float centerx, float centery, Polygon polygon)
    {
        PhysicsPolygon p = new PhysicsPolygon(worldSettings, new Vec2(centerx, centery), polygon);
        objects.add(p);
        return p;
    }

    public PhysicsObject addPolygon(float centerx, float centery, Polygon polygon, Material material)
    {
        PhysicsPolygon p = new PhysicsPolygon(worldSettings, new Vec2(centerx, centery), polygon, material);
        objects.add(p);
        return p;
    }
    // -----------------------------------------------------------------------------------------------------------------

    // Physics calculation methods -------------------------------------------------------------------------------------

    /**
     * Updates all object's positions and state
     * @param time the time step forward to calculate
     * @return returns an alpha value to show the remaining portion of a time step not calculated, to use for estimating
     * positions outside of the physics engine to allow for smoother animations
     */
    public float update(float time){
        // add the time step to the accumulator
        accumulator += time;

        // If the accumulator has risen to large, cap it at 0.5
        if(accumulator > 0.5f)
        {
            accumulator = 0.5f;
        }

        // First apply the force of gravity on every object
        applyGravity();

        // As long as enough "time" is left in the accumulator "tank", consume a timestep's worth and update the world
        while(accumulator >= TIME_STEP)
        {
            // Check for all collisions and update, but repeat multiple times to allow impulses to propogate through
            // (Mostly needed for large stacks of objects)
            for(int i=0; i < worldSettings.getCollisionPrecision(); i++)
            {
                checkCollisions(worldSettings.getScaledTimeStep());
                applyForces();
            }

            // Update each object's position
            move(worldSettings.getScaledTimeStep());
            // Take the "time" from the accumulator "tank"
            accumulator -= TIME_STEP;
        }

        // If there is still "time" left in the accumulator but not enough for a full time step, return a value to use
        // to guess each movement forward to allow for smoother animations
        return accumulator / TIME_STEP;
    }

    /**
     * Have each object check for a collision between all other objects
     * @param time UNUSED - needed for continuous collision detection
     * @return UNUSED
     */
    private float checkCollisions(float time){
//        float firstCollisionTime = timeLeft; // looks for first collision
//        float tempTime;
//        // reset collisions for each player
//        for(Entity e: objects){
//            e.reset();
//            if((tempTime = e.checkCollisions(timeLeft, obstacles)) < firstCollisionTime){
//                firstCollisionTime = tempTime;
//            }
//        }
//        return firstCollisionTime;

        // Empty broadPhase list
        broadPhase.clear();

        // Check each collision pair using a circle around the entire shape. If the circles collide, save it as a
        // potential collision to check
        for(int i=0; i<objects.size(); i++)
        {
            PhysicsObject o1 = objects.get(i);
            // Have each polygon check against each polygon further down the list
            for(int j=i+1; j<objects.size(); j++)
            {
                PhysicsObject o2 = objects.get(j);
                if(broadCheck(o1, o2)){
                    broadPhase.add(new BroadPair(o1, o2));
                }
            }
        }

        // Check each object against all other objects further down the list
        // This prevents checking A vs B and then B vs A again later
        for(int i=0; i<broadPhase.size(); i++)
        {
            BroadPair potentialCollision = broadPhase.get(i);
            potentialCollision.object1.checkCollision(potentialCollision.object2);
        }

        return time;
    }

    boolean broadCheck(PhysicsObject o1, PhysicsObject o2)
    {
        float radiusSum = o1.broadPhaseRadius + o2.broadPhaseRadius; // distance between the two circles when touching
        float dx = o1.getX() - o2.getX(); // x distance
        float dy = o1.getY() - o2.getY(); // ydistance

        // Are they closer than the distance when touching?
        float distanceSquared = dx*dx + dy*dy;
        return  distanceSquared <= (radiusSum * radiusSum);
    }

    /**
     * Apply gravity to all objects
     */
    private void applyGravity()
    {
        for(PhysicsObject object: objects)
        {
            object.applyGravity();
        }
    }

    /**
     * Move all objects by the given time step
     * @param timeStep the portion of time to move the objects by
     */
    private void move(float timeStep)
    {
        for(PhysicsObject o: objects)
        {
            o.move(timeStep);
        }
    }

    /**
     * Have each object apply the forces accumulated from collision detection
     * This transfers the force into resulting velocities
     */
    private void applyForces()
    {
        for(PhysicsObject o: objects)
        {
            o.applyTotalForce();
        }
    }
    // -----------------------------------------------------------------------------------------------------------------

    // SETTERS ---------------------------------------------------------------------------------------------------------
    public void setUpdatesPerFrame(int updates)
    {
        float timeStep = 1.0f / updates;
        worldSettings.setScaledTimeStep(timeStep * TIME_SCALE_FACTOR);
        worldSettings.setForceScaleFactor(updates / TIME_SCALE_FACTOR);
    }

    public void setCollisionPrecision(float precision)
    {
        worldSettings.setCollisionPrecision(precision);
    }

    public void setGravityDirection(float xcomponent, float ycomponent)
    {
        Vec2 gravity = new Vec2(xcomponent, ycomponent);
        gravity.normalize();
        worldSettings.setGravityDirection(gravity);
    }

    public void setGravity(float gravity){ worldSettings.setGravity(gravity); }
    public void setFriction(boolean friction){ worldSettings.setFriction(friction); }

    private class BroadPair{
        PhysicsObject object1;
        PhysicsObject object2;

        BroadPair(PhysicsObject object1, PhysicsObject object2)
        {
            this.object1 = object1;
            this.object2 = object2;
        }
    }
}
