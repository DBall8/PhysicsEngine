package entities;

import javafx.scene.shape.Shape;

public interface IObject {
    public void draw();
    public Shape getVisuals();
    public void update();
}
