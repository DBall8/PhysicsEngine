package PhysicsEngine.math;

public class Face {
    private Point p1;
    private Point p2;

    private float slope;
    private boolean infiniteSlope = false;

    public Face(){
        p1 = null;
        p2 = null;
    }

    public Face(Point p1, Point p2)
    {
        this.p1 = p1;
        this.p2 = p2;
        findSlope();
    }

    private void findSlope()
    {
        if(p1.getX() == p2.getX())
        {
            infiniteSlope = true;
            return;
        }

        slope = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
    }

    public void set(Point p1, Point p2)
    {
        this.p1 = p1;
        this.p2 = p2;
        findSlope();
    }

    public void translate(float x, float y)
    {
        p1 = new Point(p1.getX() + x, p1.getY() + y);
        p2 = new Point(p2.getX() + x, p2.getY() + y);
    }

    public void rotateAboutOrigin(float angle, boolean isRadians)
    {
        if(!isRadians)
        {
            angle = Formulas.toRadians(angle);
        }

        p1 = p1.getRotatedPoint(angle);
        p2 = p2.getRotatedPoint(angle);
    }

    // TODO add exceptions
    public float getYAt(float x)
    {
        if((x < p1.getX() && x < p2.getX()) ||
                (x > p1.getX() && x > p2.getX()))
        {
            System.err.println("POINT NOT ON LINE");
            return 0;
        }

        // Infinite slope
        if(infiniteSlope)
        {
            return p1.getY();
        }

        return slope * (x - p1.getX()) + p1.getY();
    }

    // TODO add exceptions
    public float getXAt(float y)
    {
        if((y < p1.getY() && y < p2.getY()) ||
                (y > p1.getY() && y > p2.getY()))
        {
            System.err.println("POINT NOT ON LINE");
            return 0;
        }

        if(slope == 0)
        {
            return p1.getX();
        }

        return (1.0f / slope) * (y - p1.getY()) + p1.getX();
    }

    public Line getLine()
    {
        if(p1.getX() == p2.getX())
        {
            return new Line(p1.getX());
        }

        float slope = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
        float b = p1.getY() - (slope * p1.getX());
        return new Line(slope, b);
    }

    public Point getP1(){ return p1; }
    public Point getP2(){ return p2; }
    public Vec2 getVec(){ return new Vec2(p2.getX() - p1.getX(), p2.getY() - p1.getY()); }
    public Face copy(){ return new Face(p1, p2); }
}
