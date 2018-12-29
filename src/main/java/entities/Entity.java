package entities;

import PhysicsEngine.PhysicsObject;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public abstract class Entity {

    Shape visuals;
    PhysicsObject collisionBox;

    Entity(){}

    public abstract void draw(float alpha);

    public void update(){}

    public Shape getVisuals() { return visuals; }

    public PhysicsObject getCollisionBox() {
        return collisionBox;
    }

    public void setColor(Color color)
    {
        visuals.setFill(color);
    }
}
