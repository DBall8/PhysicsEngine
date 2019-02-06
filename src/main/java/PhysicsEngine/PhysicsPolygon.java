package PhysicsEngine;

import PhysicsEngine.math.*;
import javafx.scene.paint.Color;

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
        SeperatingAxisResult result1 = new SeperatingAxisResult();
        Collision c1 = findAxisOfLeastSeperation(polygon, result1);
        if(c1.penetration + margin < 0) return null; // Seperating axis found, no collision

        SeperatingAxisResult result2 = new SeperatingAxisResult();
        Collision c2 = polygon.findAxisOfLeastSeperation(this, result2);
        if(c2.penetration + margin < 0) return null; // Seperating axis found, no collision

//        if(worldSettings.canDebug()) {
//            worldSettings.getDebugger().drawFace(result1.referenceFace, Color.PURPLE);
//            worldSettings.getDebugger().drawNormal(result1.referenceFace, c1.normal, Color.LIGHTPINK);
//            worldSettings.getDebugger().drawFace(result2.referenceFace, Color.BLUE);
//            worldSettings.getDebugger().drawNormal(result2.referenceFace, c2.normal, Color.LIGHTBLUE);
//        }

        // TODO need to find incident face from the the reference face, not simply from the other collision
        // IDEA: pass an object that stores the index of the point that crossed the reference face

        // Take the collision with the least penetration, if it was from the box's perspective, flip perspective
        if(Formulas.BiasedGreaterThan(c1.penetration, c2.penetration, 0.05f))
        {
            // Face 1 = reference face??
            // Face 2 = incident face??
            Face incidentFace = findIncidentFace(this, result2);
            findContactPoints(c2, result2.referenceFace, incidentFace);

            // Use face normal
//
            return c2;
            //return new Collision(this, polygon, c2.normal.mult(-1.0f), c2.penetration);
        }
        else
        {
            // Face 2 = reference face??
            // Face 1 = incident face??
            Face incidentFace = findIncidentFace(polygon, result1);
            findContactPoints(c1, result1.referenceFace, incidentFace);
            return c1;
        }
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
        Face bestFace = null; // the face closest to the circle's center
        Vec2 bestFaceVec = null;
        Face face;
        Vec2 faceVec; // stores the face currently being checked

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
            face = new Face(point1, point2);

            faceVec = face.getVec().normalize();

            // Get the normal vector of the face
            Vec2 normal = faceVec.tangent();

            // Check that the normal faces away from the center of the polygon, if not, flip the normal
            Vec2 pointVecFromCenter = new Vec2(polyPoints[i].getX() - relativeX, polyPoints[i].getY() - relativeY);
            if(Formulas.dotProduct(normal, pointVecFromCenter) < 0)
            {
                normal.mult(-1.0f);
            }

            // Get the point from polygon A that is closest to polygon B along the direction of the face's normal
            // (just use a point from the current face)
            Point aSupport = face.getP1();

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
                bestFaceVec = faceVec;
            }
        }

        // If the center of the circle has crossed through the face, we know collision has occurred against the face
        if(bestDist < 0)
        {
            Collision c = new Collision(this, circle, bestNormal, -bestDist);
            c.contactPoint = new Point(-bestNormal.x * circle.getRadius() + circle.position.x, -bestNormal.y * circle.getRadius() + circle.position.y);
            return c;
        }

        // Project the circle's center and each of the two points along the direction of the face. Then subtract each
        // point's projection from the circle's to see if the circle is before or past each point in the face's direction
        // Project(circlePos - pointPos) is the same as Project(-pointPos) because the circle is the origin
        float proj1 = Formulas.dotProduct(bestFaceVec, Formulas.vecMult(bestFace.getP1().getVec(), -1.0f));
        float proj2 = Formulas.dotProduct(bestFaceVec, Formulas.vecMult(bestFace.getP2().getVec(), -1.0f));

        float radiusSquared = circle.getRadius() * circle.getRadius() + margin;

        // Projection of point 1 is negative, therefore point 1 is the closest to the circle
        if(proj1 < 0)
        {
            // Check for collision with point 1
            float distSquared = (bestFace.getP1().getX() * bestFace.getP1().getX()) + (bestFace.getP1().getY() * bestFace.getP1().getY());
            if(distSquared < radiusSquared){
                // COLLISION
                float penetration = (float)(circle.getRadius() - Math.sqrt(distSquared));
                Vec2 normal = bestFace.getP1().getVec().normalize();
                Collision c = new Collision(circle, this, normal, penetration);
                c.contactPoint = new Point(bestFace.getP1().getX() + circle.position.x, bestFace.getP1().getY() + circle.position.y);
                return c;
            }
        }
        // Projection of point 1 is positive but point 2 is negative, circle is between both points and is closest to the face
        else if(proj1 > 0 && proj2 < 0)
        {
            // Check for collision with face
            float dist = Formulas.dotProduct(bestNormal.mult(-1.0f), bestFace.getP1().getVec());
            if(dist < circle.getRadius() + margin)
            {
                Collision c = new Collision(circle, this, bestNormal, circle.getRadius() - dist);
                c.contactPoint = new Point(bestNormal.x * circle.getRadius() + circle.position.x, bestNormal.y * circle.getRadius() + circle.position.y);
                return c;
            }
        }
        else if(proj2 > 0)
        {
            // Check for collision with point 2
            float distSquared = (bestFace.getP2().getX() * bestFace.getP2().getX()) + (bestFace.getP2().getY() * bestFace.getP2().getY());
            if(distSquared < radiusSquared){
                // COLLISION
                float penetration = (float)(circle.getRadius() - Math.sqrt(distSquared));
                Vec2 normal = bestFace.getP2().getVec().normalize();
                Collision c = new Collision(circle, this, normal, penetration);
                c.contactPoint = new Point(bestFace.getP2().getX() + circle.position.x, bestFace.getP2().getY() + circle.position.y);
                return c;
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
        return findAxisOfLeastSeperation(polygon).penetration + TOUCHING_AMOUNT > 0 &&
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
        Face bestFace = null;
        Vec2 bestFaceVec = null;
        Face face;
        Vec2 faceVec;

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
            face = new Face(point1, point2);

            faceVec = face.getVec().normalize();

            // Get the normal vector of the face
            Vec2 normal = faceVec.tangent();

            // Check that the normal faces away from the center of the polygon, if not, flip the normal
            Vec2 pointVecFromCenter = new Vec2(polyPoints[i].getX() - relativeX, polyPoints[i].getY() - relativeY);
            if(Formulas.dotProduct(normal, pointVecFromCenter) < 0)
            {
                normal.mult(-1.0f);
            }

            // Get the point from polygon A that is closest to polygon B along the direction of the face's normal
            // (just use a point from the current face)
            Point aSupport = face.getP1();

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
                bestFaceVec = faceVec;
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
        float proj1 = Formulas.dotProduct(bestFaceVec, Formulas.vecMult(bestFace.getP1().getVec(), -1.0f));
        float proj2 = Formulas.dotProduct(bestFaceVec, Formulas.vecMult(bestFace.getP2().getVec(), -1.0f));

        float radiusSquared = circle.getRadius() * circle.getRadius() + TOUCHING_AMOUNT;

        // Projection of point 1 is negative, therefore point 1 is the closest to the circle
        if(proj1 < 0)
        {
            // Check for collision with point 1
            float distSquared = (bestFace.getP1().getX() * bestFace.getP1().getX()) + (bestFace.getP1().getY() * bestFace.getP1().getY());
            if(distSquared < radiusSquared){
                // COLLISION
                return true;
            }
        }
        // Projection of point 1 is positive but point 2 is negative, circle is between both points and is closest to the face
        else if(proj1 > 0 && proj2 < 0)
        {
            // Check for collision with face
            float dist = Formulas.dotProduct(bestNormal.mult(-1.0f), bestFace.getP1().getVec());
            if(dist < circle.getRadius() + TOUCHING_AMOUNT)
            {
                return true;
            }
        }
        else if(proj2 > 0)
        {
            // Check for collision with point 2
            float distSquared = (bestFace.getP2().getX() * bestFace.getP2().getX()) + (bestFace.getP2().getY() * bestFace.getP2().getY());
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
        return findAxisOfLeastSeperation(b, null);
    }

    /**
     * Finds the axis between the two polygons
     * @param b
     * @param result class which stores information about the seperating axis
     * @return
     */
    public Collision findAxisOfLeastSeperation(PhysicsPolygon b, SeperatingAxisResult result)
    {
        float bestDist = -Float.MAX_VALUE;
        Vec2 bestNormal = null;
        Face bestFace = null;
        int incidentPointIndex = -1;
        Face face;
        Vec2 faceVec;

        // Move polygon B to origin = its center, and rotate it to its current rotation
        b.polygon.translateAndRotate(0, 0, b.orientation, true);
        // Move polygon A to be in B's coordinate space, rotated
        float relativeX = position.x - b.getX();
        float relativeY = position.y - b.getY();
        polygon.translateAndRotate(relativeX, relativeY, orientation ,true);

        // Now we will use the points from polygon A in polygon B's coordinate space
        Point[] polyPoints = polygon.getCalculatedPoints();
        Point[] bPoints = b.getPolygon().getCalculatedPoints();

        // Loop through each face and try to see if a seperating line can be drawn
        for(int i=0; i<polyPoints.length; i++)
        {
            // Create a face from the current point and next (and loop back around for last point)
            Point point1 = polyPoints[i];
            Point point2 = i == polyPoints.length-1? polyPoints[0]: polyPoints[i+1];
            face = new Face(point1, point2);

            faceVec = face.getVec().normalize();

            // Get the normal vector of the face
            Vec2 normal = faceVec.tangent();

            // Check that the normal faces away from the center of the polygon, if not, flip the normal
            Vec2 pointVecFromCenter = new Vec2(polyPoints[i].getX() - relativeX, polyPoints[i].getY() - relativeY);
            if(Formulas.dotProduct(normal, pointVecFromCenter) < 0)
            {
                normal.mult(-1.0f);
            }

            // Get the point from polygon B that is closest to polygon A along the direction of the face's normal
            // (Get the support point in the opposite direction of the face normal)
            int bSupportIndex = b.getPolygon().getSupportPointIndex(Formulas.vecMult(normal, -1.0f));
            Point bSupport = bPoints[bSupportIndex];
            //Point bSupport = b.getPolygon().getSupportPoint(Formulas.vecMult(normal, -1.0f));

            // Get the point from polygon A that is closest to polygon B along the direction of the face's normal
            // (just use a point from the current face)
            Point aSupport = face.getP1();

            // These two points are in B's coordinate space, so each point is also the vector from B's center

            // Project each point vector along the face normal. This essentially translates to the "distance" in the
            // direction of the face normal. Therefore, if A's point has a smaller distance from B's center than B, we
            // know that there is an overlap (quantified by the difference, negative difference is a positive overlap)
            float sepDistance = Formulas.dotProduct(normal, new Vec2(bSupport.getX() - aSupport.getX(), bSupport.getY() - aSupport.getY()));

            // sepDistance will likely be negative for all but one side. This positive value for one side indicates that
            // a separating axis can be drawn for this one side. Therefore, if no positive value is found for any side,
            // no separating axis can be drawn and the objects have collided. We then want the greatest sepDistance,
            // which is the negative of the penetration.
            if(sepDistance > bestDist)
            {
                bestDist = sepDistance;
                bestNormal = normal;
                bestFace = face;
                incidentPointIndex = bSupportIndex;
            }

//            System.out.println(bestDist);
        }

        // If a face object was passed in, store the best face points in it
        if(result != null && bestFace != null)
        {
            Point p1 = new Point(bestFace.getP1().getX() + b.getX(), bestFace.getP1().getY() + b.getY());
            Point p2 = new Point(bestFace.getP2().getX() + b.getX(), bestFace.getP2().getY() + b.getY());
            Face referenceFace = new Face(p1, p2);

            result.referenceFace = referenceFace;
            result.normal = bestNormal;
            result.incidentPointIndex = incidentPointIndex;
        }
//        System.out.println(bestDist);
        // For now, store this in a collision object
        Collision collision = new Collision(this, b, bestNormal, -bestDist);

        return collision;
    }

    private Face findIncidentFace(PhysicsPolygon b, SeperatingAxisResult axisResult)
    {
        b.getPolygon().setTranslation(b.getX(), b.getY());
        Point[] bPoints = b.getPolygon().getCalculatedPoints();
        Point incidentPoint = bPoints[axisResult.incidentPointIndex];
        int beforeIndex = axisResult.incidentPointIndex == 0? bPoints.length-1 : axisResult.incidentPointIndex-1;
        int afterIndex = axisResult.incidentPointIndex == bPoints.length-1? 0 : axisResult.incidentPointIndex+1;
        Face face1 = new Face(bPoints[beforeIndex], incidentPoint);
        Face face2 = new Face(incidentPoint, bPoints[afterIndex]);

        float test1 = Math.abs(Formulas.dotProduct(axisResult.normal, face1.getVec()));
        float test2 = Math.abs(Formulas.dotProduct(axisResult.normal, face2.getVec()));

        if(test1 > test2)
        {
            return face2;
        }
        else
        {
            return face1;
        }
    }

    private void findContactPoints(Collision collision, Face referenceFace, Face incidentFace)
    {
        final boolean CLIP_DEBUG = true;
        if(CLIP_DEBUG && worldSettings.canDebug()) {
            worldSettings.getDebugger().drawFace(referenceFace, Color.PURPLE);
            worldSettings.getDebugger().drawFace(incidentFace, Color.AQUA);
            worldSettings.getDebugger().drawNormal(referenceFace, collision.normal, Color.GREEN);
        }

        Vec2 faceVec = referenceFace.getVec();

        float transX = collision.o2.getX();
        float transY = collision.o2.getY();
        referenceFace.translate(-transX, -transY);
        incidentFace.translate(-transX, -transY);

        // If the incident's face is not parallel to the x axis with an upward normal, rotate both faces around the
        // origin until the incidicent face is
        float faceAngle = 0;
        if(collision.normal.getY() != -1) {
            faceAngle = (float) Math.acos(-collision.normal.getY());
            if(collision.normal.getX() > 0) faceAngle *= -1.0f;
            referenceFace.rotateAboutOrigin(faceAngle, true);
            incidentFace.rotateAboutOrigin(faceAngle, true);
        }

        float minx, maxx;
        if(referenceFace.getP1().getX() < referenceFace.getP2().getX())
        {
            minx = referenceFace.getP1().getX();
            maxx = referenceFace.getP2().getX();
        }
        else
        {
            minx = referenceFace.getP2().getX();
            maxx = referenceFace.getP1().getX();
        }

        float contactPoint1X = Formulas.clamp(minx, maxx, incidentFace.getP1().getX());
        Point contactPoint1 = new Point(contactPoint1X, incidentFace.getYAt(contactPoint1X));

        float contactPoint2X = Formulas.clamp(minx, maxx, incidentFace.getP2().getX());
        Point contactPoint2 = new Point(contactPoint2X, incidentFace.getYAt(contactPoint2X));

        // TODO Fix bug where rotating the contact points does not seem to work properly
        // Rotate the points back to how they were
        contactPoint1 = contactPoint1.getRotatedPoint(-faceAngle);
        contactPoint2 = contactPoint2.getRotatedPoint(-faceAngle);

        // Translate the points back to how they were
        Vec2 contactVec1 = contactPoint1.getVec().add(transX, transY);
        Vec2 contactVec2 = contactPoint2.getVec().add(transX, transY);

        if(worldSettings.canDebug()) {
            float faceProj = Formulas.dotProduct(collision.normal, faceVec);

            // TODO use vector from reference shape's center, not from origin, do it before returning rotated points
            //if(Formulas.dotProduct(collision.normal, contactVec1) - faceProj >= 0)
            {
                worldSettings.getDebugger().drawPoint(contactVec1, Color.RED);
            }
            //if(Formulas.dotProduct(collision.normal, contactVec2) - faceProj >= 0)
            {
                worldSettings.getDebugger().drawPoint(contactVec2, Color.RED);
            }
        }
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

    private class SeperatingAxisResult{
        Face referenceFace;
        Vec2 normal;
        int incidentPointIndex;

        public SeperatingAxisResult(){}
    }
}
