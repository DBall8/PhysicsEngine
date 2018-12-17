package PhysicsEngine.entities;

public class CollidableBox extends CollidableObject {

    float width;
    float height;

    public CollidableBox(Vec2 p, float width, float height)
    {
        super(p, width * height);
        this.width = width;
        this.height = height;
    }

    public void checkCollision(CollidableCircle circle){  }
    public void checkCollision(CollidableBox box){

        Vec2 normal = new Vec2(box.position.x - position.x, box.position.y - position.y);

        float aExtent = (maxX() - minX()) / 2.0f;
        float bExtent = (box.maxX() - box.minX()) / 2.0f;

        float xOverlap = aExtent + bExtent - Math.abs(normal.x);

        if(xOverlap > 0)
        {
            aExtent = (maxY() - minY()) / 2.0f;
            bExtent = (box.maxY() - box.minY()) / 2.0f;

            float yOverlap = aExtent + bExtent - Math.abs(normal.y);

            if(yOverlap > 0)
            {
                Collision collision = new Collision();
                collision.setO1(this);
                collision.setO2(box);

                if(xOverlap > yOverlap)
                {
                    if(normal.x < 0)
                    {
                        collision.setNormal(new Vec2(-1, 0));
                    }
                    else
                    {
                        collision.setNormal(new Vec2(1, 0));
                    }
                }
                else
                {
                    if(normal.y < 0)
                    {
                        collision.setNormal(new Vec2(0, -1));
                    }
                    else
                    {
                        collision.setNormal(new Vec2(0, 1));
                    }
                }
                collision.applyImpulse();
                System.out.println("COLLIDED");
            }
        }
    }

    public float minX(){ return position.x - width/2.0f; }
    public float maxX(){ return position.x + width/2.0f; }
    public float minY(){ return position.y - height/2.0f; }
    public float maxY(){ return position.y + height/2.0f; }
    public float getWidth(){ return width; }
    public float getHeight(){ return height; }
}
