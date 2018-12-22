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
        Body p = new Body(50, 50, false, Material.Wood);
        input = new UserInputListener(scene);
        p.setInput(input);
        addObject(p);
        p1 = p;

        Body p2 = new Body(100, 100, false, Material.Metal);
        addObject(p2);

        Body p3 = new Body(400, 400, true, Material.Rock);
        addObject(p3);

        Wall wall1 = new Wall(-30, 400, 80, 800);
        addObject(wall1);

        Wall wall2 = new Wall(830, 400, 80, 800);
        addObject(wall2);

        Wall wall3 = new Wall(400, -30, 780, 80);
        addObject(wall3);

        Wall wall4 = new Wall(400, 830, 780, 80);
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
            boolean circle = Math.random() > 0.5;
            Body newBody = new Body(input.getMouseX(), input.getMouseY(), circle, Material.Bouncy);
            addObject(newBody);
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
