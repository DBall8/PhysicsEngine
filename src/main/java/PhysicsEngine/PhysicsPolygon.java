package PhysicsEngine;

import Global.DebugGlobal;
import PhysicsEngine.math.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

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
        Face face1 = new Face();
        Collision c1 = findAxisOfLeastSeperation(polygon, face1);
        if(c1.penetration + margin < 0) return null; // Seperating axis found, no collision

        Face face2 = new Face();
        Collision c2 = polygon.findAxisOfLeastSeperation(this, face2);
        if(c2.penetration + margin < 0) return null; // Seperating axis found, no collision

        System.out.println("COLLIDE");
        // Take the collision with the least penetration, if it was from the box's perspective, flip perspective
        if(Formulas.BiasedGreaterThan(c1.penetration, c2.penetration, 0.05f))
        {
            // Face 1 = reference face
            // Face 2 = incident face
            findContactPoints(c2, face1, face2);

            // Use face normal
//
            return c2;
            //return new Collision(this, polygon, c2.normal.mult(-1.0f), c2.penetration);
        }
        else
        {
            // Face 2 = reference face
            // Face 1 = incident face
            findContactPoints(c1, face2, face1);
            return c1;
        }
    }

    private void findContactPoints(Collision collision, Face referenceFace, Face incidentFace)
    {
        final boolean CLIP_DEBUG = true;
        if(CLIP_DEBUG) {
            Line line1 = new Line(referenceFace.getP1().getX(), referenceFace.getP1().getY(), referenceFace.getP2().getX(), referenceFace.getP2().getY());
            line1.setStroke(Color.PURPLE);
            line1.setStrokeWidth(5);

            Line line2 = new Line(incidentFace.getP1().getX(), incidentFace.getP1().getY(), incidentFace.getP2().getX(), incidentFace.getP2().getY());
            line2.setStroke(Color.AQUA);
            line2.setStrokeWidth(5);
            DebugGlobal.getDebugView().getChildren().addAll(line1, line2);
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

        // TODO Fix bug with contact faces and points not always right
        // Rotate the points back to how they were
        contactPoint1 = contactPoint1.getRotatedPoint(-faceAngle);
        contactPoint2 = contactPoint2.getRotatedPoint(-faceAngle);

        // Translate the points back to how they were
        Vec2 contactVec1 = contactPoint1.getVec().add(transX, transY);
        Vec2 contactVec2 = contactPoint2.getVec().add(transX, transY);

        if(DebugGlobal.IsDebug()) {
            float faceProj = Formulas.dotProduct(collision.normal, faceVec);

            // TODO use vector from reference shape's center, not from origin, do it before returning rotated points
            //if(Formulas.dotProduct(collision.normal, contactVec1) - faceProj >= 0)
            {
                Circle contact1 = new Circle(contactVec1.getX(), contactVec1.getY(), 4);
                contact1.setFill(Color.RED);
                DebugGlobal.getDebugView().getChildren().add(contact1);
            }
            //if(Formulas.dotProduct(collision.normal, contactVec2) - faceProj >= 0)
            {
                Circle contact2 = new Circle(contactVec2.getX(), contactVec2.getY(), 4);
                contact2.setFill(Color.RED);
                DebugGlobal.getDebugView().getChildren().add(contact2);
            }
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
     * @param referenceFace Face class in which to store the reference face of least penetration, world coordinates
     * @return
     */
    public Collision findAxisOfLeastSeperation(PhysicsPolygon b, Face referenceFace)
    {
        float bestDist = -Float.MAX_VALUE;
        Vec2 bestNormal = null;
        Face bestFace = null;
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
            boolean flip = false;
            if(Formulas.dotProduct(normal, pointVecFromCenter) < 0)
            {
                flip = true;
                normal.mult(-1.0f);
            }

            // Get the point from polygon B that is closest to polygon A along the direction of the face's normal
            // (Get the support point in the opposite direction of the face normal)
            Point bSupport = b.getPolygon().getSupportPoint(Formulas.vecMult(normal, -1.0f));
            // Get the point from polygon A that is closest to polygon B along the direction of the face's normal
            // (just use a point from the current face)
            Point aSupport = face.getP1();

            // These two points are in B's coordinate space, so each point is also the vector from B's center

            // Project each point vector along the face normal. This essentially translates to the "distance" in the
            // direction of the face normal. Therefore, if A's point has a smaller distance from B's center than B, we
            // know that there is an overlap (quantified by the difference, negative difference is a positive overlap)
            float sepDistance = Formulas.dotProduct(normal, new Vec2(bSupport.getX() - aSupport.getX(), bSupport.getY() - aSupport.getY()));

            // Keep the smallest overlap and the normal along which it occurred
            // An overlap is a positive sepDistance
            if(bestDist > 0)
            {
                if(sepDistance < bestDist && sepDistance > 0)
                {
                    bestDist = sepDistance;
                    bestNormal = normal;
                    bestFace = face;
                }
            }
            else{
                if(sepDistance > bestDist)
                {
                    bestDist = sepDistance;
                    bestNormal = normal;
                    bestFace = face;
                }
            }

//            System.out.println(bestDist);
        }

        // If a face object was passed in, store the best face points in it
        if(referenceFace != null && bestFace != null)
        {
            Point p1 = new Point(bestFace.getP1().getX() + b.getX(), bestFace.getP1().getY() + b.getY());
            Point p2 = new Point(bestFace.getP2().getX() + b.getX(), bestFace.getP2().getY() + b.getY());
            referenceFace.set(p1, p2);
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
