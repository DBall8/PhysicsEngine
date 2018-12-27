package entities;

import GameManager.GameManager;
import GameManager.UserInputListener;
import PhysicsEngine.Vec2;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Ship extends Entity {

    private final static int WIDTH = 40;
    private final static int HEIGHT = 50;
    private final static float RACCEL = (float)(Math.PI/128);
    private final static float ACCEL = 1;
    private final static float MAX_VELOCITY = 20;

    private UserInputListener input;
    private float angle = 0;

    public Ship(int x, int y){
        collisionBox = GameManager.world.addCircle(x, y, WIDTH/2);
        Polygon shape = new Polygon();
        shape.getPoints().addAll(new Double[]{
                (double)WIDTH/2, 0.0,
                0.0, (double)HEIGHT,
                (double)WIDTH, (double)HEIGHT
        });
        shape.setFill(Color.ORANGE);

        visuals = shape;
    }

    @Override
    public void update(){
        if(input == null) return;

        if(input.isLeft() && !input.isRight())
        {
            angle -= RACCEL;
        }
        else if(!input.isLeft() && input.isRight())
        {
            angle += RACCEL;
        }

        if(input.isUp() && !input.isDown() && collisionBox.getVelocity() < MAX_VELOCITY)
        {
            float forceX = (float)(ACCEL * Math.sin(angle));
            float forceY = (float)(-ACCEL * Math.cos(angle));
            collisionBox.applyForce(new Vec2(forceX, forceY));
        }
    }

    @Override
    public void draw(float alpha) {
        float x = collisionBox.getX() + (alpha * collisionBox.getXvelocity());
        float y = collisionBox.getY() + (alpha * collisionBox.getYvelocity());
        visuals.setTranslateX(collisionBox.getX() - WIDTH/2);
        visuals.setTranslateY(collisionBox.getY() - HEIGHT/2);
        visuals.setRotate(angle * 180 / Math.PI);
    }

    public void setInput(UserInputListener input)
    {
        this.input = input;
    }
}
