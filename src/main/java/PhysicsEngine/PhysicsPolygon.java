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

    @Override
    void checkCollision(PhysicsCircle circle) {

    }

    @Override
    void checkCollision(PhysicsBox box) {

    }

    @Override
    public boolean isTouching(PhysicsCircle circle) {
        return false;
    }

    @Override
    public boolean isTouching(PhysicsBox box) {
        return false;
    }

    public Polygon getPolygon() {
        return polygon;
    }
}
