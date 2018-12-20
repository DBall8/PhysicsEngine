package entities;

import javafx.scene.shape.Shape;

public interface IObject {
    public void draw(float alpha);
    public Shape getVisuals();
    public void update();
}
