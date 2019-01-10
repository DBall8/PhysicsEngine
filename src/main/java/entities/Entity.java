package entities;

import PhysicsEngine.PhysicsObject;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public abstract class Entity {

    Group visuals = new Group();
    PhysicsObject collisionBox;

    Entity(){}

    public abstract void draw(float alpha);

    public void update(){}

    public Group getVisuals() { return visuals; }

    public PhysicsObject getCollisionBox() {
        return collisionBox;
    }

}
