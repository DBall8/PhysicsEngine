package PhysicsEngine.math;

/**
 * Class used for storing points. Cannot be modified once created
 */
public class Point {

    private float x;
    private float y;

    public Point(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Point mult(float scalar)
    {
        return new Point(x * scalar, y * scalar);
    }

    public Point add(Vec2 other)
    {
        return new Point(x + other.x, y + other.y);
    }

    public Point add(float x, float y)
    {
        return new Point(this.x + x, this.y + y);
    }

    public boolean equals(Point other) {
        return (x == other.x) && (y == other.y);
    }

    public Point copy()
    {
        return new Point(x, y);
    }

    public Vec2 getVec() { return new Vec2(x, y); }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public Point clampPointX(Point min, Point max)
    {
        if (x < min.x) {
            return min;
        } else if (x < max.x) {
            return this;
        } else {
            return max;
        }
    }

    public Point clampPointY(Point min, Point max)
    {
        if (y < min.y) {
            return min;
        } else if (y < max.y) {
            return this;
        } else {
            return max;
        }
    }

    public Point getRotatedPoint(float angleRads)
    {
        float oldAngle = (float)Math.atan(x / -y);
        // Add Pi for the half the rotation to adjust for the range of inverse tan
        if(y > 0) oldAngle += Math.PI;

        // get the distance from the center and the new angle of the point
        float mag = getVec().magnitude();
        float newAngle = oldAngle + angleRads;

        // Calculate the points new x and y coordinates
        float newx = (float) (mag * Math.sin(newAngle));
        float newy = (float) (-mag * Math.cos(newAngle));
        //if(newAngle >= Math.PI/4 && newAngle < 3.0f*Math.PI/4) pointAngles[i] += Math.PI;
//          System.out.format("Old x: %f, new X: %f\n", originPoints[i].x, newx);
//          System.out.format("Old y: %f, new Y: %f\n\n", originPoints[i].y, newy);
        return new Point(newx, newy);
    }
}
