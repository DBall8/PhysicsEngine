package GameManager;

import java.util.Timer;
import java.util.TimerTask;

public class Ability {

    private TimerTask activeTask;
    private float cooldown;
    private boolean isReady;

    public Ability(float cooldownSeconds)
    {
        cooldown = cooldownSeconds;
        isReady = true;
    }

    public void use()
    {
        isReady = false;
        Timer timer = new Timer();
        if(activeTask != null) activeTask.cancel();
        activeTask = new TimerTask() {
            @Override
            public void run() {
                isReady = true;
                timer.cancel();
            }
        };
        timer.schedule(activeTask, (int)(cooldown * 1000));
    }

    public boolean isReady()
    {
        return isReady;
    }
}
