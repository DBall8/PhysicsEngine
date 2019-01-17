package PhysicsEngine;

import PhysicsEngine.math.Formulas;
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
//        Collision c1 = findAxisOfLeastSeperation(polygon);
//        Collision c2 = polygon.findAxisOfLeastSeperation(this);
//        if(c1.penetration >= 0 || c2.penetration >= 0)
//        {
//            if(c1.penetration )
//        }
    }

    public boolean isTouching(PhysicsPolygon polygon)
    {
        return findAxisOfLeastSeperation(polygon).penetration > 0 &&
                polygon.findAxisOfLeastSeperation(this).penetration > 0;
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

            PhysicsPolygon polygon = new PhysicsPolygon(worldSettings, box.position, p);

            boolean collided = findAxisOfLeastSeperation(polygon).penetration > 0 &&
                    polygon.findAxisOfLeastSeperation(this).penetration > 0;

            return collided;
        }
        catch (MalformedPolygonException e)
        {
            e.printMessage();
            return false;
        }
    }

    public Collision findAxisOfLeastSeperation(PhysicsPolygon b)
    {
        float bestDist = -Float.MAX_VALUE;
        Vec2 bestFace = null;

        // Move polygon A to be in B's coordinate space (not rotating, might need to)
        polygon.setTranslation(position.x - b.getX(), position.y - b.getY());
        Vec2[] polyPoints = polygon.getCalculatedPoints();

        for(int i=0; i<polyPoints.length; i++)
        {
            Vec2 face;
            if(i == polyPoints.length-1)
            {
                face = new Vec2(polyPoints[0].getX() - polyPoints[i].getX(),
                        polyPoints[0].getY() - polyPoints[i].getY());
            }
            else
            {
                face = new Vec2(polyPoints[i+1].getX() - polyPoints[i].getX(),
                        polyPoints[i+1].getY() - polyPoints[i].getY());
            }
            face.normalize();
            Vec2 normal = new Vec2(face.getY(), -face.getX());

            Vec2 bSupport = b.getPolygon().getSupportPoint(Formulas.vecMult(normal, -1.0f));
            Vec2 aSupport = polyPoints[i];

            float sepDistance = Formulas.dotProduct(normal, new Vec2(bSupport.getX() - aSupport.getX(), bSupport.getY() - aSupport.getY()));

            if(sepDistance > bestDist)
            {
                bestDist = sepDistance;
                bestFace = face;
            }
        }

//        System.out.println(bestDist);
        Collision collision = new Collision(this, b, bestFace, -bestDist);

        return collision;
    }

    public Polygon getPolygon()
    {
        return polygon;
    }
}
