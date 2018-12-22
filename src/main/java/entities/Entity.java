package entities;

import PhysicsEngine.entities.CollidableObject;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public abstract class Entity {

    Shape visuals;
    CollidableObject collisionBox;

    Entity(){}

    public abstract void draw(float alpha);

    public void update(){}

    public Shape getVisuals() { return visuals; }

    public CollidableObject getCollisionBox() {
        return collisionBox;
    }

    public void setColor(Color color)
    {
        visuals.setFill(color);
    }
}
