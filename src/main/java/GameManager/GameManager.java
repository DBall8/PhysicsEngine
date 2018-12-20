package GameManager;

import Global.Settings;
import PhysicsEngine.Material;
import PhysicsEngine.PhysicsWorld;
import entities.IObject;
import entities.Body;
import entities.Wall;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class GameManager extends Pane {

    private Scene scene;
    private List<IObject> objects = new ArrayList<>();
    private int width, height;
    GameTime time;

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

        Body p2 = new Body(100, 100, false, Material.Metal);
        addObject(p2);

        Body p3 = new Body(400, 400, true, Material.Rock);
        addObject(p3);

        Wall wall1 = new Wall(0, 400, 20, 800);
        addObject(wall1);

        Wall wall2 = new Wall(800, 400, 20, 800);
        addObject(wall2);

        Wall wall3 = new Wall(400, 0, 780, 20);
        addObject(wall3);

        Wall wall4 = new Wall(400, 800, 780, 20);
        addObject(wall4);

        time.play();
    }

    public void calculateFrame()
    {
        // Update objects
        for(IObject o: objects)
        {
            o.update();
        }

        update();

        // Run physics engine
        float alpha = world.update(1.0f/Settings.getFramerate());

        // Draw objects at resulting locations
        for(IObject o: objects){
            o.draw(alpha);
        }
    }

    private void update()
    {
        if(input == null) return;

        if(input.isMousePressed())
        {
            boolean circle = Math.random() > 0.5;
            Body newBody = new Body(input.getMouseX(), input.getMouseY(), circle, Material.Rock);
            addObject(newBody);
        }
    }

    private void addObject(IObject o)
    {
        objects.add(o);
        this.getChildren().add(o.getVisuals());
    }

}
