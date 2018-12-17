package MyPhysicsEngine;

import GameManager.GameManager;
import Global.Settings;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Settings.setWindowSize(800, 800);
        GameManager game = new GameManager();

        Scene scene = new Scene(game, 800, 800);

        primaryStage.setScene(scene);
        primaryStage.show();

        game.start(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
