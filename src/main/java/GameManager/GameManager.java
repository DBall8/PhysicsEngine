package GameManager;

import Global.Settings;
import PhysicsEngine.Material;
import PhysicsEngine.PhysicsWorld;
import PhysicsEngine.Vec2;
import entities.Body;
import entities.Entity;
import entities.Wall;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GameManager extends Pane {

    private Scene scene;
    private List<Entity> objects = new ArrayList<>();
    private int width, height;
    GameTime time;

    Entity p1;
    Entity ground;

    UserInputListener input;

    public static PhysicsWorld world = new PhysicsWorld();

    public GameManager(){
        super();
        this.width = Settings.getWindowWidth();
        this.height = Settings.getWindowHeight();

        time = new GameTime(this);
    }

    public void start(Scene scene){
        this.scene = scene;
        Body p = new Body(50, 50, 40, 40, Material.Wood);
        input = new UserInputListener(scene);
        p.setInput(input);
        addObject(p);
        p1 = p;

        Body p2 = new Body(100, 50, 40, 40, Material.Metal);
        addObject(p2);

        Body p3 = new Body(400, 50, 20, Material.Rock);
        addObject(p3);

        Wall wall1 = new Wall(-30, Settings.getWindowHeight() / 2, 80, Settings.getWindowHeight());
        addObject(wall1);

        Wall wall2 = new Wall(Settings.getWindowWidth() + 30, Settings.getWindowHeight() / 2, 80, Settings.getWindowHeight());
        addObject(wall2);

        Wall wall3 = new Wall(Settings.getWindowWidth() / 2, -30, Settings.getWindowWidth() - 20, 80);
        addObject(wall3);

        Wall wall4 = new Wall(Settings.getWindowWidth() / 2, Settings.getWindowHeight() + 30, Settings.getWindowWidth() - 20, 80);
        addObject(wall4);
        ground = wall4;

        time.play();
    }

    public void calculateFrame()
    {
        // Update objects
        for(Entity o: objects)
        {
            o.update();
        }

        update();

        // Run physics engine
        float alpha = world.update(1.0f/Settings.getFramerate());

        // Draw objects at resulting locations
        for(Entity o: objects){
            o.draw(alpha);
        }
    }

    private void update()
    {
        if(input == null) return;

        if(input.isMousePressed())
        {
            Body newBody = new Body(input.getMouseX(), input.getMouseY(), 40, 40, Material.Metal);
            addObject(newBody);
        }

        if(p1.getCollisionBox().isGrounded())
        {
            p1.getVisuals().setFill(Color.RED);
        }
        else
        {
            p1.getVisuals().setFill(Color.GREEN);
        }

        if(Settings.getGravity() && input.isUp() && p1.getCollisionBox().isTouching(ground.getCollisionBox()))
        {
            p1.getCollisionBox().applyForce(new Vec2(0, -40));
        }
    }

    private void addObject(Entity o)
    {
        objects.add(o);
        this.getChildren().add(o.getVisuals());
    }

}
