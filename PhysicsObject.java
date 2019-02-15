package physicsEngine;

import physicsEngine.math.Formulas;
import physicsEngine.math.Vec2;

/**
 * Class for calculation collisions between objects
 */
public abstract class PhysicsObject{

    public final static float MASS_SCALING_FACTOR = 100.0f; // Scale for making masses closer to velocities for less
                                                            // floating point errors
    private final static float GRAVITY_SCALAR = 0.01f; // Scale for making gravity seem normal at about 10 units
    protected final static float TINY_AMOUNT = 0.01f; // A value to consider "close enough"
    protected final static float TOUCHING_AMOUNT = 0.1f;
    private final static float MIN_FORCE = 0.01f;

    private static long idCounter = 0;

    private String id;

    Vec2 position; // Position of the object
    Vec2 totalForce; // Sum of all forces currently acting on the object

    Vec2 totalImpulse; // Stores the total impulse during one collision calculation. Used with numImpulse to distrbute
                       // multiple impulses evenly
    int numImpulse;    // Number of impulses on this object in a collision calculation

    float orientation; // current angle (in radians)
    float angularVelocity;
    float torque;

    float xvelocity;
    float yvelocity;
    float volume;

    Material material;
    float mass;
    float invertedMass = 0.05f;
    float inertia;
    float invertedIntertia;
    float broadPhaseRadius = 0; // Radius at the furthest point from the shape's center

    ShapeType shapeType = ShapeType.INVALID; // Enum for tracking which collision methods to use (circle vs polygon)
    WorldSettings worldSettings; // Contains all the world environment settings for the world the object exists in

    boolean debug = false;

    protected PhysicsObject(WorldSettings worldSettings, Vec2 p, Material material, float volume)
    {
        this.id = generateID();
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
        this.totalImpulse = new Vec2(0,0);
        this.numImpulse = 0;

        setMass(volume * material.getDensity());
        setInertia(mass * volume);
    }

    private synchronized String generateID()
    {
        return "Obj-" + String.valueOf(idCounter++);
    }

    /**
     * Moves and rotates the objects
     * @param timeStep the amount of time to move the object forward through
     */
    void move(float timeStep)
    {
        // Update position;
        position.x = position.x + xvelocity * timeStep;
        position.y = position.y + yvelocity * timeStep;
        orientation = orientation + angularVelocity * timeStep;
        orientation = Formulas.normalizeAngle(orientation);
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
        // Update velocities, but only apply forces big enough to matter
        if(totalForce.x > MIN_FORCE || totalForce.x < -MIN_FORCE)
        {
            xvelocity += invertedMass * totalForce.x;
        }

        if(totalForce.y > MIN_FORCE || totalForce.y < -MIN_FORCE) {
            yvelocity += invertedMass * totalForce.y;
        }

        if(torque > MIN_FORCE || torque < -MIN_FORCE) {
            angularVelocity += invertedIntertia * torque;
        }

        // Zero out total force vector since they have been applied
        totalForce.zero();
        torque = 0;
    }

    /**
     * Stores an impulse to be added to total forces at the end of a collision calculation loop
     * NOTE this method does not scale the forces by the force scale factor since they happen every frame
     * @param impulse the impulse vector to accumulate
     */
    void applyImpulse(Vec2 impulse, Vec2 contactVec)
    {
        applyForce(impulse);
        applyTorque(Formulas.cross(contactVec, impulse));
    }

    /**
     * Turns a collection of impulses into a resulting force
     */
//    void applyTotalImpulse()
//    {
//        if(numImpulse <= 0 ) return; // if not impulses felt, do nothing
//        // Apply the impulse divided by the number of impulses felt
//        applyForce(totalImpulse.x / (float)numImpulse, totalImpulse.y / (float)numImpulse);
//
//        // Zero out the total impulse and impulse count
//        totalImpulse.zero();
//        numImpulse = 0;
//    }

    /**
     * Checks for a collision between two objects and applies an impulse if one has occurred
     * @param object The other object to check a collision against
     */
    Collision checkCollision(PhysicsObject object, float margin)
    {
        // Dont calculate collision between two immovable objects
        if(invertedMass == 0 && object.invertedMass == 0) return null;

        // Pass to the sub-class's collision check
        switch (object.shapeType)
        {
            case POLYGON:
                return checkCollision((PhysicsPolygon)object, margin);
            case CIRCLE:
                return checkCollision((PhysicsCircle) object, margin);
            default:
                System.err.println("INVALID OBJECT TYPE in check collision.");
                return null;
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
        totalForce.add(force);
    }

    /**
     * Applies a force on the object
     * @param force Vector describing the force
     */
    public void applyForce(Vec2 force)
    {
        totalForce.add(force);
    }

    /**
     * Applies a torque to the object. Positive torque increases clockwise spin
     * @param torque
     */
    public void applyTorque(float torque)
    {
        this.torque += torque;
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

    // -----------------------------------------------------------------------------------------------------------------

    // Getters and setters ---------------------------------------------------------------------------------------------

    public void setXvelocity(float xvel) { this.xvelocity = xvel; }
    public void setYvelocity(float yvel) { this.yvelocity = yvel; }
    public void setMaterial(Material material)
    {
        this.material = material;
        this.invertedMass = MASS_SCALING_FACTOR / (material.getDensity() * volume);
    }
    public String getId(){ return id; }
    public float getX(){ return position.getX(); }
    public float getY(){ return position.getY(); }
    public float getXVelocity() { return xvelocity; }
    public float getYVelocity() { return yvelocity; }
    public float getAngularVelocity() { return angularVelocity; }
    public float getOrientation() { return orientation; }
    public float getMass() { return mass; }
    public float getVelocity()
    {
        float x2 = xvelocity*xvelocity;
        float y2 = yvelocity*yvelocity;
        return (float)Math.sqrt(x2 + y2);
    }

    float getRestitution(){ return material.getRestitution(); }
    float getInvertedMass(){ return invertedMass; }
    float getInvertedInertia(){ return  invertedIntertia; }
    float getStaticFriction() { return material.getStaticFriction(); }
    float getDynamicFriction() { return material.getDynamicFriction(); }
    private ShapeType getShapeType() { return shapeType; }

    protected void setMass(float mass)
    {
        this.mass = mass;
        if(material.getDensity() == 0)
        {
            invertedMass = 0;
        }
        else {
            invertedMass = MASS_SCALING_FACTOR / mass;
        }
    }

    protected void setInertia(float inertia)
    {
        this.inertia = inertia;
        if(material.getDensity() == 0)
        {
            invertedIntertia = 0;
        }
        else {
            invertedIntertia = MASS_SCALING_FACTOR / inertia;
        }
    }

    abstract Collision checkCollision(PhysicsCircle circle, float margin);
    abstract Collision checkCollision(PhysicsPolygon polygon, float margin);
    public abstract boolean isTouching(PhysicsCircle circle);
    public abstract boolean isTouching(PhysicsPolygon polygon);
    abstract float findMaxRadius();

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
