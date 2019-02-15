package physicsEngine;

import physicsEngine.debug.Debugger;
import physicsEngine.math.Vec2;
import javafx.scene.Group;

public class WorldSettings {

    private float timeStep = 1.0f/120.0f;
    private float scaledTimeStep = timeStep; // scaled time step to speed up moving things along
    private float collisionPrecision = 1; // How many times it iterates through the collision, stops stacks squashing
    private float timeScaleFactor = 1; // Scale from original update frame rate so scale forces

    private boolean friction = true; // true if there is friction between objects
    private float gravity = 10; // the amount of gravity (10 is normal)
    private Vec2 gravityDirection = new Vec2(0, 1);

    private Debugger debugger = null;

    WorldSettings(){}

    public float getTimeStep() {
        return timeStep;
    }

    public float getScaledTimeStep() {
        return scaledTimeStep;
    }

    public float getCollisionPrecision() {
        return collisionPrecision;
    }

    public float getTimeScaleFactor() {
        return timeScaleFactor;
    }

    public boolean isFriction() {
        return friction;
    }

    public float getGravity() {
        return gravity;
    }

    public Vec2 getGravityDirection() { return gravityDirection; }

    public boolean canDebug(){ return debugger != null; }

    public Debugger getDebugger(){ return debugger; }

    public void addDebugView(Group group){ this.debugger = new Debugger(group); }

    // ------------------------------------------------------------------------------

    void setTimeStep(float timeStep) {
        this.timeStep = timeStep;
    }

    void setScaledTimeStep(float scaledTimeStep) {
        this.scaledTimeStep = scaledTimeStep;
    }

    void setCollisionPrecision(float collisionPrecision) {
        this.collisionPrecision = collisionPrecision;
    }

    void setTimeScaleFactor(float timeScaleFactor) {
        this.timeScaleFactor = timeScaleFactor;
    }

    void setFriction(boolean friction) {
        this.friction = friction;
    }

    void setGravity(float gravity) {
        this.gravity = gravity;
    }

    void setGravityDirection(Vec2 gravityDirection) { this.gravityDirection = gravityDirection; }
}
