package GameManager;

import Global.Settings;
import PhysicsEngine.PhysicsWorld;
import entities.IObject;
import entities.Player;
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

    public static PhysicsWorld world = new PhysicsWorld();

    public GameManager(){
        super();
        this.width = Settings.getWindowWidth();
        this.height = Settings.getWindowHeight();

        time = new GameTime(this);
    }

    public void start(Scene scene){
        this.scene = scene;
        Player p = new Player(50, 50);
        p.setInput(new UserInputListener(scene));
        addObject(p);

        Player p2 = new Player(100, 100);
        addObject(p2);

        Player p3 = new Player(400, 400);
        addObject(p3);

        Wall wall1 = new Wall(0, 400, 20, 800);
        addObject(wall1);

        Wall wall2 = new Wall(800, 400, 20, 800);
        addObject(wall2);

        time.play();
    }

    public void calculateFrame()
    {
        // Update objects
        for(IObject o: objects)
        {
            o.update();
        }

        // Run physics engine
        world.update();

        // Draw objects at resulting locations
        for(IObject o: objects){
            o.draw();
        }
    }

    private void addObject(IObject o)
    {
        objects.add(o);
        this.getChildren().add(o.getVisuals());
    }

}
