package entities;

import GameManager.GameManager;
import PhysicsEngine.math.Formulas;
import PhysicsEngine.math.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

public class PolygonBody extends Ship {

    Shape shape;
    Rotate rotate;
    Color color = Color.ORANGE;

    public PolygonBody(float x, float y, Point[] points) {

        collisionBox = GameManager.world.addPolygon(x, y, points);

        double[] polyPoints = new double[points.length*2];
        for(int i=0; i<points.length; i++)
        {
            polyPoints[2*i] = (points[i].getX());
            polyPoints[2*i + 1] = (points[i].getY());
        }
        shape = new Polygon(polyPoints);
        shape.setFill(Color.ORANGE);

        Line orient = new Line(0,0,points[0].getX(), points[0].getY());
        orient.setStrokeWidth(ORIENT_SIZE);
        orient.setFill(Color.BLACK);

        visuals.getChildren().addAll(shape, orient);

        rotate = new Rotate(0, 0, 0);
        visuals.getTransforms().add(rotate);
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

        Line orient = new Line(0,0,points[0].getX(), points[0].getY());
        orient.setStrokeWidth(ORIENT_SIZE);
        orient.setFill(Color.BLACK);

        visuals.getChildren().addAll(shape, orient);

        rotate = new Rotate(0, 0, 0);
        visuals.getTransforms().add(rotate);
    }

    @Override
    public void update() {
        super.update();
        collisionBox.setOrientation(angle);
    }

    @Override
    public void draw(float alpha) {
//        PhysicsEngine.math.Polygon p = polygon.getPolygon();
//        //p.setTranslation(0,0);
//        p.translateAndRotate(polygon.getX(), polygon.getY(), angle, true);
//        Point[] points = p.getCalculatedPoints();
//
//        double[] polyPoints = new double[points.length*2];
//        for(int i=0; i<points.length; i++)
//        {
//            polyPoints[2*i] = (points[i].getX());
//            polyPoints[2*i + 1] = (points[i].getY());
//        }
//        shape = new Polygon(polyPoints);
//        shape.setFill(color);
//        visuals.getChildren().set(0, shape);

        rotate.setAngle(Formulas.toDegrees(angle));
        visuals.setTranslateX(collisionBox.getX());
        visuals.setTranslateY(collisionBox.getY());
    }

    public void setColor(Color color)
    {
        this.color = color;
    }
}
