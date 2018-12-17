package PhysicsEngine;

import PhysicsEngine.entities.CollidableBox;
import PhysicsEngine.entities.CollidableCircle;
import PhysicsEngine.entities.Vec2;

import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {

    private List<CollidableCircle> circles = new ArrayList<>();
    private List<CollidableBox> boxes = new ArrayList<>();

    public PhysicsWorld(){}

    public CollidableCircle addCircle(float x, float y, float radius)
    {
        CollidableCircle c = new CollidableCircle(new Vec2(x, y), radius);
        circles.add(c);
        return c;
    }

    public CollidableBox addBox(float centerx, float centery, float width, float height)
    {
        CollidableBox b = new CollidableBox(new Vec2(centerx, centery), width, height);
        boxes.add(b);
        return b;
    }

    public void update(){
        float timeLeft = 1.00f;
        float firstCollisionTime;
        do {
            // Check if each particle hits the box boundaries (must be done first as it resets collision)
            firstCollisionTime = checkCollisions(timeLeft);

            move(firstCollisionTime);

            timeLeft -= firstCollisionTime;

        }while(timeLeft > 0.01f);
    }

    private float checkCollisions(float timeLeft){
//        float firstCollisionTime = timeLeft; // looks for first collision
//        float tempTime;
//        // reset collisions for each player
//        for(Entity e: objects){
//            e.reset();
//            if((tempTime = e.checkCollisions(timeLeft, obstacles)) < firstCollisionTime){
//                firstCollisionTime = tempTime;
//            }
//        }
//        return firstCollisionTime;

        // Check each circle against all other objects

        for(int i=0; i<circles.size(); i++)
        {
            CollidableCircle c =  circles.get(i);
            // Have each circle check against each circle further down the list
            for(int j=i+1; j<circles.size(); j++)
            {
                c.checkCollision(circles.get(j));
            }
            // Have each circle check against each box
            for(int j=0; j<boxes.size(); j++)
            {
                c.checkCollision(boxes.get(j));
            }
        }

        // Check each box against all other boxes further down the list
        for(int i=0; i<boxes.size()-1; i++)
        {
            CollidableBox b =  boxes.get(i);
            for(int j=i+1; j<boxes.size(); j++)
            {
                b.checkCollision(boxes.get(j));
            }
        }

        return timeLeft;
    }

    private void move(float timeStep)
    {
        for(CollidableCircle c: circles)
        {
            c.move(timeStep);
        }

        for(CollidableBox b: boxes)
        {
            b.move(timeStep);
        }
    }
}
