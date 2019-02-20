# Physics Engine (Name Needed) #

This module provides utilities for the calculation of movement and collisions between 2D shapes (circles
and convex polygons).

## Table of Contents ##
1. [Overview](#1.-overview)
2. [Included Classes](#2.-included-classes)\
  2a. [PhysicsObject](#2a.-physicsobject)\
  2b. [PhysicsWorld](#2b.-physicsworld)\
  2c. [Material](#2c.-material)

## 1. Overview ##
The basic usage follows the following steps:

1. Create a PhysicsWorld
2. Add objects to the PhysicsWorld
3. Periodically run the PhysicsWorld's update() method to update the state of all objects

Example:
``` 
world = new PhysicsWorld();
PhysicsObject circle = world.addCircle(x, y, radius);

while(1)
{
    world.update(1.0/120.0);
    Thread.sleep(1.0/120.0);
}
```
NOTE: The value you pass to the update() method is the amount of time forward in seconds that the world should move 

There is much more that can be done, see below for more details.

## 2. Included Classes ##

### 2a. PhysicsObject ###
The PhysicsObject class represents each object in the physics simulation. Each object has a coordinate position and
orientation (in radians). PhysicsObjects can only be created through one of the methods available in a PhysicsWorld, but
can then be controlled through its own methods.

#### Useful Methods ####

**1. Getters and Setters**
``` 
public String getId();
public float getX();
public float getY();
public float getXVelocity();
public float getYVelocity();
public float getVelocity();
public float getAngularVelocity();
public float getOrientation();
public float getMass();

public void setXvelocity(float xvel);
public void setYvelocity(float yvel);
public void setOrientation(float o);
public void setMaterial(Material material);
```

1. getId() -- Each object has a unique (to its world) string ID to identify it
2. getX() -- Gets the object's current x coordinate
3. getY() -- Gets the object's current y coordinate
4. getXVelocity() -- Gets the object's current velocity along the x axis
5. getYVelocity() -- Gets the object's current velocity along the y axis
6. getVelocity() -- Gets the magnitude of the object's velocity
7. getAngularVelocity() -- Gets the angular velocity of the object (in radians/update)
8. getOrientation() -- Gets the orientation (angle) of the object (in radians)
9. getMass() -- Gets the mass of the object. Each object has a uniform mass across its surface.
10. setXVelocity() -- Sets the velocity along the x axis
11. SetYVelocity() -- Sets the velocity along the y axis
12. setOrientation() -- Sets the orientation of the object (radians)
13. setMaterial() -- Sets the material of the object. The object's material is used to set its mass, inertia,
    friction, and bounciness.
    
    
**2. Other important Methods**
```
public void applyForce(float xcomponent, float ycomponent);
public void applyForce(Vec2 force);
public void applyForceInDirection(float magnitude, float angleInRadians);
public void applyTorque(float torque);
public boolean isTouching(PhysicsObject object);
public void ignore(PhysicsObject object);
public void removeIgnore(PhysicsObject object);
public void setCollisionCallback(Callback<PhysicsObject> callback);
```

1. applyForce() -- Applies a force on an object. Can either be called with an x component and y component corresponding
    to the two components of the force vector being applied, or a Vec2 class instance representing the same thing.
2. applyForceInDirection() -- Also applies a force on an object, but takes a magnitude and an angle (in radians) 
    representing the angle to apply the force in. 
3. applyTorque() -- Applies a torque (turning force) on the object. A positive torque applies a rotational force in the
    clockwise direction, and a negative torque counterclockwise.
4. isTouching() -- This method returns true if the PhysicsObject is currently touching the given PhysicsObject
5. ignore() -- This method adds a given PhysicsObject to the list of objects to ignore collisions with. If either of the
    two PhysicsObjects in a collision have the other on its ignore list, it will ignore the collision.
6. removeIgnore() -- This method removes a given object from its ignore list, if it is present.
7. setCollisionCallback() -- This method takes a callback function to call whenever the object collides with something. 
    The callback will provide the PhysicsObject that the object collided with.

### 2b. PhysicsWorld ###
The PhysicsWorld class contains all the PhysicsObjects created from it and controls the updating of each object's position
and orientation. There are several methods for creating PhysicsObjects inside a world, and several methods for changing
the behavior of the world itself.

#### Useful Methods ####
**1. PhysicsObject Creators**
``` 
public PhysicsObject addCircle(float x, float y, float radius);
public PhysicsObject addCircle(float x, float y, float radius, Material material );
public PhysicsObject addBox(float centerx, float centery, float width, float height);
public PhysicsObject addBox(float centerx, float centery, float width, float height, Material material);
public PhysicsObject addPolygon(float centerx, float centery, Point[] points);
public PhysicsObject addPolygon(float centerx, float centery, Point[] points, Material material);
public PhysicsObject addPolygon(float centerx, float centery, float[] points);
public PhysicsObject addPolygon(float centerx, float centery, float[] points, Material material);
public PhysicsObject addPolygon(float centerx, float centery, Polygon polygon);
public PhysicsObject addPolygon(float centerx, float centery, Polygon polygon, Material material);
public void removeObject(PhysicsObject object);
public void removeObject(String objectId);
```
1. addCircle() -- Creates a new circular PhysicsObject. Requires x and y coordinates and a radius. Optionally, it can take 
    a material in the constructor to use, but if none is provided it will use Material.WOOD.
2. addBox() -- Creates a new rectangular PhysicsObject. Requires x and y coordinates, a width, and a height. Optionally, 
    it can take a material in the constructor to use, but if none is provided it will use Material.WOOD.
3. addPolygon() -- There are three ways to create a general polygon shaped PhysicsObject. All three require a center x
    and y coordinate pair. Then, either an array of Points, an array of floats corresponding to points, or a Polygon object
    can be passed to construct the shape of the polygon. If using points, the points do not need to be centered around
    the center coordinate pair or the origin, the construction of the Polygon will handle centering. Optionally,
    any of the construction methods can take a material in the constructor to use, but if none is provided it will use
    Material.WOOD.
4. removeObject() -- This method simply removes an object from the physics simulation. It can be done through either
    the object itself, or through giving the ID of the object.
    
**2. Other Useful Methods** 
```
public float update(float time);
public void setUpdatesPerSecond(int updates);
public void setCollisionPrecision(float precision);
public void setGravity(float gravity);
public void setFriction(boolean friction);
public Vec2 getGroundedVector(PhysicsObject object);
```
1. update() -- This method is used to move the physics engine "forward through time." The method returns an alpha value
    that represents the portion of a time step that was not completed (due to the granularity of its calculation step). 
    This value can be used to estimate the positions of the objects by moving them by the alpha value, if desired.
2. setUpdatesPerSecond() -- Sets the number of calculations done per second (the granularity) or the simulation. The
    higher the value the more accurate the simulation is, but the more taxing it is to run it. Lower values may result in
    objects passing through others when travelling at higher speeds.
3. setCollisionPrecision() -- Sets a value used to make more accurate collisions. Value ranges from 1 to infinity, where
    higher values are more accurate but the simulation is more taxed and may run slower. Lower values will see stacks of
    objects squishing into each other more, but will run smoother.
4. setGravity() -- Sets the strength of gravity. Default is around 10.
5. setFriction() -- Either sets or disables friction in the simulation (True = enabled, false = disabled)
6. getGroundedVector() -- Uses a formula to return information about how perpendicular to gravity the objects the currentt
    PhysicsObject is resting on are. The y component of the resulting vector corresponds to how "grounded" or solidly
    supported to object is (1 is on flat ground, 0 is completely unsupported). The x component corresponds to a scaled
    value of how supported tangentially the object is.

### 2c. Material ###
This class is for storing information about an object. Stored information includes:

1. Density -- Used for calculation mass (mass = density * volume) NOTE: To create immovable objects, use a density of 0.
2. Restitution -- This corresponds to bounciness. Values range from 0 (no bounce) to 1 (full bounce)
3. Static Friction -- The coefficient of static friction of the object. Ranges from 0 (No friction) to infinity (immovable when
    stopped) NOTE: Default is 0.3
4. Dynamic Friction -- The coefficient of dynamic friction of the object. Ranges from 0 (No friction) to infinity (Stop on 
    contact) NOTE: Default is 0.1
    
A couple of pre-made materials are available (NOTE Static is for immovable objects):

Material.Rock = (Density: 0.6, Restitution: 0.1, Static Friction: 0.4, Dynamic Friction: 0.8)\
Material.Wood = (Density: 0.3, Restitution: 0.2, Static Friction: 0.5, Dynamic Friction: 1)\
Material.Metal = (Density: 1.2, Restitution: 0.05, Static Friction: 0.25, Dynamic Friction: 0.5)\
Material.Bouncy = (Density: 0.3, Restitution: 0.8, Static Friction: 0.9, Dynamic Friction: 0.7)\
Material.Static = (Density: 0, Restitution: 0.2, Static Friction: 0.3, Dynamic Friction: 0.1)