package PhysicsEngine;

public class Material {
    private float restitution;
    private float density;
    private float staticFriction;
    private float dynamicFriction;

    public Material(float restitution, float density) {
        this.restitution = restitution;
        this.density = density;
        this.staticFriction = 0.3f;
        this.dynamicFriction = 0.1f;
    }
    public Material(float restitution, float density, float staticFriction, float dynamicFriction) {
        this.restitution = restitution;
        this.density = density;
        this.staticFriction = staticFriction;
        this.dynamicFriction = dynamicFriction;
    }

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

    public final static Material Rock = new Material(0.1f, 0.6f, 0.1f, 0.5f);
    public final static Material Wood = new Material(0.2f, 0.3f, 0.5f, 0.3f);
    public final static Material Metal = new Material(0.05f, 1.2f, 0.25f, 0.1f);
    public final static Material Bouncy = new Material(0.8f, 0.3f, 0.2f, 0.9f);
    public final static Material Static = new Material(0.8f, 0);

}
