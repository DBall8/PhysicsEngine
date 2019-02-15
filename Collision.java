package physicsEngine;

import physicsEngine.math.Formulas;
import physicsEngine.math.Point;
import physicsEngine.math.Vec2;
import javafx.scene.paint.Color;

/**
 * Class for resolving the impulse between two physics objects
 */
class Collision {

    private final static float POSITION_CORRECTION_PERCENT = 0.2f; // the percent to use when correcting the position
                                                                   // of overlapped objects
    private final static float MIN_POSITION_CORRECTION = 0.1f; // Minimum position correction to use
    private final static short MAX_CONTACT_POINTS = 2;

    private final static boolean ENABLE_ROTATION = true;
    private final static boolean ENABLE_FRICTION = true;

    // Objects involved in collision
    PhysicsObject o1;
    PhysicsObject o2;
    // Vector of the force of the collision
    Vec2 normal;
    // The amount of overlap between the two objects
    float penetration;
    Point[] contactPoints;
    short numContactPoints;

    Collision(PhysicsObject o1, PhysicsObject o2, Vec2 normal, float penetration)
    {
        this.o1 = o1;
        this.o2 = o2;
        this.normal = normal;
        this.penetration = penetration;
        this.contactPoints = new Point[2];
        this.numContactPoints = 0;
    }

    void addContactPoint(Point p)
    {
        if(numContactPoints > MAX_CONTACT_POINTS)
        {
            System.err.println("TOO MANY CONTACT POINTS.");
            return;
        }
        contactPoints[numContactPoints] = p;
        numContactPoints++;

        if(o1.worldSettings.canDebug())
        {
            o1.worldSettings.getDebugger().drawPoint(p, Color.RED);
        }
    }

    /**
     * Calculates the impulse generated from the collision and applies it to both objects involved
     * @return the impulse vector, which should be applied inverted to object 1 and normally to object 2
     */
    void applyImpulse() {
        // If both objects have infinite mass, neither will be affected since they are immovable
        if (o1.getInvertedMass() + o2.getInvertedMass() == 0) return; // Two infinite mass objects cannot move

        if (numContactPoints == 0) {
            // TODO fix this ever hitting
            return;
        }

        // Move the objects apart if overlapping
        correctPosition();

        // Use the average restitution
        float e = o2.getRestitution() + o1.getRestitution() / 2.0f;
        // get the coefficient of friction to use for this collision
        float muStatic = (o1.getStaticFriction() + o2.getStaticFriction()) / 2.0f;
        float muDynamic = (o1.getDynamicFriction() + o2.getDynamicFriction()) / 2.0f;

        float crossA;
        float crossB;
        Vec2 contactA;
        Vec2 contactB;
        for (int i = 0; i < numContactPoints; i++) {

            contactA = new Vec2(contactPoints[i].getX() - o1.getX(), contactPoints[i].getY() - o1.getY());
            contactB = new Vec2(contactPoints[i].getX() - o2.getX(), contactPoints[i].getY() - o2.getY());

            // Get the vector formed by the two circles' velocities
            Vec2 pointAAngularVelocity = Formulas.cross(o1.getAngularVelocity(), contactA);
            Vec2 pointBAngularVelocity = Formulas.cross(o2.getAngularVelocity(), contactB);
            Vec2 relativeVelocity = new Vec2(o2.getXVelocity() + pointBAngularVelocity.x - o1.getXVelocity() - pointAAngularVelocity.x,
                    o2.getYVelocity() + pointBAngularVelocity.y - o1.getYVelocity() - pointAAngularVelocity.y);
            // Get the relative velocity along the normal vector
            float normalVelocity = Formulas.dotProduct(normal, relativeVelocity);

            // If the relative velocity along the normal is positive, the objects are already moving apart, do nothing
            if (normalVelocity >= 0) return;

            crossA = Formulas.cross(contactA, normal);
            crossB = Formulas.cross(contactB, normal);

            crossA *= crossA;
            crossB *= crossB;

            // Get the impulse scalar from the normal velocity, the restitution, and the inverted masses
            // j times normal is normal force
            float j = -(1.0f + e) * normalVelocity;
            float inverseMassSum = o1.getInvertedMass() + o2.getInvertedMass();
            if (ENABLE_ROTATION) {
                inverseMassSum += (crossA * o1.getInvertedInertia()) + (crossB * o2.getInvertedInertia());
            }
            j /= inverseMassSum;
            //j /= numContactPoints;

            // Distribute the impulse across the normal of the collision
            Vec2 resolutionVec = Formulas.vecMult(normal, j);

            Vec2 oppositeImpulse = resolutionVec.copy().mult(-1.0f);
            if(!ENABLE_FRICTION) {
                if (ENABLE_ROTATION)
                {
                    o1.applyImpulse(oppositeImpulse, contactA);
                    o2.applyImpulse(resolutionVec, contactB);
                } else {
                    o1.applyImpulse(oppositeImpulse, new Vec2(0, 0));
                    o2.applyImpulse(resolutionVec, new Vec2(0, 0));
                }

                return;
            }

            Vec2 tangent = Formulas.vecMult(normal, -1.0f * Formulas.dotProduct(relativeVelocity, normal));
            tangent = Formulas.vecAdd(relativeVelocity, tangent);
            tangent.normalize();

            // Recalculate j but using the tangent of the collision normal
            float jF = -1.0f * Formulas.dotProduct(relativeVelocity, tangent);
            jF /= inverseMassSum;
            //jF /= numContactPoints;

            // If the the absolute value of jF is less than the static coefficient times j
            Vec2 frictionVec;
            if (Math.abs(jF) < muStatic * j) {
                frictionVec = tangent.mult(jF);
            }
            // Otherwise use j and dynamic friction
            else {
                frictionVec = tangent.mult(-1.0f * j * muDynamic);
            }

            oppositeImpulse.add(Formulas.vecMult(frictionVec, -1.0f));
            resolutionVec.add(frictionVec);

            if (ENABLE_ROTATION) {
                o1.applyImpulse(oppositeImpulse, contactA);
                o2.applyImpulse(resolutionVec, contactB);
            } else {
                o1.applyImpulse(oppositeImpulse, new Vec2(0, 0));
                o2.applyImpulse(resolutionVec, new Vec2(0, 0));
            }
        }

    }

    /**
     * Move two overlapping objects apart a bit to correct the overlapping
     */
    private void correctPosition()
    {
        // If the overlap is small enough, ignore it
        float minPositionCorrection = MIN_POSITION_CORRECTION * o1.worldSettings.getTimeScaleFactor();
        if(penetration <= minPositionCorrection) return;

        float correctionAmount;
        correctionAmount = (penetration / (o1.getInvertedMass() + o2.getInvertedMass())) * POSITION_CORRECTION_PERCENT;

        Vec2 correctionVector = normal.copy();
        correctionVector.mult(correctionAmount);

        o1.position.x -= o1.getInvertedMass() * correctionVector.x;
        o1.position.y -= o1.getInvertedMass() * correctionVector.y;
        o2.position.x += o2.getInvertedMass() * correctionVector.x;
        o2.position.y += o2.getInvertedMass() * correctionVector.y;
    }

    /**
     * Flips the perspective of the collision so that o1 is now o2 and o2 is now o1 but the normal still points
     * from o1 to o2
     */
    void flipPerspective()
    {
        PhysicsObject temp = o1;
        o1 = o2;
        o2 = temp;
        normal.mult(-1.0f);
    }
}
