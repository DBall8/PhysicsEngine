package PhysicsEngine;

import PhysicsEngine.math.MalformedPolygonException;
import PhysicsEngine.math.Polygon;
import PhysicsEngine.math.Vec2;

public class PhysicsPolygon extends PhysicsObject{

    Polygon polygon;

    PhysicsPolygon(WorldSettings worldSettings, Vec2 p, Polygon polygon)
    {
        super(worldSettings, p, Material.Wood, polygon.estimateVolume());
        this.polygon = polygon;
        polygon.setTranslation(p.getX(), p.getY());
    }

    PhysicsPolygon(WorldSettings worldSettings, Vec2 p, Polygon polygon, Material material)
    {
        super(worldSettings, p, material, polygon.estimateVolume());
        this.polygon = polygon;
        polygon.setTranslation(p.getX(), p.getY());
    }

    void checkCollision(PhysicsCircle circle)
    {

    }

    void checkCollision(PhysicsBox box)
    {
        try {
            Polygon p = new Polygon(new Vec2[]{
                    new Vec2(box.position.getX() - box.width / 2.0f, box.position.getY() - box.width / 2.0f),
                    new Vec2(box.position.getX() + box.width / 2.0f, box.position.getY() - box.width / 2.0f),
                    new Vec2(box.position.getX() + box.width / 2.0f, box.position.getY() + box.width / 2.0f),
                    new Vec2(box.position.getX() - box.width / 2.0f, box.position.getY() + box.width / 2.0f),
            });

        }
        catch (MalformedPolygonException e)
        {
            e.printMessage();
        }
    }

    void checkCollision(PhysicsPolygon polygon)
    {

    }

    public boolean isTouching(PhysicsPolygon polygon)
    {
        return this.polygon.findAxisOfLeastSeperation(polygon.getPolygon()) >= 0 ||
                polygon.getPolygon().findAxisOfLeastSeperation(this.polygon) >= 0;
    }

    public boolean isTouching(PhysicsCircle circle)
    {
        return false;
    }

    public boolean isTouching(PhysicsBox box)
    {
        try {
            Polygon p = new Polygon(new Vec2[]{
                    new Vec2(box.position.getX() - box.width / 2.0f, box.position.getY() - box.width / 2.0f),
                    new Vec2(box.position.getX() + box.width / 2.0f, box.position.getY() - box.width / 2.0f),
                    new Vec2(box.position.getX() + box.width / 2.0f, box.position.getY() + box.width / 2.0f),
                    new Vec2(box.position.getX() - box.width / 2.0f, box.position.getY() + box.width / 2.0f),
            });

            return this.polygon.findAxisOfLeastSeperation(p) >= 0 ||
                    p.findAxisOfLeastSeperation(this.polygon) >= 0;
        }
        catch (MalformedPolygonException e)
        {
            e.printMessage();
            return false;
        }
    }

    public Polygon getPolygon()
    {
        return polygon;
    }
}
