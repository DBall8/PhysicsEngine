package entities;

import GameManager.GameManager;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Wall extends Entity{

    private final static boolean CIRCLE = false;

    public Wall(int x, int y, int width, int height)
    {
        if(CIRCLE)
        {
            collisionBox = GameManager.world.addCircle(x, y, height/2.0f);
            collisionBox.setMass(0);
            visuals = new Circle(x, y, height/2);
        }
        else
        {
            collisionBox = GameManager.world.addBox(x, y, width, height);
            collisionBox.setMass(0);
            visuals = new Rectangle(x - width/2, y - height/2, width, height);
        }
        visuals.setFill(Color.BLACK);
    }

    public void draw()
    {
        if(CIRCLE)
        {
            ((Circle)visuals).setCenterX(collisionBox.getX());
            ((Circle)visuals).setCenterY(collisionBox.getY());
        }
        else
        {
            ((Rectangle)visuals).setX(collisionBox.getX() - collisionBox.getWidth()/2);
            ((Rectangle)visuals).setY(collisionBox.getY() - collisionBox.getHeight()/2);
        }

    }
}
