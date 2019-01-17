package PhysicsEngine.math;

public class Polygon {

    private static final float VOLUME_ESTIMATE_FACTOR = 18.0f;

    // Data about the polygon's traits
    private Vec2[] setPoints;
    private float[] pointAngles;

    // Data about polygon's current position and rotation, and the points at this state
    Vec2 translation = new Vec2(0,0);
    float rotation = 0; // Rads
    Vec2[] points;


    public Polygon(Vec2[] points) throws MalformedPolygonException{
        if(points.length <= 0) throw(new MalformedPolygonException("Points do not form a valid polygon."));
        this.setPoints = points;
        this.points = new Vec2[setPoints.length];
        centerAtOrigin();
        findPointAngles();
    }

    public Vec2 getSupportPoint(Vec2 direction) {

        if(setPoints.length <= 0) System.err.println("POLYGON POINTS NOT CALCULATED");

        float bestProjection = -Float.MAX_VALUE;
        Vec2 support = null;
        for(Vec2 point: setPoints)
        {
            float projection = Formulas.dotProduct(point, direction);
            if(projection > bestProjection)
            {
                support = point;
                bestProjection = projection;
            }
        }
        return support;
    }



    private void centerAtOrigin(){

        if(setPoints.length <= 0) System.err.println("POLYGON SET POINTS NOT SET");

        float xsum = 0;
        float ysum = 0;
        for(Vec2 point: setPoints)
        {
            xsum += point.x;
            ysum += point.y;
        }

        Vec2 center = new Vec2(xsum / setPoints.length, ysum / setPoints.length);

        // Center set points around 0,0
        for(int i=0; i<setPoints.length; i++)
        {
            setPoints[i] = new Vec2(setPoints[i].x - center.x, setPoints[i].y - center.y);
        }
    }

    private void findPointAngles()
    {
        if(setPoints.length <= 0) System.err.println("POLYGON SET POINTS NOT SET");

        pointAngles = new float[setPoints.length];
        for(int i=0; i<setPoints.length; i++)
        {
            float dx = setPoints[i].x;
            float dy = -1.0f*setPoints[i].y;
            pointAngles[i] = (float)Math.atan(dx / dy);
            if(dy <= 0) pointAngles[i] += Math.PI;
//            System.out.println(pointAngles[i] * (180.0 / Math.PI));
        }
    }

    public float estimateVolume()
    {
        // Assume the polygon is regular, with apothem as the average of its points distances from the center
        // But is a percentage of the apothem relative to n, since the fewer n the smaller the apothem becomes
        // A = n * apothem^2 * tan(180/n)

        // get the average distance from the center of the points, use this as the apothem
        float apothem = 0;
        for(Vec2 point: setPoints)
        {
            apothem += Math.sqrt((point.x * point.x) + (point.y * point.y));
        }

        apothem /= setPoints.length;
        apothem *= (setPoints.length * VOLUME_ESTIMATE_FACTOR / 100.0f);

        float estimatedVolume = (float)(apothem * apothem * Math.tan(Math.PI / setPoints.length) * setPoints.length);

//        // When taking the average, divide by 1 more than the number of points to decrease the result more when there
//        // are fewer points. This is because less points indicates a further overestimate,(think of a triangle vs an
//        // even octagon)
//        pointDistSum /= setPoints.length;
//        float estimatedVolume = (float)(Math.PI * pointDistSum);
//        estimatedVolume = estimatedVolume * (setPoints.length * VOLUME_ESTIMATE_FACTOR / 100.0f);
        //System.out.println(estimatedVolume);
        return estimatedVolume;
    }

    public void setTranslation(float x, float y)
    {
        translation = new Vec2(x, y);

        recalcPoints();
    }

    public void setRotation(float r, boolean radians)
    {
        if(!radians)
        {
            r *= Math.PI / 180.0;
        }

        this.rotation = Formulas.normalizeAngle(r);

        recalcPoints();
    }

    public void translateAndRotate(float x, float y, float r, boolean radians)
    {
        if(!radians)
        {
            r *= Math.PI / 180.0;
        }

        this.rotation = Formulas.normalizeAngle(r);

        translation = new Vec2(x, y);

        recalcPoints();
    }

    private void recalcPoints()
    {
        if(setPoints.length <= 0) System.err.println("POLYGON SET POINTS NOT SET");
        if(pointAngles.length <= 0) System.err.println("POLYGON POINT ANGLES NOT CALCULATED");


        for(int i=0; i<setPoints.length; i++)
        {
            if(rotation != 0) {
                float mag = setPoints[i].magnitude();
                float newAngle = pointAngles[i] + rotation;

                //points[i] = setPoints[i].copy();
                float newx = (float) (mag * Math.sin(newAngle));
                float newy = (float) (mag * Math.cos(newAngle));
                //if(newAngle >= Math.PI/4 && newAngle < 3.0f*Math.PI/4) pointAngles[i] += Math.PI;
                //System.out.format("Old x: %f, new X: %f\n", setPoints[i].x, newx);
                points[i] = new Vec2(newx, newy);
            }
            else
            {
                points[i] = setPoints[i];
            }

        }

        for(int i=0; i<setPoints.length; i++)
        {
            float px = points[i].x;
            float py = points[i].y;

            px += translation.x;
            py += translation.y;

            points[i] = new Vec2(px, py);
        }
    }

    public Polygon copy()
    {
        Vec2[] newPoints = new Vec2[setPoints.length];
        for(int i=0; i<setPoints.length; i++)
        {
            newPoints[i] = setPoints[i].copy();
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

    public Vec2[] getCalculatedPoints(){ return points; }
    public Vec2[] getPoints(){ return setPoints; }

    private void printPoints()
    {
        for(Vec2 point: points)
        {
            System.out.printf("X: %f, Y:%f\n", point.x, point.y);
        }
    }
}
