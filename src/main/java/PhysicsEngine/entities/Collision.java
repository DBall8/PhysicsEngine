package PhysicsEngine.entities;

import PhysicsEngine.Formulas;
import PhysicsEngine.PhysicsWorld;
import PhysicsEngine.Vec2;

public class Collision {

    private final static float POSITION_CORRECTION_PERCENT = 0.8f;
    private final static float MIN_POSITION_CORRECTION = 0.01f;

    CollidableObject o1;
    CollidableObject o2;
    Vec2 normal;
    float penetration;

    Collision(){}

    Collision(CollidableObject o1, CollidableObject o2, Vec2 normal, float penetration)
    {
        this.o1 = o1;
        this.o2 = o2;
        this.normal = normal;
        this.penetration = penetration;
    }

    void applyImpulse()
    {
        correctPosition();

        // Get the vector formed by the two circles' velocities
        Vec2 relativeVelocity = new Vec2(o2.getXvelocity() - o1.getXvelocity(),
                                         o2.getYvelocity() - o1.getYvelocity());

        // Get the relative velocity along the normal vector
        float normalVelocity = Formulas.dotProduct(normal, relativeVelocity);

        // If the relative velocity along the normal is positive, the objects are already moving apart, do nothing
        if(normalVelocity >= 0) return;

        // Use the smaller restitution
        float e = Math.min(o2.getRestitution(), o1.getRestitution());

        // Get the impulse scalar from the normal velocity, the restitution, and the inverted masses
        // j times normal is normal force
        float j = -(1.0f + e) * normalVelocity;
        j /= o1.getInvertedMass() + o2.getInvertedMass();

        // Distribute the impulse across the normal of the collision
        Vec2 resolutionVec = normal.copy();
        resolutionVec.mult(j);

        o1.xvelocity -= o1.getInvertedMass() * resolutionVec.x;
        o1.yvelocity -= o1.getInvertedMass() * resolutionVec.y;
        o2.xvelocity += o2.getInvertedMass() * resolutionVec.x;
        o2.yvelocity += o2.getInvertedMass() * resolutionVec.y;

        applyFriction(relativeVelocity, j);
    }

    void applyFriction(Vec2 relativeVelocity, float j)
    {
//        float normalVelocity = Formulas.dotProduct(normal, relativeVelocity);
//        j = -1.0f * normalVelocity;
//        j /= o1.getInvertedMass() + o2.getInvertedMass();

        // Tangent to normal, for the friction force
        Vec2 tangent = Formulas.vecMult(normal, -1.0f * Formulas.dotProduct(relativeVelocity, normal));
        tangent = Formulas.vecAdd(relativeVelocity, tangent);
        tangent.normalize();

        float jF = -1.0f * Formulas.dotProduct(relativeVelocity, tangent);
        jF /= (o1.getInvertedMass() + o2.getInvertedMass());

        // get the coefficient of friction to use for this collision
        float mu =  (o1.getStaticFriction() + o2.getStaticFriction()) / 2.0f;

        Vec2 frictionVec;
        if(Math.abs(jF) < mu * j)
        {
            frictionVec = tangent.mult(jF);
        }
        else
        {
            mu = (o1.getDynamicFriction() + o2.getDynamicFriction()) / 2.0f;
            frictionVec = tangent.mult(-1.0f * j * mu);
        }

        o1.xvelocity -= o1.getInvertedMass() * frictionVec.x;
        o1.yvelocity -= o1.getInvertedMass() * frictionVec.y;
        o2.xvelocity += o2.getInvertedMass() * frictionVec.x;
        o2.yvelocity += o2.getInvertedMass() * frictionVec.y;
    }

    void correctPosition()
    {
        if(penetration <= MIN_POSITION_CORRECTION) return;

        float correctionAmount;
        correctionAmount = (penetration / (o1.invertedMass + o2.invertedMass)) * POSITION_CORRECTION_PERCENT;

        Vec2 correctionVector = normal.copy();
        correctionVector.mult(correctionAmount);

        o1.position.x -= o1.getInvertedMass() * correctionVector.x;
        o1.position.y -= o1.getInvertedMass() * correctionVector.y;
        o2.position.x += o2.getInvertedMass() * correctionVector.x;
        o2.position.y += o2.getInvertedMass() * correctionVector.y;
    }

    public void setO1(CollidableObject o1){ this.o1 = o1; }
    public void setO2(CollidableObject o2){ this.o2 = o2; }
    public void setNormal(Vec2 normal){ this.normal = normal; }
    public void setPenetration(float penetration){ this.penetration = penetration; }
}
