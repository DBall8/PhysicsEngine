package entities;

import GameManager.GameManager;
import PhysicsEngine.Material;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Bullet extends Entity{
    private static final int WIDTH = 5;
    private static final int HEIGHT =10;
    private static final float SPEED = 50;

    public Bullet(float x, float y, float orientation){
        collisionBox = GameManager.world.addBox(x, y, WIDTH, HEIGHT, Material.Metal);
        collisionBox.setOrientation(orientation);
        collisionBox.applyForceInDirection(SPEED, orientation);

        double[] polyPoints = new double[]{
                -WIDTH/2.0f, -HEIGHT/2.0f,
                WIDTH/2.0f, -HEIGHT/2.0f,
                WIDTH/2.0f, HEIGHT/2.0f,
                -WIDTH/2.0f, HEIGHT/2.0f
        };
        Polygon shape = new Polygon(polyPoints);
        shape.setFill(Color.TOMATO);
        visuals.getChildren().add(shape);
    }
}
