package GameManager;
import Global.Settings;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class GameTime{

    private GameManager game;
    private Timeline time;
    private boolean playing = false;
    public GameTime(GameManager game){

        this.game = game;
        time = new Timeline();
        time.setCycleCount(Animation.INDEFINITE);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(1000L / Settings.getFramerate()), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // update positions
                game.calculateFrame();
            }
        });
        time.getKeyFrames().add(keyFrame);
    }

    public void play(){
        if(!playing){
            playing = true;
            time.play();
        }
    }

    public void pause(){
        if(playing){
            playing = false;
            time.pause();
        }
    }
}
