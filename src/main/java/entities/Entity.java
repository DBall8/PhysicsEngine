package entities;

import PhysicsEngine.entities.CollidableObject;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public abstract class Entity implements IObject {

    Shape visuals;
    CollidableObject collisionBox;

    Entity(){}

    public abstract void draw(float alpha);

    public void update(){}

    public Shape getVisuals() { return visuals; }
}
