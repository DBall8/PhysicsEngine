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
            // Create a polygon from the box TODO make this a box member or remove
            Polygon p = new Polygon(new Vec2[]
            {
                    new Vec2(-box.width / 2.0f, box.height / 2.0f),
                    new Vec2(box.width / 2.0f, box.height / 2.0f),
                    new Vec2(box.width / 2.0f, -box.height / 2.0f),
                    new Vec2(-box.width / 2.0f, -box.height / 2.0f),
            });
            PhysicsPolygon boxPoly = new PhysicsPolygon(worldSettings, box.position, p);

            if(Float.isNaN(box.position.x) || Float.isNaN(position.x)){
                System.out.println("HEY");
            }

            // Check for collisions from each polygon's perspective
            Collision c1 = findAxisOfLeastSeperation(boxPoly);
            Collision c2 = boxPoly.findAxisOfLeastSeperation(this);
            // Collision occurred if both cannot find an axis of seperation
            if(c1.penetration >= 0 && c2.penetration >= 0)
            {
                // Take the collision with the least penetration, if it was from the box's perspective, flip perspective
                Collision collision;
                if(c1.penetration < c2.penetration)
                {
                    collision = c1;
                }
                else
                {
                    collision = new Collision(this, box, c2.normal.mult(-1.0f), c2.penetration);
                }
                // Apply impulse
                collision.applyImpulse();
            }

        }
        catch (MalformedPolygonException e)
        {
            e.printMessage();
        }
    }

    void checkCollision(PhysicsPolygon polygon)
    {
        // Check for collisions from each polygon's perspective
        Collision c1 = findAxisOfLeastSeperation(polygon);
        // Collision occurred if both cannot find an axis of seperation
        Collision c2 = polygon.findAxisOfLeastSeperation(this);
        if(c1.penetration >= 0 && c2.penetration >= 0)
        {
            // Take the collision with the least penetration, if it was from the box's perspective, flip perspective
            Collision collision;
            if(c1.penetration < c2.penetration)
            {
                collision = c1;
            }
            else
            {
                collision = new Collision(this, polygon, c2.normal.mult(-1.0f), c2.penetration);
            }
            // Apply impulse
            collision.applyImpulse();
        }
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
                    new Vec2(-box.width / 2.0f, -box.height / 2.0f),
                    new Vec2(box.width / 2.0f, -box.height / 2.0f),
                    new Vec2(box.width / 2.0f, box.height / 2.0f),
                    new Vec2(-box.width / 2.0f, box.height / 2.0f),
            });

            PhysicsPolygon polygon = new PhysicsPolygon(worldSettings, box.position, p);

            boolean collided = findAxisOfLeastSeperation(polygon).penetration > 0 &&
                    polygon.findAxisOfLeastSeperation(this).penetration > 0;

            //if(collided) System.out.println("TOUCHING");
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

        b.polygon.translateAndRotate(0, 0, b.orientation, true);
        float relativeX = position.x - b.getX();
        float relativeY = position.y - b.getY();

        // Move polygon A to be in B's coordinate space (not rotating, might need to)
        polygon.translateAndRotate(relativeX, relativeY, orientation ,true);
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
            // Make sure the normal is facing outwards
            Vec2 pointVecFromCenter = new Vec2(polyPoints[i].x - relativeX, polyPoints[i].y - relativeY);
            if(Formulas.dotProduct(normal, pointVecFromCenter) < 0)
            {
                normal = new Vec2(-face.getY(), face.getX());
            }

            Vec2 bSupport = b.getPolygon().getSupportPoint(Formulas.vecMult(normal, -1.0f));
            Vec2 aSupport = polyPoints[i];

            float sepDistance = Formulas.dotProduct(normal, new Vec2(bSupport.getX() - aSupport.getX(), bSupport.getY() - aSupport.getY()));

            if(sepDistance > bestDist)
            {
                bestDist = sepDistance;
                bestFace = normal;
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

    // TODO DELETE THIS
    public void setOrientation(float o){ this.orientation = o; }
}
