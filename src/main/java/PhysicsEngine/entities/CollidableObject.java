package PhysicsEngine.entities;

import PhysicsEngine.Material;
import PhysicsEngine.Vec2;

public abstract class CollidableObject {

    private final static float MASS_SCALING_FACTOR = 100.0f;
    protected final static float TINY_AMOUNT = 0.01f;

    Vec2 position;
    Vec2 totalForce;

    float xvelocity;
    float yvelocity;
    float volume;

    Material material;
    float invertedMass = 0.05f;

    CollidableObject(Vec2 p, Material material, float volume)
    {
        position = p;
        this.xvelocity = 0;
        this.yvelocity = 0;
        this.material = material;
        this.volume = volume;
        this.totalForce = new Vec2(0, 0);
        if(material.getDensity() == 0)
        {
            invertedMass = 0;
        }
        else {
            invertedMass = MASS_SCALING_FACTOR / (material.getDensity() * volume);
        }
    }

    public void move(float timeStep)
    {
        position.x = position.x + xvelocity * timeStep;
        position.y = position.y + yvelocity * timeStep;
    }

    public void applyForce(Vec2 force)
    {
        totalForce.add(force);
    }

    public void applyTotalForce()
    {
        xvelocity += invertedMass * totalForce.x;
        yvelocity += invertedMass * totalForce.y;
        totalForce.zero();
    }

    public void setXvelocity(float xvel) { this.xvelocity = xvel; }
    public void setYvelocity(float yvel) { this.yvelocity = yvel; }
    public void setMaterial(Material material)
    {
        this.material = material;
        this.invertedMass = MASS_SCALING_FACTOR / (material.getDensity() * volume);
    }

    public float getRestitution(){ return material.getResitution(); }
    public float getInvertedMass(){ return invertedMass; }
    public float getX(){ return position.getX(); }
    public float getY(){ return position.getY(); }
    public float getXvelocity() { return xvelocity; }
    public float getYvelocity() { return yvelocity; }
    public float getVelocity()
    {
        float x2 = xvelocity*xvelocity;
        float y2 = yvelocity*yvelocity;
        return (float)Math.sqrt(x2 + y2);
    }

    public abstract void checkCollision(CollidableCircle circle);
    public abstract void checkCollision(CollidableBox box);

    public abstract float getWidth();
    public abstract float getHeight();
}
