package physicsEngine.debug;

import physicsEngine.math.Face;
import physicsEngine.math.Point;
import physicsEngine.math.Vec2;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Debugger {

    private static final int POINT_RADIUS = 6;
    private static final int LINE_THICKNESS = 3;
    private static final int LINE_LENGTH = 800;
    private static final float NORMAL_LENGTH = 10.0f;
    private static final float VEC_LENGTH = 50;

    private Group view;

    public Debugger(Group view)
    {
        this.view = view;
    }

    public void drawPoint(Point p, Color c)
    {
        Circle circle = new Circle(p.getX(), p.getY(), POINT_RADIUS);
        circle.setFill(c);
        view.getChildren().add(circle);
    }

    public void drawPoint(Point p, Color c, int size)
    {
        Circle circle = new Circle(p.getX(), p.getY(), size);
        circle.setFill(c);
        view.getChildren().add(circle);
    }

    public void drawPoint(Vec2 p, Color c)
    {
        Circle circle = new Circle(p.getX(), p.getY(), POINT_RADIUS);
        circle.setFill(c);
        view.getChildren().add(circle);
    }

    public void drawFace(Face face, Color c)
    {
        Line line = new Line(face.getP1().getX(), face.getP1().getY(), face.getP2().getX(), face.getP2().getY());
        line.setStroke(c);
        line.setStrokeWidth(LINE_THICKNESS);
        view.getChildren().add(line);
    }

    public void drawNormal(Face face, Vec2 normal, Color c)
    {
        float midx = (face.getP1().getX() + face.getP2().getX()) / 2.0f;
        float midy = (face.getP1().getY() + face.getP2().getY()) / 2.0f;
        Line line = new Line(midx, midy,
                midx + (normal.getX() * NORMAL_LENGTH),
                midy + (normal.getY() * NORMAL_LENGTH));
        line.setStroke(c);
        line.setStrokeWidth(LINE_THICKNESS);
        view.getChildren().add(line);
    }

    public void drawLine(physicsEngine.math.Line l, Color c)
    {
        Line line;
        if(l.isVertical())
        {
            line = new Line(l.getYIntercept(), l.getYAt(0), l.getYIntercept(), LINE_LENGTH);
        }
        else
            {
            line = new Line(0, l.getYAt(0), LINE_LENGTH, l.getYAt(LINE_LENGTH));
        }

        line.setStroke(c);
        line.setStrokeWidth(LINE_THICKNESS / 2.0f);
        view.getChildren().add(line);
    }

    public void drawVec(Point pos, Vec2 vec, Color c)
    {
        Line line = new Line(pos.getX(), pos.getY(),
                        pos.getX() + (vec.getX() * VEC_LENGTH), pos.getY() + (vec.getY() * VEC_LENGTH));
        line.setStroke(c);
        line.setStrokeWidth(LINE_THICKNESS);
        view.getChildren().add(line);
    }

    public void clear()
    {
        view.getChildren().clear();
    }
}
