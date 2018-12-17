package PhysicsEngine.entities;

import PhysicsEngine.Formulas;
import PhysicsEngine.Vec2;

public class CollidableCircle extends CollidableObject {

    float radius;

    public CollidableCircle(Vec2 p, float r)
    {
        super(p, (float)(Math.PI * r * r));
        this.radius = r;
    }

    public void checkCollision(CollidableCircle circle)
    {
        float radiusSum = radius + circle.radius; // distance between the two circles when touching
        float dx = position.x - circle.position.x; // x distance
        float dy = position.y - circle.position.y; // ydistance

        // Are they closer than the distance when touching?
        boolean collided =  (radiusSum * radiusSum) > (dx*dx + dy*dy);

        // If no collision happened, do nothing
        if(!collided) return;

        // Get the unit vector between the two circles
        Vec2 normal = new Vec2(circle.position.x - position.x, circle.position.y - position.y);
        normal.normalize();

        Collision collision = new Collision(this, circle, normal);
        collision.applyImpulse();
    }

    public void checkCollision(CollidableBox box)
    {

        // Vector between circle and box centers
        Vec2 normal = new Vec2(box.position.x - position.x,
                               box.position.y - position.y);

        // Box's centers
        float xExtent = box.width / 2.0f;
        float yExtent = box.height / 2.0f;

        Vec2 closestPoint = new Vec2(Formulas.clamp(-xExtent, xExtent, normal.x),
                                     Formulas.clamp(-yExtent, yExtent, normal.y));

        boolean inside = false;

        if(normal.equals(closestPoint))
        {
            inside = true;

            if(Math.abs(normal.x) > Math.abs(normal.y))
            {
                if(closestPoint.x > 0)
                    closestPoint.x = xExtent;
                else
                    closestPoint.x = -xExtent;
            }
            else
            {
                if(closestPoint.y > 0)
                    closestPoint.y = yExtent;
                else
                    closestPoint.y = -yExtent;
            }
        }

        normal = new Vec2(normal.x - closestPoint.x, normal.y - closestPoint.y);

        float distance = normal.magnitude();
        distance *= distance;

        if((distance > radius * radius) && !inside) return;

        normal.normalize();
        if(inside)
        {
            normal.mult(-1);
        }
        Collision collision = new Collision(this, box, normal);
        collision.applyImpulse();
    }

    public float getWidth(){ return radius*2; }
    public float getHeight(){ return radius*2; }


}
