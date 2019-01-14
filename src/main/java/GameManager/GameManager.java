package GameManager;

import Global.Settings;
import PhysicsEngine.Material;
import PhysicsEngine.PhysicsCircle;
import PhysicsEngine.PhysicsPolygon;
import PhysicsEngine.PhysicsWorld;
import PhysicsEngine.math.Polygon;
import PhysicsEngine.math.Vec2;
import entities.*;
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

    public static PhysicsWorld world = new PhysicsWorld(Settings.getGravity(), false);

    public GameManager(){
        super();
        this.width = Settings.getWindowWidth();
        this.height = Settings.getWindowHeight();

//        world.setGravityDirection(1, 0);

        time = new GameTime(this);
    }

    public void start(Scene scene){
        this.scene = scene;
        input = new UserInputListener(scene);

        PhysicsWorld world = new PhysicsWorld();
        PhysicsCircle circle = world.addCircle(10, 10, 10);

        Entity p;
        if(Settings.isShip())
        {
            Ship s = new Ship(50, 50);
            s.setInput(input);
            p = s;
        }
        else
        {
            Body b = new Body(20, 20, 40, 40, Material.Wood);
            b.setInput(input);
            p = b;
        }

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

        PolygonBody polyBody = new PolygonBody(400, 400, new Vec2[]{
                new Vec2(40, 0),
                new Vec2(40, 40),
                new Vec2(0, 40),
                new Vec2(0, 0),
        });

        addObject(polyBody);

        PhysicsPolygon pp = (PhysicsPolygon)(polyBody.getCollisionBox());
        Polygon poly = pp.getPolygon();

        PolygonBody polyBody2 = new PolygonBody(600, 100, new Vec2[]{
                new Vec2(10, 0),
                new Vec2(10, 10),
                //new Vec2(0, 10),
                new Vec2(0, 0),
        });
        addObject(polyBody2);

        PolygonBody polyBody3 = new PolygonBody(500, 500, new Vec2[]{
                new Vec2(100, 0),
                new Vec2(100, 100),
                new Vec2(0, 100),
                new Vec2(0, 0),
        });
        addObject(polyBody3);

        PolygonBody polyBody4 = new PolygonBody(100, 600, new Vec2[]{
                new Vec2(0, 0),
                new Vec2(15, 5),
                new Vec2(20, 20),
                new Vec2(15, 35),
                new Vec2(0, 40),
                new Vec2(-15, 35),
                new Vec2(-20, 20),
                new Vec2(-15, 5),
        });
        addObject(polyBody4);

//        PhysicsPolygon polygon = world.addPolygon(400, 400, new Vec2[]{
//                new Vec2(10, 0),
//                new Vec2(20, 10),
//                new Vec2(10, 20),
//                new Vec2(0, 10),
//        });

        PhysicsPolygon polygon2 = world.addPolygon(400, 400, new Vec2[]{
                new Vec2(10, 0),
                new Vec2(10, 10),
                //new Vec2(0, 10),
                new Vec2(0, 0),
        });

        PhysicsPolygon polygon3 = world.addPolygon(400, 400, new Vec2[]{
                new Vec2(100, 0),
                new Vec2(100, 100),
                new Vec2(0, 100),
                new Vec2(0, 0),
        });

        PhysicsPolygon polygon4 = world.addPolygon(400, 400, new Vec2[]{
                new Vec2(0, 0),
                new Vec2(12, 12),
                new Vec2(20, 20),
                new Vec2(12, 22),
                new Vec2(20, 40),
                new Vec2(0, 22),
                new Vec2(-20, 20),
                new Vec2(-12, 12),

        });

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
            Body newBody = new Body(input.getMouseX(), input.getMouseY(), 40, 40, Material.Rock);
            addObject(newBody);
        }

        if(Settings.getGravity() > 0 && input.isUp() && p1.getCollisionBox().isTouching(ground.getCollisionBox()))
        {
            p1.getCollisionBox().applyForce(0, -20);
        }
    }

    private void addObject(Entity o)
    {
        objects.add(o);
        this.getChildren().add(o.getVisuals());
    }

}
