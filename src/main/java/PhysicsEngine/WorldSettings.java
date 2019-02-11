package PhysicsEngine;

import PhysicsEngine.debug.Debugger;
import PhysicsEngine.math.Vec2;
import javafx.scene.Group;

public class WorldSettings {

    private float scaledTimeStep = 1.0f/120.0f; // scaled time step to speed up moving things along
    private float collisionPrecision = 1; // How many times it iterates through the collision, stops stacks squashing
    private float forceScaleFactor = 1; // Scale from original update frame rate so scale forces

    private boolean friction = true; // true if there is friction between objects
    private float gravity = 10; // the amount of gravity (10 is normal)
    private Vec2 gravityDirection = new Vec2(0, 1);

    private Debugger debugger = null;

    WorldSettings(){}

    public float getScaledTimeStep() {
        return scaledTimeStep;
    }

    public float getCollisionPrecision() {
        return collisionPrecision;
    }

    public float getForceScaleFactor() {
        return forceScaleFactor;
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

    void setScaledTimeStep(float scaledTimeStep) {
        this.scaledTimeStep = scaledTimeStep;
    }

    void setCollisionPrecision(float collisionPrecision) {
        this.collisionPrecision = collisionPrecision;
    }

    void setForceScaleFactor(float forceScaleFactor) {
        this.forceScaleFactor = forceScaleFactor;
    }

    void setFriction(boolean friction) {
        this.friction = friction;
    }

    void setGravity(float gravity) {
        this.gravity = gravity;
    }

    void setGravityDirection(Vec2 gravityDirection) { this.gravityDirection = gravityDirection; }
}
