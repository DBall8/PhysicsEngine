package PhysicsEngine;

import PhysicsEngine.math.Formulas;
import PhysicsEngine.math.Vec2;

/**
 * Class for calculation collisions between objects
 */
public abstract class PhysicsObject{

    public final static float MASS_SCALING_FACTOR = 100.0f; // Scale for making masses closer to velocities for less
                                                            // floating point errors
    private final static float GRAVITY_SCALAR = 0.01f; // Scale for making gravity seem normal at about 10 units
    protected final static float TINY_AMOUNT = 0.01f; // A value to consider "close enough"
    protected final static float TOUCHING_AMOUNT = 0.1f;

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
    float broadPhaseRadius = 0; // Radius at the furthest point from the shape's center

    ShapeType shapeType = ShapeType.INVALID; // Enum for tracking which collision methods to use (circle vs polygon)
    WorldSettings worldSettings; // Contains all the world environment settings for the world the object exists in

    boolean debug = false;

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

    /**
     * Moves and rotates the objects
     * @param timeStep the amount of time to move the object forward through
     */
    void move(float timeStep)
    {
        // Update position
        position.x = position.x + xvelocity * timeStep;
        position.y = position.y + yvelocity * timeStep;
    }

    /**
     * Applies a force of "gravity", which is just a weight independent continual force in a given direction.
     * The strength and direction of gravity is set by the world settings
     */
    void applyGravity()
    {
        float strength = worldSettings.getGravity() * GRAVITY_SCALAR * mass / (MASS_SCALING_FACTOR);
        Vec2 gravity = Formulas.vecMult(worldSettings.getGravityDirection(), strength);
        applyForce(gravity.x, gravity.y);
    }

    /**
     * Takes the total force vector of the objects and applies it to the object, resulting in a change in velocities
     */
    void applyTotalForce()
    {
        // Update velocities
        xvelocity += invertedMass * totalForce.x;
        yvelocity += invertedMass * totalForce.y;

        // Zero out total force vector since they have been applied
        totalForce.zero();
    }

    /**
     * Checks for a collision between two objects and applies an impulse if one has occurred
     * @param object The other object to check a collision against
     */
    void checkCollision(PhysicsObject object)
    {
        // Pass to the sub-class's collision check
        switch (object.shapeType)
        {
            case POLYGON:
                checkCollision((PhysicsPolygon)object);
                break;
            case CIRCLE:
                checkCollision((PhysicsCircle) object);
                break;
            default:
                System.err.println("INVALID OBJECT TYPE in check collision.");
        }
    }

    // Accessible to users ---------------------------------------------------------------------------------------------

    /**
     * Applies a force on the object
     * @param xcomponent force along the x axis
     * @param ycomponent force along the y axis
     */
    public void applyForce(float xcomponent, float ycomponent)
    {
        Vec2 force = new Vec2(xcomponent, ycomponent);
        force.mult(worldSettings.getForceScaleFactor()); // scale by force scale factor
        totalForce.add(force);
    }

    /**
     * Applies a force on the object
     * @param magnitude strength of the force
     * @param angleInRadians Direction in which to apply the force
     */
    public void applyForceInDirection(float magnitude, float angleInRadians)
    {
        Vec2 force = new Vec2(Formulas.getXComponent(magnitude, angleInRadians),
                Formulas.getYComponent(magnitude, angleInRadians));
        force.mult(worldSettings.getForceScaleFactor());
        totalForce.add(force);
    }

    /**
     * Checks if the object is currently touching another object
     * @param object The object to check against
     * @return Returns true if the two objects are touching
     */
    public boolean isTouching(PhysicsObject object)
    {
        // Pass to appropriate sub class's isTouching method
        switch (object.shapeType)
        {
            case POLYGON:
                return isTouching((PhysicsPolygon) object);
            case CIRCLE:
                return isTouching((PhysicsCircle) object);
            default:
                System.err.println("INVALID OBJECT TYPE in isTouching.");
                return false;
        }
    }

    /**
     * TODO Checks if the object is resting on the "Ground"
     * @return Returns true if the object is resting on a surface that counteracts gravity
     */
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
    abstract float findMaxRadius();
    private ShapeType getShapeType() { return shapeType; }
    public float getMass() { return mass; }
    public float getVelocity()
    {
        float x2 = xvelocity*xvelocity;
        float y2 = yvelocity*yvelocity;
        return (float)Math.sqrt(x2 + y2);
    }

    abstract void checkCollision(PhysicsCircle circle);
    abstract void checkCollision(PhysicsPolygon polygon);
    public abstract boolean isTouching(PhysicsCircle circle);
    public abstract boolean isTouching(PhysicsPolygon polygon);

    protected enum ShapeType{
        POLYGON,
        CIRCLE,
        INVALID
    }

    // DEBUG TODO REMOVE
    // TODO DELETE THIS
    public void setOrientation(float o){ this.orientation = o; }
    public void  setDebug(){ this.debug = true; }
}
