package entities;

import GameManager.GameManager;
import PhysicsEngine.Material;
import PhysicsEngine.PhysicsBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Wall extends Entity{

    private final static boolean CIRCLE = false;

    Shape shape;

    public Wall(int x, int y, int width, int height)
    {
        if(CIRCLE)
        {
            collisionBox = GameManager.world.addCircle(x, y, height/2.0f, Material.Static);
            shape = new Circle(x, y, height/2);
        }
        else
        {
            collisionBox = GameManager.world.addBox(x, y, width, height, Material.Static);
            shape = new Rectangle(x - width/2, y - height/2, width, height);
        }
        shape.setFill(Color.BLACK);

        visuals.getChildren().add(shape);
    }

    public void draw(float alpha)
    {
        if(CIRCLE)
        {
            ((Circle)shape).setCenterX(collisionBox.getX());
            ((Circle)shape).setCenterY(collisionBox.getY());
        }
        else
        {
            ((Rectangle)shape).setX(collisionBox.getX() - ((PhysicsBox)collisionBox).getWidth()/2);
            ((Rectangle)shape).setY(collisionBox.getY() - ((PhysicsBox)collisionBox).getHeight()/2);
        }

    }
}
