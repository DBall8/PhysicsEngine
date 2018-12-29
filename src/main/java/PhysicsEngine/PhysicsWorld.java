package PhysicsEngine;

import PhysicsEngine.math.Vec2;

import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {

    private final static int INITIAL_FRAMERATE = 120; // default frame rate
    private final static float INITIAL_COLLISION_PRECISION = 50;
    private final static float TIME_SCALE_FACTOR = 120; // factor for making time run faster and adjusting forces (might need to seperate)

    private final static float TIME_STEP = 1.0f / INITIAL_FRAMERATE; // amount of time to step forward

    private List<PhysicsCircle> circles = new ArrayList<>();
    private List<PhysicsBox> boxes = new ArrayList<>();

    private float accumulator = 0;

    private WorldSettings worldSettings = new WorldSettings();

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

    public PhysicsCircle addCircle(float x, float y, float radius)
    {
        PhysicsCircle c = new PhysicsCircle(worldSettings, new Vec2(x, y), radius);
        circles.add(c);
        return c;
    }

    public PhysicsCircle addCircle(float x, float y, float radius, Material material )
    {
        PhysicsCircle c = new PhysicsCircle(worldSettings, new Vec2(x, y), radius, material);
        circles.add(c);
        return c;
    }

    public PhysicsBox addBox(float centerx, float centery, float width, float height)
    {
        PhysicsBox b = new PhysicsBox(worldSettings, new Vec2(centerx, centery), width, height);
        boxes.add(b);
        return b;
    }

    public PhysicsBox addBox(float centerx, float centery, float width, float height, Material material)
    {
        PhysicsBox b = new PhysicsBox(worldSettings, new Vec2(centerx, centery), width, height, material);
        boxes.add(b);
        return b;
    }

    public float update(float time){
        accumulator += time;

        if(accumulator > 0.5f)
        {
            accumulator = 0.5f;
        }

        applyGravity();

        while(accumulator >= TIME_STEP)
        {
            for(int i=0; i < worldSettings.getCollisionPrecision(); i++) checkCollisions(worldSettings.getScaledTimeStep());
            applyForces();
            move(worldSettings.getScaledTimeStep());
            accumulator -= TIME_STEP;
        }

        return accumulator / TIME_STEP;
    }

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

        // Check each circle against all other objects

        for(int i=0; i<circles.size(); i++)
        {
            PhysicsCircle c =  circles.get(i);
            // Have each circle check against each circle further down the list
            for(int j=i+1; j<circles.size(); j++)
            {
                c.checkCollision(circles.get(j));
            }
            // Have each circle check against each box
            for(int j=0; j<boxes.size(); j++)
            {
                c.checkCollision(boxes.get(j));
            }
        }

        // Check each box against all other boxes further down the list
        for(int i=0; i<boxes.size()-1; i++)
        {
            PhysicsBox b =  boxes.get(i);
            for(int j=i+1; j<boxes.size(); j++)
            {
                b.checkCollision(boxes.get(j));
            }
        }

        return time;
    }

    private void applyGravity()
    {
        for(PhysicsCircle circle: circles)
        {
            circle.applyGravity();
        }

        for(PhysicsBox box: boxes)
        {
            box.applyGravity();
        }
    }

    private void move(float timeStep)
    {
        for(PhysicsCircle c: circles)
        {
            c.move(timeStep);
        }

        for(PhysicsBox b: boxes)
        {
            b.move(timeStep);
        }
    }

    private void applyForces()
    {
        for(PhysicsCircle c: circles)
        {
            c.applyTotalForce();
        }

        for(PhysicsBox b: boxes)
        {
            b.applyTotalForce();
        }
    }

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
}
