package entities;

import GameManager.GameManager;
import GameManager.UserInputListener;
import Global.Settings;
import PhysicsEngine.Material;
import PhysicsEngine.PhysicsBox;
import PhysicsEngine.PhysicsCircle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Body extends Entity{

    private final static int RADIUS = 20;
    private final static int MAX_AXIS_VELOCITY = 20;
    private final static float ACCELERATION = 1.0f * (60.0f / Settings.getFramerate());

    private final static float ORIENT_SIZE = 2;

    private boolean circle;
    UserInputListener input;

    Shape shape;

    public Body(float x, float y, float width, float height, Material material)
    {
        this.circle = false;
        collisionBox = GameManager.world.addBox(x, y, width, height, material);
        shape = new Rectangle(0, 0, width, height);

        setColor(material);

        Rectangle orient = new Rectangle(ORIENT_SIZE, height/2);
        orient.setX(width/2 - ORIENT_SIZE/2);
        orient.setFill(Color.BLACK);

        visuals.getChildren().addAll(shape, orient);
    }

    public Body(float x, float y, float radius, Material material)
    {
        this.circle = true;
        collisionBox = GameManager.world.addCircle(x, y, radius, material);
        shape = new Circle(0 + radius, 0 + radius, radius);

        setColor(material);

        Rectangle orient = new Rectangle(ORIENT_SIZE, radius);
        orient.setX(radius - ORIENT_SIZE/2);
        orient.setFill(Color.BLACK);

        visuals.getChildren().addAll(shape, orient);
    }

    @Override
    public void update(){

        float yvel = collisionBox.getYvelocity();
        float xvel = collisionBox.getXvelocity();

        float xaccel = 0;
        float yaccel = 0;

        if(input != null) {
            if (input.isDown() && !input.isUp() && yvel < MAX_AXIS_VELOCITY) {
                yaccel = ACCELERATION;
            } else if (!input.isDown() && input.isUp() && yvel > -MAX_AXIS_VELOCITY) {
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

        collisionBox.applyForce(xaccel, yaccel);
    }

    public void draw(float alpha)
    {

        float x, y;
        if(circle)
        {
            x = collisionBox.getX() - ((PhysicsCircle)collisionBox).getRadius() + (alpha * collisionBox.getXvelocity());
            y = collisionBox.getY() - ((PhysicsCircle)collisionBox).getRadius() + (alpha * collisionBox.getYvelocity());
        }
        else
        {
            x = collisionBox.getX() - ((PhysicsBox)collisionBox).getWidth()/2 + (alpha * collisionBox.getXvelocity());
            y = collisionBox.getY() - ((PhysicsBox)collisionBox).getHeight()/2 + (alpha * collisionBox.getYvelocity());
        }

        visuals.setTranslateX(x);
        visuals.setTranslateY(y);

        visuals.setRotate(collisionBox.getOrientation() * 180 / Math.PI);

    }

    public void setColor(Color color)
    {
        shape.setFill(color);
    }

    public void setColor(Material material)
    {
        if(material.equals(Material.Wood))
        {
            shape.setFill(Color.GREEN);
        }
        else if(material.equals(Material.Rock))
        {
            shape.setFill(Color.BROWN);
        }
        else if(material.equals(Material.Metal))
        {
            shape.setFill(Color.LIGHTBLUE);
        }
        else if(material.equals(Material.Bouncy))
        {
            shape.setFill(Color.PINK);
        }
    }

    public void setInput(UserInputListener input)
    {
        this.input = input;
    }
}
