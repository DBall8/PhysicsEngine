package Global;

import javafx.scene.Group;

public class DebugGlobal {
    private final static boolean DEBUG = false;

    private Group debugView = new Group();

    public static boolean IsDebug(){ return DEBUG; }
    public static Group getDebugView(){ return GetInstance().debugView; }
    public static void clearDebugView(){ GetInstance().debugView.getChildren().clear(); }

    private DebugGlobal(){}
    private static class DebugGlobal_{
        private final static DebugGlobal Instance = new DebugGlobal();
    }
    private static DebugGlobal GetInstance(){ return DebugGlobal_.Instance; }
}
