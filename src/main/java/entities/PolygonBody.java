package entities;

import GameManager.GameManager;
import PhysicsEngine.PhysicsPolygon;
import PhysicsEngine.math.Point;
import PhysicsEngine.math.Vec2;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class PolygonBody extends Ship {

    PhysicsPolygon polygon;
    Shape shape;
    Color color = Color.ORANGE;

    public PolygonBody(float x, float y, Point[] points) {

        polygon = GameManager.world.addPolygon(x, y, points);
        collisionBox = polygon;
        points = polygon.getPolygon().getPoints();

        double[] polyPoints = new double[points.length*2];
        for(int i=0; i<points.length; i++)
        {
            polyPoints[2*i] = (points[i].getX());
            polyPoints[2*i + 1] = (points[i].getY());
        }
        shape = new Polygon(polyPoints);
        shape.setFill(Color.ORANGE);

//        Line orient = new Line(0,0,points[0].getX(), points[0].getY());
//        orient.setStrokeWidth(ORIENT_SIZE);
//        orient.setFill(Color.BLACK);

        visuals.getChildren().addAll(shape/*, orient*/);
    }

    public PolygonBody(float x, float y, PhysicsEngine.math.Polygon polygon) {
        //super(x, y, 20, Material.Wood);

        collisionBox = GameManager.world.addPolygon(x, y, polygon);

        Point[] points = polygon.getPoints();

        double[] polyPoints = new double[points.length*2];
        for(int i=0; i<points.length; i++)
        {
            polyPoints[2*i] = (points[i].getX());
            polyPoints[2*i + 1] = (points[i].getY());
        }
        shape = new Polygon(polyPoints);
        shape.setFill(color);

        visuals.getChildren().addAll(shape);
    }

    @Override
    public void update() {
        super.update();
        polygon.setOrientation(angle);
    }

    @Override
    public void draw(float alpha) {
        PhysicsEngine.math.Polygon p = polygon.getPolygon();
        //p.setTranslation(0,0);
        p.translateAndRotate(polygon.getX(), polygon.getY(), angle, true);
        Point[] points = p.getCalculatedPoints();

        double[] polyPoints = new double[points.length*2];
        for(int i=0; i<points.length; i++)
        {
            polyPoints[2*i] = (points[i].getX());
            polyPoints[2*i + 1] = (points[i].getY());
        }
        shape = new Polygon(polyPoints);
        shape.setFill(color);
        visuals.getChildren().set(0, shape);
    }

    public void setColor(Color color)
    {
        this.color = color;
    }
}
