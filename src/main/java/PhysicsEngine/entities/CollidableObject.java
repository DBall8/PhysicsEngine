package PhysicsEngine.entities;

import PhysicsEngine.Vec2;

public abstract class CollidableObject {

    private final static float MASS_SCALING_FACTOR = 1000.0f;
    private final static float DEFAULT_RESTITUTION = 0.5f;
    protected final static float TINY_AMOUNT = 0.01f;

    Vec2 position;

    float xvelocity;
    float yvelocity;

    float restitution = DEFAULT_RESTITUTION;
    float invertedMass = 0.05f;

    CollidableObject(Vec2 p, float mass)
    {
        position = p;
        this.xvelocity = 0;
        this.yvelocity = 0;
        invertedMass = MASS_SCALING_FACTOR / mass;
    }

    public void move(float timeStep)
    {
        position.x = position.x + xvelocity * timeStep;
        position.y = position.y + yvelocity * timeStep;
    }

    public void setXvelocity(float xvel) { this.xvelocity = xvel; }
    public void setYvelocity(float yvel) { this.yvelocity = yvel; }
    public void setMass(float mass)
    {
        if(mass == 0)
        {
            invertedMass = 0;
        }
        else
        {
            invertedMass = 1.0f / mass;
        }
    }

    public float getRestitution(){ return restitution; }
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
