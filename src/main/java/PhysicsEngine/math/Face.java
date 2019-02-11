package PhysicsEngine.math;

public class Face {
    private Point p1;
    private Point p2;

    public Face(Point p1, Point p2)
    {
        this.p1 = p1;
        this.p2 = p2;
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
