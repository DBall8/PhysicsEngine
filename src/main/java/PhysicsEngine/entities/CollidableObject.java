package PhysicsEngine.entities;

import PhysicsEngine.Material;
import PhysicsEngine.PhysicsWorld;
import PhysicsEngine.Vec2;

public abstract class CollidableObject {

    public final static float MASS_SCALING_FACTOR = 100.0f;
    private final static float GRAVITY_SCALAR = 0.01f;
    protected final static float TINY_AMOUNT = 0.01f;

    Vec2 position;
    Vec2 totalForce;

    float xvelocity;
    float yvelocity;
    float volume;

    Material material;
    float mass;
    float invertedMass = 0.05f;

    boolean isGrounded = false;

    PhysicsWorld world;

    CollidableObject(PhysicsWorld world, Vec2 p, Material material, float volume)
    {
        this.world = world;
        position = p;
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

    public void move(float timeStep)
    {
        position.x = position.x + xvelocity * timeStep;
        position.y = position.y + yvelocity * timeStep;
    }

    public void applyForce(Vec2 force)
    {
        totalForce.add(force);
    }

    public void applyGravity(float gravity)
    {
        totalForce.y += gravity * GRAVITY_SCALAR * mass / (MASS_SCALING_FACTOR );
    }

    public void applyTotalForce()
    {
        if(world.isGravity() && Math.abs(totalForce.y) < TINY_AMOUNT)
        {
            isGrounded = true;
        }
        xvelocity += invertedMass * totalForce.x;
        yvelocity += invertedMass * totalForce.y;
        totalForce.zero();
    }

    public boolean isTouching(CollidableObject object)
    {
        if(object instanceof CollidableCircle) {
            return isTouching((CollidableCircle) object);
        }
        else
        {
            return isTouching((CollidableBox) object);
        }
    }

    public boolean isGrounded()
    {
        return Math.abs(yvelocity) < TINY_AMOUNT;
    }

    public void setXvelocity(float xvel) { this.xvelocity = xvel; }
    public void setYvelocity(float yvel) { this.yvelocity = yvel; }
    public void setMaterial(Material material)
    {
        this.material = material;
        this.invertedMass = MASS_SCALING_FACTOR / (material.getDensity() * volume);
    }

    public float getRestitution(){ return material.getRestitution(); }
    public float getInvertedMass(){ return invertedMass; }
    public float getX(){ return position.getX(); }
    public float getY(){ return position.getY(); }
    public float getXvelocity() { return xvelocity; }
    public float getYvelocity() { return yvelocity; }
    public float getStaticFriction() { return material.getStaticFriction(); }
    public float getDynamicFriction() { return material.getDynamicFriction(); }
    public float getMass() { return mass; }
    public float getVelocity()
    {
        float x2 = xvelocity*xvelocity;
        float y2 = yvelocity*yvelocity;
        return (float)Math.sqrt(x2 + y2);
    }

    public abstract void checkCollision(CollidableCircle circle);
    public abstract void checkCollision(CollidableBox box);
    abstract boolean isTouching(CollidableCircle circle);
    abstract boolean isTouching(CollidableBox box);

    public abstract float getWidth();
    public abstract float getHeight();
}
