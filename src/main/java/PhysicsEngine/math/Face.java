package PhysicsEngine.math;

public class Face {
    private Point p1;
    private Point p2;

    private boolean empty = true;

    public Face(){
        p1 = null;
        p2 = null;
        empty = true;
    }
    public Face(Point p1, Point p2)
    {
        this.p1 = p1;
        this.p2 = p2;
        empty = false;
    }

    public void set(Point p1, Point p2)
    {
        if(!empty)
        {
            System.err.println("Cannot set face: face already contains values.");
            return;
        }

        this.p1 = p1;
        this.p2 = p2;
        empty = false;
    }

    public void translate(float x, float y)
    {
        p1 = new Point(p1.getX() + x, p1.getY() + y);
        p2 = new Point(p2.getX() + x, p2.getY() + y);
    }

    public void rotateTo(float angle, boolean isRadians)
    {
        if(!isRadians)
        {
            angle = Formulas.toRadians(angle);
        }

        float dx, dy, oldAngle, mag, newAngle, newx, newy;

        dx = p1.getX();
        dy = -1.0f* p1.getY();
        oldAngle = (float)Math.atan(dx / dy);
        // Add Pi for the half the rotation to adjust for the range of inverse tan
        if(dy <= 0) oldAngle += Math.PI;


        // get the distance from the center and the new angle of the point
        mag = p1.getVec().magnitude();
        newAngle = oldAngle + angle;

        // Calculate the points new x and y coordinates
        newx = (float) (mag * Math.sin(newAngle));
        newy = (float) (-mag * Math.cos(newAngle));
        //if(newAngle >= Math.PI/4 && newAngle < 3.0f*Math.PI/4) pointAngles[i] += Math.PI;
//          System.out.format("Old x: %f, new X: %f\n", originPoints[i].x, newx);
//          System.out.format("Old y: %f, new Y: %f\n\n", originPoints[i].y, newy);
        p1 = new Point(newx, newy);

        dx = p2.getX();
        dy = -1.0f* p2.getY();
        oldAngle = (float)Math.atan(dx / dy);
        // Add Pi for the half the rotation to adjust for the range of inverse tan
        if(dy <= 0) oldAngle += Math.PI;


        // get the distance from the center and the new angle of the point
        mag = p2.getVec().magnitude();
        newAngle = oldAngle + angle;

        // Calculate the points new x and y coordinates
        newx = (float) (mag * Math.sin(newAngle));
        newy = (float) (-mag * Math.cos(newAngle));
        //if(newAngle >= Math.PI/4 && newAngle < 3.0f*Math.PI/4) pointAngles[i] += Math.PI;
//          System.out.format("Old x: %f, new X: %f\n", originPoints[i].x, newx);
//          System.out.format("Old y: %f, new Y: %f\n\n", originPoints[i].y, newy);
        p2 = new Point(newx, newy);
    }

    public boolean isEmpty() { return empty; }
    public Point getP1(){ return p1; }
    public Point getP2(){ return p2; }
    public Vec2 getVec(){ return new Vec2(p2.getX() - p1.getX(), p2.getY() - p1.getY()); }
}
