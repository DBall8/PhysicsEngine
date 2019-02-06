package PhysicsEngine.math;


/**
 * Class used for calculating the points of a polygon
 */
public class Polygon {

    private static final float VOLUME_ESTIMATE_FACTOR = 18.0f; // Fudge factor for estimating volume

    // Data about the polygon's traits
    private Point[] originPoints; // each point of the polygon when centered about the origin
    private float[] pointAngles; // the corresponding angle coordinate of each point, used for rotating

    // Data about polygon's current position and rotation, and the points at this state
    Vec2 translation = new Vec2(0,0); // Current translation from the origin
    float rotation = 0; // Current rotation in radians
    Point[] points; // The points of the polygon at the current translation and rotation


    /**
     * Constructor
     * @param points An array of points that when connected in order (and then last to first) form the polygon
     * @throws MalformedPolygonException Throws when points do not create a valid polygon
     */
    public Polygon(Point[] points) throws MalformedPolygonException{
        if(points.length <= 0) throw(new MalformedPolygonException("Points do not form a valid polygon."));
        this.originPoints = points;

        // Take the given polygon and center it at the origin.
        centerAtOrigin();
        // Find the relative angles of each point
        findPointAngles();

        // Initialize the current points array to be the same as the origin points
        this.points = new Point[originPoints.length];
        for(int i=0; i< points.length; i++)
        {
            this.points[i] = this.originPoints[i].copy();
        }
    }

    /**
     * Moves points so that the center of the polygon is 0,0
     */
    private void centerAtOrigin(){

        if(originPoints.length <= 0) System.err.println("POLYGON SET POINTS NOT SET");

        // Find the average x coordinate and y coordinate of the points
        float xsum = 0;
        float ysum = 0;
        for(Point point: originPoints)
        {
            xsum += point.getX();
            ysum += point.getY();
        }

        // The center is the average x and y coordinate
        Point center = new Point(xsum / originPoints.length, ysum / originPoints.length);

        // Translate everything by the negative of the center to be centered around 0,0
        for(int i = 0; i< originPoints.length; i++)
        {
            originPoints[i] = new Point(originPoints[i].getX() - center.getX(), originPoints[i].getY() - center.getY());
        }
    }

    /**
     * Save the angle of each point from the center, to be used in rotation calculation later
     */
    private void findPointAngles()
    {
        if(originPoints.length <= 0) System.err.println("POLYGON SET POINTS NOT SET");

        // Initialize array
        pointAngles = new float[originPoints.length];
        // For each point, find its angle from its coordinates
        for(int i = 0; i< originPoints.length; i++)
        {
            float dx = originPoints[i].getX();
            float dy = -1.0f* originPoints[i].getY();
            pointAngles[i] = (float)Math.atan(dx / dy);
            // Add Pi for the half the rotation to adjust for the range of inverse tan
            if(dy <= 0) pointAngles[i] += Math.PI;
//            System.out.println(pointAngles[i] * (180.0 / Math.PI));
        }
    }

    /**
     * Estimate the volume of the polygon
     * @return the estimated volume
     */
    public float estimateVolume()
    {
        // Assume the polygon is regular, with apothem as the average of its points distances from the center
        // But is a percentage of the apothem relative to n, since the fewer n the smaller the apothem becomes
        // A = n * apothem^2 * tan(180/n)

        // get the average distance from the center of the points, use this as the apothem
        float apothem = 0;
        for(Point point: originPoints)
        {
            apothem += Math.sqrt((point.getX() * point.getX()) + (point.getY() * point.getY()));
        }

        apothem /= originPoints.length;
        apothem *= (originPoints.length * VOLUME_ESTIMATE_FACTOR / 100.0f);

        float estimatedVolume = (float)(apothem * apothem * Math.tan(Math.PI / originPoints.length) * originPoints.length);

//        // When taking the average, divide by 1 more than the number of points to decrease the result more when there
//        // are fewer points. This is because less points indicates a further overestimate,(think of a triangle vs an
//        // even octagon)
//        pointDistSum /= originPoints.length;
//        float estimatedVolume = (float)(Math.PI * pointDistSum);
//        estimatedVolume = estimatedVolume * (originPoints.length * VOLUME_ESTIMATE_FACTOR / 100.0f);
        //System.out.println(estimatedVolume);
        return estimatedVolume;
    }

    /**
     * Set the current translation of the polygon
     * @param x translation along x axis
     * @param y translation along y axis
     */
    public void setTranslation(float x, float y)
    {
        // Move the points minus what the current translation already is
        translatePoints(x - translation.x, y - translation.y);
        // Save new current translation
        translation = new Vec2(x, y);
    }

    /**
     * Sets the current rotation of the polygon
     * @param r the angle to set the rotation to
     * @param radians true if angle was given in radians, false if given in degrees
     */
    public void setRotation(float r, boolean radians)
    {
        // If angle given in degrees, convert to radians
        if(!radians)
        {
            r *= Math.PI / 180.0;
        }

        // Move angle to between 0 and 2Pi
        r = Formulas.normalizeAngle(r);
        // If this is a different rotation than that already set, rotate the polygon to the new angle
        if(this.rotation != r) {
            rotatePointsTo(r);
            this.rotation = r;
        }
    }

    /**
     * Rotates and translates the polygon in one call
     * @param x translation along the x axis
     * @param y translation along the y axis
     * @param r angle to set the polygon to
     * @param radians true if angle given in radians, false if given in degrees
     */
    public void translateAndRotate(float x, float y, float r, boolean radians)
    {
        // If angle given in degrees, convert to radians
        if(!radians)
        {
            r *= Math.PI / 180.0;
        }

        // Move angle to between 0 and 2Pi
        r = Formulas.normalizeAngle(r);

        // If angle has not changed, just translate
        if(r == this.rotation)
        {
            setTranslation(x ,y);
        }
        // If angle has changed, do both
        else
        {
            // Set the translation, and then rotate
            // The rotation will also handle the translation
            translation = new Vec2(x, y);
            setRotation(r, radians);
        }
    }

    /**
     * Translates all points in the current points array BY the given values (not TO them)
     * @param x amount to translate along the x axis
     * @param y amount to translate along the y axis
     */
    private void translatePoints(float x, float y)
    {
        if(x == 0 && y == 0) return; // do nothing if no movement needed

        for(int i=0; i<points.length; i++)
        {
            points[i] = points[i].add(x, y);
        }
    }

    /**
     * Rotates all points TO the given angle (not BY)
     * @param angleRads the angle in radians to set the rotation to
     */
    private void rotatePointsTo(float angleRads)
    {
        // Adjust each point around the center with the center as the origin, and then translate back to the current
        // translation
        for(int i=0; i<points.length; i++)
        {
            // get the distance from the center and the new angle of the point
            float mag = originPoints[i].getVec().magnitude();
            float newAngle = pointAngles[i] + angleRads;

            // Calculate the points new x and y coordinates
            float newx = (float) (mag * Math.sin(newAngle));
            float newy = (float) (-mag * Math.cos(newAngle));
            //if(newAngle >= Math.PI/4 && newAngle < 3.0f*Math.PI/4) pointAngles[i] += Math.PI;
//          System.out.format("Old x: %f, new X: %f\n", originPoints[i].x, newx);
//          System.out.format("Old y: %f, new Y: %f\n\n", originPoints[i].y, newy);
            points[i] = new Point(newx + translation.x, newy + translation.y);
        }
    }

    /**
     * Returns the point that is furthest in a given direction on the polygon
     * @param direction Vector representing the direction to use
     * @return the point with the greatest projecting in the given direction
     */
    public Point getSupportPoint(Vec2 direction) {

        if(points.length <= 0) System.err.println("POLYGON POINTS NOT CALCULATED");

        // Start with "empty" values and store the best
        float bestProjection = -Float.MAX_VALUE;
        Point support = null;

        // Loop through each point and save the one with best projection
        for(Point point: points)
        {
            float projection = Formulas.dotProduct(point.getVec(), direction);
            if(projection > bestProjection)
            {
                support = point;
                bestProjection = projection;
            }
        }

        if(support == null)
            System.err.println("[[[SUPPORT POINT NOT FOUND]]]");
        return support;
    }

    /**
     * Returns the index of point that is furthest in a given direction on the polygon
     * @param direction Vector representing the direction to use
     * @return the index of point with the greatest projecting in the given direction
     */
    public int getSupportPointIndex(Vec2 direction) {

        if(points.length <= 0) System.err.println("POLYGON POINTS NOT CALCULATED");

        // Start with "empty" values and store the best
        float bestProjection = -Float.MAX_VALUE;
        int supportIndex = -1;

        // Loop through each point and save the one with best projection
        for(int i=0; i<points.length; i++)
        {
            Point point = points[i];
            float projection = Formulas.dotProduct(point.getVec(), direction);
            if(projection > bestProjection)
            {
                supportIndex = i;
                bestProjection = projection;
            }
        }

        if(supportIndex < 0)
            System.err.println("[[[SUPPORT POINT NOT FOUND]]]");
        return supportIndex;
    }

    /**
     * Creates a copy of the polygon
     * @return
     */
    public Polygon copy()
    {
        // Copy origin points
        Point[] newPoints = new Point[originPoints.length];
        for(int i = 0; i< originPoints.length; i++)
        {
            newPoints[i] = originPoints[i].copy();
        }

        try {
            return new Polygon(newPoints);
        }
        catch (MalformedPolygonException e)
        {
            System.err.println("Failed to create polygon");
            return null;
        }
    }

    // GETTERS ---------------------------------------------------------------------------------------------------------
    public Point[] getCalculatedPoints(){ return points; }
    public Point[] getPoints(){ return originPoints; }

    // DEBUG -----------------------------------------------------------------------------------------------------------
    private void printPoints()
    {
        for(Point point: points)
        {
            System.out.printf("X: %f, Y:%f\n", point.getX(), point.getY());
        }
    }

    // OUTDATED --------------------------------------------------------------------------------------------------------

    // Outdated code, once used to recalculate point positions from start
    private void recalcPoints()
    {
        if(originPoints.length <= 0) System.err.println("POLYGON SET POINTS NOT SET");
        if(pointAngles.length <= 0) System.err.println("POLYGON POINT ANGLES NOT CALCULATED");


        for(int i = 0; i< originPoints.length; i++)
        {
            if(rotation != 0) {
                float mag = originPoints[i].getVec().magnitude();
                float newAngle = pointAngles[i] + rotation;

                //points[i] = originPoints[i].copy();
                float newx = (float) (mag * Math.sin(newAngle));
                float newy = (float) (-mag * Math.cos(newAngle));
                //if(newAngle >= Math.PI/4 && newAngle < 3.0f*Math.PI/4) pointAngles[i] += Math.PI;
//                System.out.format("Old x: %f, new X: %f\n", originPoints[i].x, newx);
//                System.out.format("Old y: %f, new Y: %f\n\n", originPoints[i].y, newy);
                points[i] = new Point(newx, newy);
            }
            else
            {
                points[i] = originPoints[i];
            }

        }

        for(int i = 0; i< originPoints.length; i++)
        {
            float px = points[i].getX();
            float py = points[i].getY();

            px += translation.x;
            py += translation.y;

            points[i] = new Point(px, py);
        }
    }
}
