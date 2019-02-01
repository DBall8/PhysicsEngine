package Global;

import java.util.Set;

public class Settings {

    private static final int FRAMERATE = 120;
    private static int WIDTH = 1200;
    private static int HEIGHT = 900;
    private static final float GRAVITY = 10; // default 10

    private static final boolean IS_SHIP = false;

    private static class Settings_{
        private static final Settings instance = new Settings();
    }

    private static Settings getInstance(){ return Settings_.instance; }

    public static void setWindowSize(int width, int height){
        getInstance().WIDTH = width;
        getInstance().HEIGHT = height;
    }
    public static int getFramerate(){ return FRAMERATE; }
    public static int getWindowWidth(){ return getInstance().WIDTH; }
    public static int getWindowHeight(){ return getInstance().HEIGHT; }
    public static float getGravity(){ return getInstance().GRAVITY; }
    public static boolean isShip(){ return getInstance().IS_SHIP;}

    private Settings(){}
}
