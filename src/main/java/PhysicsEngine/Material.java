package PhysicsEngine;

public class Material {
    private float resitution;
    private float density;

    public Material(float resitution, float density) {
        this.resitution = resitution;
        this.density = density;
    }

    public float getResitution() {
        return resitution;
    }

    public float getDensity() {
        return density;
    }

    public final static Material Rock = new Material(0.1f, 0.6f);
    public final static Material Wood = new Material(0.2f, 0.3f);
    public final static Material Metal = new Material(0.05f, 1.2f);
    public final static Material Bouncy = new Material(0.8f, 0.3f);
    public final static Material Static = new Material(0.4f, 0);

}
