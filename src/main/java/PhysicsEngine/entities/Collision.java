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
        float j = -(1.0f + e) * normalVelocity;
        j /= o1.getInvertedMass() + o2.getInvertedMass();

        // Distribute the impulse across the normal of the collision
        Vec2 resolutionVec = normal.copy();
        resolutionVec.mult(j);

        // Update circles' velocities relative to their masses
        o1.xvelocity -= o1.getInvertedMass() * resolutionVec.x;
        o1.yvelocity -= o1.getInvertedMass() * resolutionVec.y;
        o2.xvelocity += o2.getInvertedMass() * resolutionVec.x;
        o2.yvelocity += o2.getInvertedMass() * resolutionVec.y;
    }

    void correctPosition()
    {
        if(penetration <= MIN_POSITION_CORRECTION) return;

        float correctionAmount;
        if(o1.invertedMass == 0 || o2.invertedMass == 0)
        {
            correctionAmount = penetration;
        }
        else
        {
            correctionAmount = (penetration * (o1.invertedMass + o2.invertedMass)) * POSITION_CORRECTION_PERCENT;
        }

        Vec2 correctionVector = normal.copy();
        correctionVector.mult(correctionAmount);
//        System.out.printf("Correction x: %f. Correction y: %f\n", correctionVector.x, correctionVector.y);
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
