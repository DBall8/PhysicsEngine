package PhysicsEngine;

import PhysicsEngine.math.*;
class PhysicsPolygon extends PhysicsObject{

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
        float bestDist = -Float.MAX_VALUE;
        Vec2 bestNormal = null;
        Vec2 bestFace = null;
        Point bestPoint1 = null;
        Point bestPoint2 = null;
        Vec2 face;

        // Move polygon A to be in the circle's coordinate space, rotated
        float relativeX = position.x - circle.getX();
        float relativeY = position.y - circle.getY();
        polygon.translateAndRotate(relativeX, relativeY, orientation ,true);

        // Now we will use the points from polygon A in polygon B's coordinate space
        Point[] polyPoints = polygon.getCalculatedPoints();

        // Loop through each face and try to see if a seperating line can be drawn
        for(int i=0; i<polyPoints.length; i++)
        {
            // Create a face from the current point and next (and loop back around for last point)
            Point point1 = polyPoints[i];
            Point point2 = (i == polyPoints.length-1? polyPoints[0]: polyPoints[i+1]);

            face = new Vec2(point2.getX() - point1.getX(),
                    point2.getY() - point1.getY());
            face.normalize();

            // Get the normal vector of the face
            Vec2 normal = new Vec2(face.getY(), -face.getX());

            // Check that the normal faces away from the center of the polygon, if not, flip the normal
            Vec2 pointVecFromCenter = new Vec2(polyPoints[i].getX() - relativeX, polyPoints[i].getY() - relativeY);
            if(Formulas.dotProduct(normal, pointVecFromCenter) < 0)
            {
                normal.mult(-1.0f);
            }

            // Get the point from polygon A that is closest to polygon B along the direction of the face's normal
            // (just use a point from the current face)
            Point aSupport = polyPoints[i];

            // These two points are in B's coordinate space, so each point is also the vector from B's center

            // Project each point vector along the face normal. This essentially translates to the "distance" in the
            // direction of the face normal. Therefore, if A's point has a smaller distance from B's center than B, we
            // know that there is an overlap (quantified by the difference, positive difference is a positive overlap)
            float sepDistance = Formulas.dotProduct(normal, new Vec2(-aSupport.getX(), -aSupport.getY()));

            // Keep the best overlap and the normal along which it occurred
            if(sepDistance > bestDist)
            {
                bestDist = sepDistance;
                bestNormal = normal;
                bestFace = face;
                bestPoint1 = point1.copy();
                bestPoint2 = point2.copy();
            }
        }

        // If the center of the circle has crossed through the face, we know collision has occurred against the face
        if(bestDist < 0)
        {
            Collision collision = new Collision(this, circle, bestNormal, -bestDist);
            collision.applyImpulse();
        }

        // Project the circle's center and each of the two points along the direction of the face. Then subtract each
        // point's projection from the circle's to see if the circle is before or past each point in the face's direction
        // Project(circlePos - pointPos) is the same as Project(-pointPos) because the circle is the origin
        float proj1 = Formulas.dotProduct(bestFace, Formulas.vecMult(bestPoint1.getVec(), -1.0f));
        float proj2 = Formulas.dotProduct(bestFace, Formulas.vecMult(bestPoint2.getVec(), -1.0f));

        float radiusSquared = circle.getRadius() * circle.getRadius();

        // Projection of point 1 is negative, therefore point 1 is the closest to the circle
        if(proj1 < 0)
        {
            // Check for collision with point 1
            float distSquared = (bestPoint1.getX() * bestPoint1.getX()) + (bestPoint1.getY() * bestPoint1.getY());
            if(distSquared < radiusSquared){
                // COLLISION
                float penetration = (float)(circle.getRadius() - Math.sqrt(distSquared));
                Vec2 normal = bestPoint1.getVec().normalize();
                Collision collision = new Collision(circle, this, normal, penetration);
                collision.applyImpulse();
            }
        }
        // Projection of point 1 is positive but point 2 is negative, circle is between both points and is closest to the face
        else if(proj1 > 0 && proj2 < 0)
        {
            // Check for collision with face
            float dist = Formulas.dotProduct(bestNormal.mult(-1.0f), bestPoint1.getVec());
            if(dist < circle.getRadius())
            {
                Collision collision = new Collision(circle, this, bestNormal, circle.getRadius() - dist);
                collision.applyImpulse();
            }
        }
        else if(proj2 > 0)
        {
            // Check for collision with point 2
            float distSquared = (bestPoint2.getX() * bestPoint2.getX()) + (bestPoint2.getY() * bestPoint2.getY());
            if(distSquared < radiusSquared){
                // COLLISION
                float penetration = (float)(circle.getRadius() - Math.sqrt(distSquared));
                Vec2 normal = bestPoint2.getVec().normalize();
                Collision collision = new Collision(circle, this, normal, penetration);
                collision.applyImpulse();

            }
        }
    }

    void checkCollision(PhysicsBox box)
    {
        try {
            // Create a polygon from the box TODO make this a box member or remove
            Polygon p = new Polygon(new Point[]
            {
                    new Point(-box.width / 2.0f, box.height / 2.0f),
                    new Point(box.width / 2.0f, box.height / 2.0f),
                    new Point(box.width / 2.0f, -box.height / 2.0f),
                    new Point(-box.width / 2.0f, -box.height / 2.0f),
            });
            PhysicsPolygon boxPoly = new PhysicsPolygon(worldSettings, box.position, p);

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
            Polygon p = new Polygon(new Point[]{
                    new Point(-box.width / 2.0f, -box.height / 2.0f),
                    new Point(box.width / 2.0f, -box.height / 2.0f),
                    new Point(box.width / 2.0f, box.height / 2.0f),
                    new Point(-box.width / 2.0f, box.height / 2.0f),
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
        Vec2 bestNormal = null;
        Vec2 face;

        // Move polygon B to origin = its center, and rotate it to its current rotation
        b.polygon.translateAndRotate(0, 0, b.orientation, true);
        // Move polygon A to be in B's coordinate space, rotated
        float relativeX = position.x - b.getX();
        float relativeY = position.y - b.getY();
        polygon.translateAndRotate(relativeX, relativeY, orientation ,true);

        // Now we will use the points from polygon A in polygon B's coordinate space
        Point[] polyPoints = polygon.getCalculatedPoints();

        // Loop through each face and try to see if a seperating line can be drawn
        for(int i=0; i<polyPoints.length; i++)
        {
            // Create a face from the current point and next (and loop back around for last point)
            Point point1 = polyPoints[i];
            Point point2 = i == polyPoints.length-1? polyPoints[0]: polyPoints[i+1];

            face = new Vec2(point2.getX() - point1.getX(),
                    point2.getY() - point1.getY());
            face.normalize();

            // Get the normal vector of the face
            Vec2 normal = new Vec2(face.getY(), -face.getX());

            // Check that the normal faces away from the center of the polygon, if not, flip the normal
            Vec2 pointVecFromCenter = new Vec2(polyPoints[i].getX() - relativeX, polyPoints[i].getY() - relativeY);
            if(Formulas.dotProduct(normal, pointVecFromCenter) < 0)
            {
                normal.mult(-1.0f);
            }

            // Get the point from polygon B that is closest to polygon A along the direction of the face's normal
            // (Get the support point in the opposite direction of the face normal)
            Point bSupport = b.getPolygon().getSupportPoint(Formulas.vecMult(normal, -1.0f));
            // Get the point from polygon A that is closest to polygon B along the direction of the face's normal
            // (just use a point from the current face)
            Point aSupport = polyPoints[i];

            // These two points are in B's coordinate space, so each point is also the vector from B's center

            // Project each point vector along the face normal. This essentially translates to the "distance" in the
            // direction of the face normal. Therefore, if A's point has a smaller distance from B's center than B, we
            // know that there is an overlap (quantified by the difference, positive difference is a positive overlap)
            float sepDistance = Formulas.dotProduct(normal, new Vec2(bSupport.getX() - aSupport.getX(), bSupport.getY() - aSupport.getY()));

            // Keep the best overlap and the normal along which it occurred
            if(sepDistance > bestDist)
            {
                bestDist = sepDistance;
                bestNormal = normal;
            }
        }
//        System.out.println(bestDist);
        // For now, store this in a collision object
        Collision collision = new Collision(this, b, bestNormal, -bestDist);

        return collision;
    }

    public Polygon getPolygon()
    {
        return polygon;
    }
}
