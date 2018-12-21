package Global;

public class Settings {

    private static final int FRAMERATE = 120;
    private static int WIDTH = 800;
    private static int HEIGHT = 800;
    private static final boolean GRAVITY = true;

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
    public static boolean getGravity(){ return getInstance().GRAVITY; }
}
