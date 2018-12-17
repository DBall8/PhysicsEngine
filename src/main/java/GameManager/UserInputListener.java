package GameManager;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class UserInputListener {
    private boolean up = false;
    private boolean down = false;
    private boolean right = false;
    private boolean left = false;
    private boolean boost = false;
    private boolean mouseDown = false;
    private float mousex = 0;
    private float mousey = 0;

    public float getMouseX() {
        return mousex;
    }

    public float getMouseY() {
        return mousey;
    }

    public boolean isUp() {
        return up;
    }

    public boolean isDown() {
        return down;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isBoost() {
        return boost;
    }

    public boolean isMouseDown() {
        return mouseDown;
    }


    public UserInputListener(Scene scene){
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                keyDown(event);
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                keyUp(event);
            }
        });

        scene.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseMoved(event);
            }
        });

        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!mouseDown){
                    mouseDown = true;
                }
            }
        });

        scene.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseDown = false;
            }
        });

        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { mouseMoved(event); }
        });
    }

    private void mouseMoved(MouseEvent me){
        mousex = (float)me.getSceneX();
        mousey = (float)me.getSceneY();
    }

    private void keyDown(KeyEvent ke){
        switch(ke.getCode()){
            case W:
                if(!up) up = true;
                break;
            case S:
                if(!down) down = true;
                break;
            case A:
                if(!left) left = true;
                break;
            case D:
                if(!right) right = true;
                break;
            case SPACE:
                if(!boost) boost = true;
        }
    }

    private void keyUp(KeyEvent ke){
        switch(ke.getCode()){
            case W:
                up = false;
                break;
            case S:
                down = false;
                break;
            case A:
                left = false;
                break;
            case D:
                right = false;
                break;
            case SPACE:
                boost = false;
        }
    }
}
