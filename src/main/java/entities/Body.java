package entities;

import GameManager.GameManager;
import GameManager.UserInputListener;
import Global.Settings;
import PhysicsEngine.entities.CollidableCircle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Body extends Entity{

    private final static int RADIUS = 20;
    private final static int MAX_AXIS_VELOCITY = 20;

    private boolean circle;
    UserInputListener input;

    public Body(float x, float y, boolean circle)
    {
        this.circle = circle;
        if(circle)
        {
            collisionBox = GameManager.world.addCircle(x, y, RADIUS);
            visuals = new Circle(x, y, RADIUS);
            visuals.setFill(Color.BLUE);
        }
        else
        {
            collisionBox = GameManager.world.addBox(x, y, RADIUS*2, RADIUS*2);
            visuals = new Rectangle(x - RADIUS, y - RADIUS, RADIUS*2, RADIUS*2);
            visuals.setFill(Color.BLUE);
        }

    }

    @Override
    public void update(){

        float yvel = collisionBox.getYvelocity();
        float xvel = collisionBox.getXvelocity();

        if(Settings.getGravity() && yvel < MAX_AXIS_VELOCITY)
        {
            yvel += 2;
        }
        if(input != null) {
            if (input.isDown() && !input.isUp() && yvel < MAX_AXIS_VELOCITY) {
                yvel += 0.5;
            } else if (!input.isDown() && input.isUp() && yvel > -MAX_AXIS_VELOCITY) {
                yvel -= 1.5;
            } else {
                if (Math.abs(yvel) >= 0.1f) {
                    yvel -= Math.abs(yvel) / yvel * 0.1f;
                } else {
                    yvel = 0;
                }
            }

            if (input.isRight() && !input.isLeft() && xvel < MAX_AXIS_VELOCITY) {
                xvel += 0.5;
            } else if (!input.isRight() && input.isLeft() && xvel > -MAX_AXIS_VELOCITY) {
                xvel -= 0.5;
            } else {
                if (Math.abs(xvel) >= 0.1f) {
                    xvel -= Math.abs(xvel) / xvel * 0.1f;
                } else {
                    xvel = 0;
                }
            }
        }

        collisionBox.setXvelocity(xvel);
        collisionBox.setYvelocity(yvel);
    }

    public void draw()
    {
        if(circle)
        {
            ((Circle)visuals).setCenterX(collisionBox.getX());
            ((Circle)visuals).setCenterY(collisionBox.getY());
        }
        else
        {
            ((Rectangle)visuals).setX(collisionBox.getX() - collisionBox.getWidth()/2);
            ((Rectangle)visuals).setY(collisionBox.getY() - collisionBox.getHeight()/2);
        }

    }

    public void setInput(UserInputListener input)
    {
        this.input = input;
    }
}
