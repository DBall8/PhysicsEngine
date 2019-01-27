package PhysicsEngine;

import PhysicsEngine.math.Formulas;
import PhysicsEngine.math.Vec2;

/**
 * Base class for calculation collisions between objects
 */
public abstract class PhysicsObject{

    public final static float MASS_SCALING_FACTOR = 100.0f; // Scale for making masses closer to velocities for less
                                                            // floating point errors
    private final static float GRAVITY_SCALAR = 0.01f; // Scale for making gravity seem normal at about 10 units
    protected final static float TINY_AMOUNT = 0.01f; // A value to consider "close enough"

    Vec2 position; // Position of the object
    Vec2 totalForce; // Sum of all forces currently acting on the object

    float orientation; // current angle (in radians)
    float angularVelocity;
    float torque;

    float xvelocity;
    float yvelocity;
    float volume;

    Material material;
    float mass;
    float invertedMass = 0.05f;

    WorldSettings worldSettings;

    protected PhysicsObject(WorldSettings worldSettings, Vec2 p, Material material, float volume)
    {
        this.worldSettings = worldSettings;
        position = p;
        this.angularVelocity = 0;
        this.orientation = 0;
        this.torque = 0;
        this.xvelocity = 0;
        this.yvelocity = 0;
        this.material = material;
        this.volume = volume;
        this.totalForce = new Vec2(0, 0);
        this.mass = material.getDensity() * volume;
        if(material.getDensity() == 0)
        {
            invertedMass = 0;
        }
        else {
            invertedMass = MASS_SCALING_FACTOR / mass;
        }
    }

    void move(float timeStep)
    {
        position.x = position.x + xvelocity * timeStep;
        position.y = position.y + yvelocity * timeStep;
    }

    void applyGravity()
    {
        float strength = worldSettings.getGravity() * GRAVITY_SCALAR * mass / (MASS_SCALING_FACTOR);
        Vec2 gravity = Formulas.vecMult(worldSettings.getGravityDirection(), strength);
        applyForce(gravity.x, gravity.y);
    }

    void applyTotalForce()
    {
        xvelocity += invertedMass * totalForce.x;
        yvelocity += invertedMass * totalForce.y;
        totalForce.zero();
    }

    // Accessible to users ---------------------------------------------------------------------------------------------

    public void applyForce(float xcomponent, float ycomponent)
    {
        Vec2 force = new Vec2(xcomponent, ycomponent);
        force.mult(worldSettings.getForceScaleFactor());
        totalForce.add(force);
    }

    public void applyForceInDirection(float magnitude, float angleInRadians)
    {
        Vec2 force = new Vec2(Formulas.getXComponent(magnitude, angleInRadians),
                Formulas.getYComponent(magnitude, angleInRadians));
        force.mult(worldSettings.getForceScaleFactor());
        totalForce.add(force);
    }

    public boolean isTouching(PhysicsObject object)
    {
        if(object instanceof PhysicsCircle) {
            return isTouching((PhysicsCircle) object);
        }
        else if(object instanceof PhysicsBox)
        {
            return isTouching((PhysicsBox) object);
        }
        else
        {
            return isTouching((PhysicsPolygon) object);
        }
    }

    public boolean isGrounded()
    {
        return false;
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Getters and setters ---------------------------------------------------------------------------------------------

    public void setXvelocity(float xvel) { this.xvelocity = xvel; }
    public void setYvelocity(float yvel) { this.yvelocity = yvel; }
    public void setMaterial(Material material)
    {
        this.material = material;
        this.invertedMass = MASS_SCALING_FACTOR / (material.getDensity() * volume);
    }

    float getRestitution(){ return material.getRestitution(); }
    float getInvertedMass(){ return invertedMass; }
    public float getX(){ return position.getX(); }
    public float getY(){ return position.getY(); }
    public float getXvelocity() { return xvelocity; }
    public float getYvelocity() { return yvelocity; }
    public float getOrientation() { return orientation; }
    float getStaticFriction() { return material.getStaticFriction(); }
    float getDynamicFriction() { return material.getDynamicFriction(); }
    public float getMass() { return mass; }
    public float getVelocity()
    {
        float x2 = xvelocity*xvelocity;
        float y2 = yvelocity*yvelocity;
        return (float)Math.sqrt(x2 + y2);
    }

    abstract void checkCollision(PhysicsCircle circle);
    abstract void checkCollision(PhysicsPolygon polygon);
    abstract void checkCollision(PhysicsBox box);
    public abstract boolean isTouching(PhysicsCircle circle);
    public abstract boolean isTouching(PhysicsBox box);
    public abstract boolean isTouching(PhysicsPolygon polygon);

    // DEBUG TODO REMOVE
    // TODO DELETE THIS
    public void setOrientation(float o){ this.orientation = o; }
}
