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
    private final static float ACCELERATION = 1.0f * (60.0f / Settings.getFramerate());

    private boolean circle;
    UserInputListener input;

    public Body(float x, float y, float width, float height, Material material)
    {
        this.circle = false;
        collisionBox = GameManager.world.addBox(x, y, width, height, material);
        visuals = new Rectangle(x - width / 2, y - height / 2, width, height);

        setColor(material);

    }

    public Body(float x, float y, float radius, Material material)
    {
        this.circle = true;
        collisionBox = GameManager.world.addCircle(x, y, radius, material);
        visuals = new Circle(x, y, radius);

        setColor(material);

    }

    @Override
    public void update(){

        float yvel = collisionBox.getYvelocity();
        float xvel = collisionBox.getXvelocity();

        float xaccel = 0;
        float yaccel = 0;

        if(input != null) {
            if (input.isDown() && !input.isUp() && yvel < MAX_AXIS_VELOCITY && Settings.getGravity() == 0) {
                yaccel = ACCELERATION;
            } else if (!input.isDown() && input.isUp() && yvel > -MAX_AXIS_VELOCITY && Settings.getGravity() == 0) {
                yaccel = -ACCELERATION;
            }

            if (input.isRight() && !input.isLeft() && xvel < MAX_AXIS_VELOCITY) {
                xaccel = ACCELERATION;
            } else if (!input.isRight() && input.isLeft() && xvel > -MAX_AXIS_VELOCITY) {
                xaccel = -ACCELERATION;
            }

            if(input.isBoost())
            {
                xaccel *= 20.0f;
            }
        }

        collisionBox.applyForce(new Vec2(xaccel, yaccel));
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
