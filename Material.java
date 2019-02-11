package physicsEngine;

/**
 * Class for storing material specific information
 */
public class Material {
    private float restitution; // Bounciness factor
    private float density; // Used for making heavier or lighter objects
    private float staticFriction; // Resistance to starting moving from stopped
    private float dynamicFriction; // Resistance to movement while moving

    /**
     * Creates a new material with default friction values
     * @param restitution Bounciness factor, 0 to 1.0, the higher the bouncier
     * @param density Higher the density the heavier to object, recommended 0.1 to 2 (0 makes it immovable)
     */
    public Material(float restitution, float density) {
        this.restitution = restitution;
        this.density = density;
        this.staticFriction = 0.3f;
        this.dynamicFriction = 0.1f;
    }

    /**
     * Creates a new material
     * @param restitution Bounciness factor, 0 to 1.0, the higher the bouncier
     * @param density Higher the density the heavier to object, recommended 0.1 to 2 (0 makes it immovable)
     * @param staticFriction Coefficient of static friction (0 to 1)
     * @param dynamicFriction Coefficient of dynamic friction (0 to 1)
     */
    public Material(float restitution, float density, float staticFriction, float dynamicFriction) {
        this.restitution = restitution;
        this.density = density;
        this.staticFriction = staticFriction;
        this.dynamicFriction = dynamicFriction;
    }

    // GETTERS ---------------------------------------------------------------------------------------------------------
    public float getRestitution() {
        return restitution;
    }

    public float getDensity() {
        return density;
    }

    public float getStaticFriction() {
        return staticFriction;
    }
    public float getDynamicFriction() {
        return dynamicFriction;
    }

    // Default materials for easy use
    public final static Material Rock = new Material(0.1f, 0.6f, 0.1f, 0.5f);
    public final static Material Wood = new Material(0.2f, 0.3f, 0.5f, 0.3f);
    public final static Material Metal = new Material(0.05f, 1.2f, 0.25f, 0.1f);
    public final static Material Bouncy = new Material(0.8f, 0.3f, 0.2f, 0.9f);
    public final static Material Static = new Material(0.2f, 0);

}
