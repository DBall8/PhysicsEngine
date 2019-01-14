package entities;

import PhysicsEngine.PhysicsObject;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public abstract class Entity {

    Group visuals = new Group();
    PhysicsObject collisionBox;

    Entity(){}

    public void draw(float alpha)
    {
        visuals.setTranslateX(collisionBox.getX() + (alpha * collisionBox.getXvelocity()));
        visuals.setTranslateY(collisionBox.getY() + (alpha * collisionBox.getYvelocity()));

        visuals.setRotate(collisionBox.getOrientation() * 180 / Math.PI);
    }

    public void update(){}

    public Group getVisuals() { return visuals; }

    public PhysicsObject getCollisionBox() {
        return collisionBox;
    }

}
