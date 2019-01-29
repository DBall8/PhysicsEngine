package PhysicsEngine;

import PhysicsEngine.math.Formulas;
import PhysicsEngine.math.Vec2;

/**
 * Class for resolving the impulse between two physics objects
 */
class Collision {

    private final static float POSITION_CORRECTION_PERCENT = 0.2f; // the percent to use when correcting the position
                                                                   // of overlapped objects
    private final static float MIN_POSITION_CORRECTION = 0.05f; // Minimum position correction to use

    // Objects involved in collision
    PhysicsObject o1;
    PhysicsObject o2;
    // Vector of the force of the collision
    Vec2 normal;
    // The amount of overlap between the two objects
    float penetration;

    Collision(PhysicsObject o1, PhysicsObject o2, Vec2 normal, float penetration)
    {
        this.o1 = o1;
        this.o2 = o2;
        this.normal = normal;
        this.penetration = penetration;
    }

    /**
     * Calculates the impulse generated from the collision and applies it to both objects involved
     * @return the impulse vector, which should be applied inverted to object 1 and normally to object 2
     */
    Vec2 getImpulse()
    {
        Vec2 impulseVector = new Vec2(0,0);
        // If both objects have infinite mass, neither will be affected since they are immovable
        if(o1.getInvertedMass() + o2.getInvertedMass() == 0) return impulseVector; // Two infinite mass objects cannot move

        // Move the objects apart if overlapping
        correctPosition();

        // Get the vector formed by the two circles' velocities
        Vec2 relativeVelocity = new Vec2(o2.getXvelocity() - o1.getXvelocity(),
                                         o2.getYvelocity() - o1.getYvelocity());

        // Get the relative velocity along the normal vector
        float normalVelocity = Formulas.dotProduct(normal, relativeVelocity);

        // If the relative velocity along the normal is positive, the objects are already moving apart, do nothing
        if(normalVelocity >= 0) return impulseVector;

        // Use the average restitution
        float e = o2.getRestitution() + o1.getRestitution() / 2.0f;

        // Get the impulse scalar from the normal velocity, the restitution, and the inverted masses
        // j times normal is normal force
        float j = -(1.0f + e) * normalVelocity;
        j /= o1.getInvertedMass() + o2.getInvertedMass();

        // Distribute the impulse across the normal of the collision
        Vec2 resolutionVec = normal.copy();
        resolutionVec.mult(j);

        // Apply the force to each object, in opposite directions
        impulseVector.add(resolutionVec);
//        o1.applyForce(-resolutionVec.x, -resolutionVec.y);
//        o2.applyForce(resolutionVec.x, resolutionVec.y);
//        o1.xvelocity -= o1.getInvertedMass() * resolutionVec.x;
//        o1.yvelocity -= o1.getInvertedMass() * resolutionVec.y;
//        o2.xvelocity += o2.getInvertedMass() * resolutionVec.x;
//        o2.yvelocity += o2.getInvertedMass() * resolutionVec.y;

        // Apply friction generated from the collision
        impulseVector.add(getFriction(relativeVelocity, j));

        return impulseVector;
    }

    /**
     * Find the friction from a collision
     * @param relativeVelocity the relative velocity of the two colliding objects
     * @param j the collision strength factor found from calculating the impulse
     * @return The friction vector
     */
    private Vec2 getFriction(Vec2 relativeVelocity, float j)
    {
//        float normalVelocity = Formulas.dotProduct(normal, relativeVelocity);
//        j = -1.0f * normalVelocity;
//        j /= o1.getInvertedMass() + o2.getInvertedMass();

        // Tangent to normal, for the friction force
        Vec2 tangent = Formulas.vecMult(normal, -1.0f * Formulas.dotProduct(relativeVelocity, normal));
        tangent = Formulas.vecAdd(relativeVelocity, tangent);
        tangent.normalize();

        // Recalculate j but using the tangent of the collision normal
        float jF = -1.0f * Formulas.dotProduct(relativeVelocity, tangent);
        jF /= (o1.getInvertedMass() + o2.getInvertedMass());

        // get the coefficient of friction to use for this collision
        float mu =  (o1.getStaticFriction() + o2.getStaticFriction()) / 2.0f;

        // If the the absolute value of jF is less than the static coefficient times j
        Vec2 frictionVec;
        if(Math.abs(jF) < mu * j)
        {
            frictionVec = tangent.mult(jF);
        }
        // Otherwise use j and dynamic friction
        else
        {
            mu = (o1.getDynamicFriction() + o2.getDynamicFriction()) / 2.0f;
            frictionVec = tangent.mult(-1.0f * j * mu);
        }

        return frictionVec;

        // Apply the friction forces in opposite forces
//        o1.applyForce(-frictionVec.x, -frictionVec.y);
//        o2.applyForce(frictionVec.x, frictionVec.y);
//        o1.xvelocity -= o1.getInvertedMass() * frictionVec.x;
//        o1.yvelocity -= o1.getInvertedMass() * frictionVec.y;
//        o2.xvelocity += o2.getInvertedMass() * frictionVec.x;
//        o2.yvelocity += o2.getInvertedMass() * frictionVec.y;
    }

    /**
     * Move two overlapping objects apart a bit to correct the overlapping
     */
    private void correctPosition()
    {
        // If the overlap is small enough, ignore it
        if(penetration <= MIN_POSITION_CORRECTION) return;

        float correctionAmount;
        correctionAmount = (penetration / (o1.getInvertedMass() + o2.getInvertedMass())) * POSITION_CORRECTION_PERCENT;

        Vec2 correctionVector = normal.copy();
        correctionVector.mult(correctionAmount);

        o1.position.x -= o1.getInvertedMass() * correctionVector.x;
        o1.position.y -= o1.getInvertedMass() * correctionVector.y;
        o2.position.x += o2.getInvertedMass() * correctionVector.x;
        o2.position.y += o2.getInvertedMass() * correctionVector.y;
    }

//    public void setO1(PhysicsObject o1){ this.o1 = o1; }
//    public void setO2(PhysicsObject o2){ this.o2 = o2; }
//    public void setNormal(Vec2 normal){ this.normal = normal; }
//    public void setPenetration(float penetration){ this.penetration = penetration; }
}
