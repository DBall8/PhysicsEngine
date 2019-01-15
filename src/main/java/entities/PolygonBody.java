package entities;

import GameManager.GameManager;
import PhysicsEngine.Material;
import PhysicsEngine.PhysicsPolygon;
import PhysicsEngine.math.Vec2;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class PolygonBody extends Body {
    public PolygonBody(float x, float y, Vec2[] points) {

        PhysicsPolygon polygon = GameManager.world.addPolygon(x, y, points);
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

        Line orient = new Line(0,0,points[0].getX(), points[0].getY());
        orient.setStrokeWidth(ORIENT_SIZE);
        orient.setFill(Color.BLACK);

        visuals.getChildren().addAll(shape, orient);
    }

    public PolygonBody(float x, float y, PhysicsEngine.math.Polygon polygon) {
        //super(x, y, 20, Material.Wood);

        collisionBox = GameManager.world.addPolygon(x, y, polygon);

        Vec2[] points = polygon.getPoints();

        double[] polyPoints = new double[points.length*2];
        for(int i=0; i<points.length; i++)
        {
            polyPoints[2*i] = (points[i].getX());
            polyPoints[2*i + 1] = (points[i].getY());
        }
        shape = new Polygon(polyPoints);
        shape.setFill(Color.ORANGE);

        visuals.getChildren().addAll(shape);
    }
}
