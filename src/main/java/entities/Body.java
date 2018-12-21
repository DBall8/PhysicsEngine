package entities;

import GameManager.GameManager;
import GameManager.UserInputListener;
import Global.Settings;
import PhysicsEngine.Material;
import PhysicsEngine.Vec2;
import PhysicsEngine.entities.CollidableCircle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Body extends Entity{

    private final static int RADIUS = 20;
    private final static int MAX_AXIS_VELOCITY = 20;
    private final static float ACCELERATION = 0.5f * (60.0f / Settings.getFramerate());

    private boolean circle;
    UserInputListener input;

    public Body(float x, float y, boolean circle, Material material)
    {
        this.circle = circle;
        if(circle)
        {
            collisionBox = GameManager.world.addCircle(x, y, RADIUS, material);
            visuals = new Circle(x, y, RADIUS);
        }
        else
        {
            collisionBox = GameManager.world.addBox(x, y, RADIUS*2, RADIUS*2, material);
            visuals = new Rectangle(x - RADIUS, y - RADIUS, RADIUS*2, RADIUS*2);
        }

        setColor(material);

    }

    @Override
    public void update(){

        float yvel = collisionBox.getYvelocity();
        float xvel = collisionBox.getXvelocity();

        if(Settings.getGravity() && yvel < MAX_AXIS_VELOCITY)
        {
            collisionBox.applyForce(new Vec2(0, 1));
        }
        if(input != null) {
            if (input.isDown() && !input.isUp() && yvel < MAX_AXIS_VELOCITY) {
                yvel += ACCELERATION;
            } else if (!input.isDown() && input.isUp() && yvel > -MAX_AXIS_VELOCITY) {
                yvel -= ACCELERATION;
            } else {
                if (Math.abs(yvel) >= 0.1f) {
                    yvel -= Math.abs(yvel) / yvel * 0.1f;
                } else {
                    yvel = 0;
                }
            }

            if (input.isRight() && !input.isLeft() && xvel < MAX_AXIS_VELOCITY) {
                xvel += ACCELERATION;
            } else if (!input.isRight() && input.isLeft() && xvel > -MAX_AXIS_VELOCITY) {
                xvel -= ACCELERATION;
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

    public void draw(float alpha)
    {
        if(circle)
        {
            float x = collisionBox.getX() + (alpha * collisionBox.getXvelocity());
            float y = collisionBox.getY() + (alpha * collisionBox.getYvelocity());
            ((Circle)visuals).setCenterX(x);
            ((Circle)visuals).setCenterY(y);
        }
        else
        {
            float x = collisionBox.getX() - collisionBox.getWidth()/2 + (alpha * collisionBox.getXvelocity());
            float y = collisionBox.getY() - collisionBox.getHeight()/2 + (alpha * collisionBox.getYvelocity());
            ((Rectangle)visuals).setX(x);
            ((Rectangle)visuals).setY(y);
        }

    }

    public void setColor(Material material)
    {
        if(material.equals(Material.Wood))
        {
            visuals.setFill(Color.GREEN);
        }
        else if(material.equals(Material.Rock))
        {
            visuals.setFill(Color.BROWN);
        }
        else if(material.equals(Material.Metal))
        {
            visuals.setFill(Color.LIGHTBLUE);
        }
        else if(material.equals(Material.Bouncy))
        {
            visuals.setFill(Color.PINK);
        }
    }

    public void setInput(UserInputListener input)
    {
        this.input = input;
    }
}
