package PhysicsEngine.entities;

import PhysicsEngine.Material;
import PhysicsEngine.PhysicsWorld;
import PhysicsEngine.Vec2;

public class CollidableBox extends CollidableObject {

    float width;
    float height;

    public CollidableBox(PhysicsWorld world, Vec2 p, float width, float height)
    {
        super(world, p, Material.Wood, width * height);
        this.width = width;
        this.height = height;
    }

    public CollidableBox(PhysicsWorld world, Vec2 p, float width, float height, Material material)
    {
        super(world, p, material, width * height);
        this.width = width;
        this.height = height;
    }

    public void checkCollision(CollidableCircle circle)
    {
        circle.checkCollision(this);
    }

    public void checkCollision(CollidableBox box){

        // Get the normal vector for the boxes' centers
        Vec2 normal = new Vec2(box.position.x - position.x, box.position.y - position.y);

        if(Math.abs(normal.x) < TINY_AMOUNT && Math.abs(normal.y) < TINY_AMOUNT)
        {
            normal = new Vec2(0, 1);
            Collision collision = new Collision(this, box, normal, width/2.0f);
            collision.applyImpulse();
            return;
        }

        // Get the boxes x extents (sorta radii)
        float aExtent = width / 2.0f;
        float bExtent = box.width / 2.0f;

        // Get the box x overlap, which is the sum of the box x radii minus the distance in the x direction
        float xOverlap = aExtent + bExtent - Math.abs(normal.x);

        // If Overlapping in the x direction
        if(xOverlap > 0)
        {
            // Get the boxes' y extents
            aExtent = height / 2.0f;
            bExtent = box.height / 2.0f;

            // Get the y overlap (Sum of their radii minus y distance)
            float yOverlap = aExtent + bExtent - Math.abs(normal.y);

            // If overlapping y, then both overlapping and a collision has occurred!
            if(yOverlap > 0)
            {
                // Set up collision
                Collision collision = new Collision();
                collision.setO1(this);
                collision.setO2(box);

                // If the smaller overlap is on the y axis, use a y normal
                if(xOverlap > yOverlap)
                {
                    if(normal.y < 0)
                    {
                        collision.setNormal(new Vec2(0, -1));
                    }
                    else
                    {
                        collision.setNormal(new Vec2(0, 1));
                    }
                    collision.setPenetration(yOverlap);
                }
                // If the smaller overlap is on the x axis, use a x normal
                else
                {
                    if(normal.x < 0)
                    {
                        collision.setNormal(new Vec2(-1, 0));
                    }
                    else
                    {
                        collision.setNormal(new Vec2(1, 0));
                    }
                    collision.setPenetration(xOverlap);
                }
                // Apply the impulse
                collision.applyImpulse();
            }
        }
    }

    boolean isTouching(CollidableCircle circle){
        return circle.isTouching(this);
    }
    boolean isTouching(CollidableBox box){

        // Get the normal vector for the boxes' centers
        Vec2 normal = new Vec2(box.position.x - position.x, box.position.y - position.y);
        // Get the boxes x extents (sorta radii)
        float aExtent = width / 2.0f;
        float bExtent = box.width / 2.0f;

        // Get the box x overlap, which is the sum of the box x radii minus the distance in the x direction
        float xOverlap = aExtent + bExtent - Math.abs(normal.x);

        // If Overlapping in the x direction
        if(xOverlap > 0) {
            // Get the boxes' y extents
            aExtent = height / 2.0f;
            bExtent = box.height / 2.0f;

            // Get the y overlap (Sum of their radii minus y distance)
            float yOverlap = aExtent + bExtent - Math.abs(normal.y);

            // If overlapping y, then both overlapping and a collision has occurred!
            if (yOverlap > 0) {
                return true;
            }
        }

        return false;
    }

    public float minX(){ return position.x - width/2.0f; }
    public float maxX(){ return position.x + width/2.0f; }
    public float minY(){ return position.y - height/2.0f; }
    public float maxY(){ return position.y + height/2.0f; }
    public float getWidth(){ return width; }
    public float getHeight(){ return height; }
}
