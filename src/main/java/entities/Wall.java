package entities;

import GameManager.GameManager;
import PhysicsEngine.Material;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Wall extends Entity{

    int width;
    int height;
    Shape shape;

    public Wall(int x, int y, int width, int height)
    {
        this.width = width;
        this.height = height;
        collisionBox = GameManager.world.addBox(x, y, width, height, Material.Static);
        shape = new Rectangle(x - width/2, y - height/2, width, height);
        shape.setFill(Color.BLACK);

        visuals.getChildren().add(shape);
    }

    public void draw(float alpha)
    {
        ((Rectangle)shape).setX(collisionBox.getX() - width/2);
        ((Rectangle)shape).setY(collisionBox.getY() - height/2);
    }
}
