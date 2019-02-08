package entities;

import GameManager.GameManager;
import GameManager.UserInputListener;
import Global.Settings;
import PhysicsEngine.Material;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Body extends Entity{

    private final static boolean CONTROL_VIA_SPIN = false;

    private final static int RADIUS = 20;
    private final static int MAX_AXIS_VELOCITY = 20;
    private final static float ACCELERATION = 1.0f * (60.0f / Settings.getFramerate());
    public final static float JUMP_STRENGTH = 40;
    private final static float SPIN_ACCEL = 10.0f * (60.0f / Settings.getFramerate());

    protected final static float ORIENT_SIZE = 2;

    UserInputListener input;

    Shape shape;

    Body(){}

    public Body(float x, float y, float width, float height, Material material)
    {
        collisionBox = GameManager.world.addBox(x, y, width, height, material);
        shape = new Rectangle(-width/2, -height/2, width, height);

        setColor(material);

        Rectangle orient = new Rectangle(-ORIENT_SIZE/2, -height/2, ORIENT_SIZE, height/2);
        orient.setFill(Color.BLACK);

        visuals.getChildren().addAll(shape, orient);
    }

    public Body(float x, float y, float radius, Material material)
    {
        collisionBox = GameManager.world.addCircle(x, y, radius, material);
        shape = new Circle(0, 0, radius);

        setColor(material);

        Rectangle orient = new Rectangle(-ORIENT_SIZE/2, -radius, ORIENT_SIZE, radius);
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
            } else if (Settings.getGravity() == 0 && !input.isDown() && input.isUp() && yvel > -MAX_AXIS_VELOCITY) {
                yaccel = -ACCELERATION;
            }

            if (input.isRight() && !input.isLeft() && xvel < MAX_AXIS_VELOCITY) {
                xaccel = CONTROL_VIA_SPIN? SPIN_ACCEL : ACCELERATION;
            } else if (!input.isRight() && input.isLeft() && xvel > -MAX_AXIS_VELOCITY) {
                xaccel = CONTROL_VIA_SPIN? -SPIN_ACCEL : -ACCELERATION;
            }

            if(input.isBoost())
            {
                xaccel *= 20.0f;
            }
        }

        if(CONTROL_VIA_SPIN) {
            collisionBox.applyForce(0, yaccel);
            collisionBox.applyTorque(xaccel);
        }
        else{
            collisionBox.applyForce(xaccel, yaccel);
        }
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
