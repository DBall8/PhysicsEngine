package entities;

import GameManager.GameManager;
import GameManager.UserInputListener;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Ship extends Entity {

    private final static int WIDTH = 40;
    private final static int HEIGHT = 50;
    private final static float RACCEL = (float)(Math.PI/128);
    private final static float ACCEL = 0.5f;
    private final static float MAX_VELOCITY = 20;

    private UserInputListener input;
    private float angle = 0; // radians

    public Ship(int x, int y){
        collisionBox = GameManager.world.addCircle(x, y, WIDTH/2);
        Polygon shape = new Polygon();
        shape.getPoints().addAll(new Double[]{
                (double)WIDTH/2, 0.0,
                0.0, (double)HEIGHT,
                (double)WIDTH, (double)HEIGHT
        });
        shape.setFill(Color.ORANGE);

        visuals.getChildren().add(shape);
    }

    @Override
    public void update(){
        if(input == null) return;

        boolean angleChanged = false;
        if(input.isLeft() && !input.isRight())
        {
            angle -= RACCEL;
            angleChanged = true;
        }
        else if(!input.isLeft() && input.isRight())
        {
            angle += RACCEL;
            angleChanged = true;
        }

        if(angleChanged)
        {
            if(angle > 2.0f * Math.PI)
            {
                angle -= 2.0f * Math.PI;
            }
            else if(angle < 0)
            {
                angle += 2.0f * Math.PI;
            }
        }

        if(input.isUp() && !input.isDown() && collisionBox.getVelocity() < MAX_VELOCITY)
        {
            collisionBox.applyForceInDirection(ACCEL, angle);
        }
    }

    @Override
    public void draw(float alpha) {
        float x = collisionBox.getX() + (alpha * collisionBox.getXvelocity());
        float y = collisionBox.getY() + (alpha * collisionBox.getYvelocity());
        visuals.setTranslateX(x - WIDTH/2);
        visuals.setTranslateY(y - HEIGHT/2);
        visuals.setRotate(angle * 180 / Math.PI);
    }

    public void setInput(UserInputListener input)
    {
        this.input = input;
    }
}