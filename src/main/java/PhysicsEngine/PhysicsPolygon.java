package PhysicsEngine;

import PhysicsEngine.math.*;

/**
 * Class for calculating collisions for a polygon (Must be completely convex to function properly)
 */
class PhysicsPolygon extends PhysicsObject{

    Polygon polygon; // class containing the location of all points of the polygon

    // CONSTRUCTORS ----------------------------------------------------------------------------------------------------
    PhysicsPolygon(WorldSettings worldSettings, Vec2 p, Polygon polygon)
    {
        super(worldSettings, p, Material.Wood, polygon.estimateVolume());
        commonInit(p, polygon);
    }

    PhysicsPolygon(WorldSettings worldSettings, Vec2 p, Polygon polygon, Material material)
    {
        super(worldSettings, p, material, polygon.estimateVolume());
        commonInit(p, polygon);
    }

    /**
     * Portion of contructors common to each
     * @param p position vector
     * @param polygon shape of polygon
     */
    private void commonInit(Vec2 p, Polygon polygon)
    {
        shapeType = ShapeType.POLYGON;
        this.polygon = polygon;
        polygon.setTranslation(p.getX(), p.getY());
        broadPhaseRadius = findMaxRadius();
    }
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Check and resolve a collision between this polygon and another
     * @param polygon
     */
    @Override
    Collision checkCollision(PhysicsPolygon polygon, float margin)
    {
        // Check for collisions from each polygon's perspective
        Collision c1 = findAxisOfLeastSeperation(polygon);
        // Collision occurred if both cannot find an axis of seperation
        Collision c2 = polygon.findAxisOfLeastSeperation(this);
        if(c1.penetration + margin >= 0 && c2.penetration + margin >= 0)
        {
            // Take the collision with the least penetration, if it was from the box's perspective, flip perspective
            Collision collision;
            if(c1.penetration < c2.penetration)
            {
                return c1;
            }
            else
            {
                return new Collision(this, polygon, c2.normal.mult(-1.0f), c2.penetration);
            }
        }

        return null;
    }

    /**
     * Check for and resolve a collision between this polygon and a circle
     * @param circle
     */
    @Override
    Collision checkCollision(PhysicsCircle circle, float margin)
    {
        float bestDist = -Float.MAX_VALUE; // closest distance between the circle center and a polygon face
        Vec2 bestNormal = null; // the normal of the polygon face at the best distance
        Vec2 bestFace = null; // the face closest to the circle's center
        Point bestPoint1 = null; // the first of the points in the best face
        Point bestPoint2 = null; // the second of the points in the best face
        Vec2 face; // stores the face currently being checked

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
            return new Collision(this, circle, bestNormal, -bestDist);
        }

        // Project the circle's center and each of the two points along the direction of the face. Then subtract each
        // point's projection from the circle's to see if the circle is before or past each point in the face's direction
        // Project(circlePos - pointPos) is the same as Project(-pointPos) because the circle is the origin
        float proj1 = Formulas.dotProduct(bestFace, Formulas.vecMult(bestPoint1.getVec(), -1.0f));
        float proj2 = Formulas.dotProduct(bestFace, Formulas.vecMult(bestPoint2.getVec(), -1.0f));

        float radiusSquared = circle.getRadius() * circle.getRadius() + margin;

        // Projection of point 1 is negative, therefore point 1 is the closest to the circle
        if(proj1 < 0)
        {
            // Check for collision with point 1
            float distSquared = (bestPoint1.getX() * bestPoint1.getX()) + (bestPoint1.getY() * bestPoint1.getY());
            if(distSquared < radiusSquared){
                // COLLISION
                float penetration = (float)(circle.getRadius() - Math.sqrt(distSquared));
                Vec2 normal = bestPoint1.getVec().normalize();
                return new Collision(circle, this, normal, penetration);
            }
        }
        // Projection of point 1 is positive but point 2 is negative, circle is between both points and is closest to the face
        else if(proj1 > 0 && proj2 < 0)
        {
            // Check for collision with face
            float dist = Formulas.dotProduct(bestNormal.mult(-1.0f), bestPoint1.getVec());
            if(dist < circle.getRadius() + margin)
            {
                return new Collision(circle, this, bestNormal, circle.getRadius() - dist);
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
                return new Collision(circle, this, normal, penetration);
            }
        }
        return null;
    }

    /**
     * Checks if the polygon is touching another polygon
     * @param polygon
     * @return true if they are touching
     */
    @Override
    public boolean isTouching(PhysicsPolygon polygon)
    {
        // If an axis of seperation cannot be drawn between the two in either direction, they are touching
        return findAxisOfLeastSeperation(polygon).penetration + TOUCHING_AMOUNT> 0 &&
                polygon.findAxisOfLeastSeperation(this).penetration + TOUCHING_AMOUNT > 0;
    }

    /**
     * Checks if this polygon is touching the given circle
     * @param circle
     * @return true if they are touching
     */
    @Override
    public boolean isTouching(PhysicsCircle circle)
    {
        // See checkCollision(PhysicsCircle) for details on these variables
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
            return true;
        }

        // Project the circle's center and each of the two points along the direction of the face. Then subtract each
        // point's projection from the circle's to see if the circle is before or past each point in the face's direction
        // Project(circlePos - pointPos) is the same as Project(-pointPos) because the circle is the origin
        float proj1 = Formulas.dotProduct(bestFace, Formulas.vecMult(bestPoint1.getVec(), -1.0f));
        float proj2 = Formulas.dotProduct(bestFace, Formulas.vecMult(bestPoint2.getVec(), -1.0f));

        float radiusSquared = circle.getRadius() * circle.getRadius() + TOUCHING_AMOUNT;

        // Projection of point 1 is negative, therefore point 1 is the closest to the circle
        if(proj1 < 0)
        {
            // Check for collision with point 1
            float distSquared = (bestPoint1.getX() * bestPoint1.getX()) + (bestPoint1.getY() * bestPoint1.getY());
            if(distSquared < radiusSquared){
                // COLLISION
                return true;
            }
        }
        // Projection of point 1 is positive but point 2 is negative, circle is between both points and is closest to the face
        else if(proj1 > 0 && proj2 < 0)
        {
            // Check for collision with face
            float dist = Formulas.dotProduct(bestNormal.mult(-1.0f), bestPoint1.getVec());
            if(dist < circle.getRadius() + TOUCHING_AMOUNT)
            {
                return true;
            }
        }
        else if(proj2 > 0)
        {
            // Check for collision with point 2
            float distSquared = (bestPoint2.getX() * bestPoint2.getX()) + (bestPoint2.getY() * bestPoint2.getY());
            if(distSquared < radiusSquared){
                // COLLISION
                return true;

            }
        }

        return false;
    }

    /**
     * Finds the axis between the two polygons
     * @param b
     * @return
     */
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
            // know that there is an overlap (quantified by the difference, negative difference is a positive overlap)
            float sepDistance = Formulas.dotProduct(normal, new Vec2(bSupport.getX() - aSupport.getX(), bSupport.getY() - aSupport.getY()));

            // Keep the smallest overlap and the normal along which it occurred
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

    /**
     * Finds the distance of the point furthest from the polygon's center
     * @return the distance of the furthest point
     */
    float findMaxRadius()
    {
        float greatestRadius = 0;
        Point[] points = polygon.getPoints();
        for(Point point: points)
        {
            float r = point.getVec().magnitude();
            if(r > greatestRadius)
            {
                greatestRadius = r;
            }
        }
        return greatestRadius;
    }

    public Polygon getPolygon()
    {
        return polygon;
    }
}
