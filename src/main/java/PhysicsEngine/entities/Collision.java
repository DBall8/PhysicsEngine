package PhysicsEngine.entities;

import PhysicsEngine.Formulas;
import PhysicsEngine.Vec2;

public class Collision {
    CollidableObject o1;
    CollidableObject o2;
    Vec2 normal;

    Collision(){}

    Collision(CollidableObject o1, CollidableObject o2, Vec2 normal)
    {
        this.o1 = o1;
        this.o2 = o2;
        this.normal = normal;
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
        float j = -(1 + e) * normalVelocity;
        j /= o1.getInvertedMass() + o2.getInvertedMass();

        // Distribute the impulse across the normal of the collision
        normal.mult(j);

        // Update circles' velocities relative to their masses
        o1.xvelocity -= o1.getInvertedMass() * normal.x;
        o1.yvelocity -= o1.getInvertedMass() * normal.y;
        o2.xvelocity += o2.getInvertedMass() * normal.x;
        o2.yvelocity += o2.getInvertedMass() * normal.y;

//        System.out.println("J: " + j);
//        System.out.println("NormalVel: " + normalVelocity);
//        System.out.format("VelX: %f, VelY: %f\n", normal.x, normal.y);
    }

    public void setO1(CollidableObject o1){ this.o1 = o1; }
    public void setO2(CollidableObject o2){ this.o2 = o2; }
    public void setNormal(Vec2 normal){ this.normal = normal; }
}
