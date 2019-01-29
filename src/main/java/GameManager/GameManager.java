package GameManager;

import Global.Settings;
import PhysicsEngine.Material;
import PhysicsEngine.PhysicsWorld;
import PhysicsEngine.math.Point;
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
    PolygonBody testPoly;

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

        Entity p;
        if(Settings.isShip())
        {
            Ship s = new Ship(50, 50);
            s.setInput(input);
            p = s;
        }
        else
        {
            Body b = new Body(20, 20, 20, /*40,*/ Material.Wood);
            b.setInput(input);
            p = b;
        }

        addObject(p);
        p1 = p;
        p1.getCollisionBox().setDebug();

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

//        PolygonBody polyBody = new PolygonBody(400, 400, new Point[]{
//                new Point(40, 0),
//                new Point(40, 40),
//                new Point(0, 40),
//                new Point(0, 0),
//        });
//
//        addObject(polyBody);

//        PhysicsPolygon pp = (PhysicsPolygon)(polyBody.getCollisionBox());
//        Polygon poly = pp.getPolygon();
//
//        PolygonBody polyBody2 = new PolygonBody(600, 100, new Point[]{
//                new Point(10, 0),
//                new Point(10, 10),
//                //new Point(0, 10),
//                new Point(0, 0),
//        });
//        addObject(polyBody2);

//        PolygonBody polyBody3 = new PolygonBody(500, 500, new Point[]{
//                new Point(100, 0),
//                new Point(100, 100),
//                new Point(0, 100),
//                new Point(0, 0),
//        });
//        addObject(polyBody3);

        testPoly = new PolygonBody(600, 600, new Point[]{
                new Point(0, 0),
                new Point(100, 100),
                new Point(100, 0),


        });
        addObject(testPoly);
        testPoly.setInput(input);

        PolygonBody poly1 = new PolygonBody(100, 600, new Point[]{
                new Point(0, 0),
                new Point(25, 15),
                new Point(40, 40),
                new Point(25, 55),
                new Point(0, 60),
                new Point(-25, 55),
                new Point(-40, 40),
                new Point(-25, 15),
        });
        addObject(poly1);

        PolygonBody poly2 = new PolygonBody(300, 200, new Point[]{
                new Point(0, 0),
                new Point(0, 100),
                new Point(100, 100),

        });
        addObject(poly2);

//        PolygonBody polyBodyC = new PolygonBody(420, 420, new Point[]{
//                new Point(40, 0),
//                new Point(40, 40),
//                new Point(0, 40),
//                new Point(0, 0),
//        });
//
//        addObject(polyBodyC);

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
            //Body newBody = new Body(input.getMouseX(), input.getMouseY(), 40, 40, Material.Rock);
            //addObject(newBody);

            PolygonBody newBody = new PolygonBody(input.getMouseX(), input.getMouseY(), new Point[]{
                    new Point(0, 0),
                    new Point(50, 50),
                    new Point(50, 0),
            });
            newBody.setMaterial(Material.Rock);
            float randAngle = (float)(Math.random()*2.0f*Math.PI);
            newBody.setRotation(randAngle);
            addObject(newBody);
        }


        float groundedPercent = world.getGroundedPercent(p1.getCollisionBox());
        //System.out.println(groundedPercent);
        if(groundedPercent > 0.74f)
        {
            ((Body)p1).setColor(Color.AQUA);
            if(Settings.getGravity() > 0 && input.isUp() /*&& p1.getCollisionBox().isTouching(ground.getCollisionBox())*/)
            {
                p1.getCollisionBox().applyForce(0, -20);

            }
        }
        else{
            ((Body)p1).setColor(Color.GREEN);
        }

//        testPoly.setColor(Color.ORANGE);
//        for(Entity e: objects) {
//            if(e.equals(testPoly)) continue;
//            if (testPoly.getCollisionBox().isTouching(e.getCollisionBox())) {
//                testPoly.setColor(Color.CYAN);
//            }
//        }
    }

    private void addObject(Entity o)
    {
        objects.add(o);
        this.getChildren().add(o.getVisuals());
    }

}
