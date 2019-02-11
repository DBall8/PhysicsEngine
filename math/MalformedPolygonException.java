package physicsEngine.math;

public class MalformedPolygonException extends Exception{

    private String message;

    public MalformedPolygonException(String message)
    {
        this.message = message;
    }

    public void printMessage()
    {
        System.err.println(message);
    }
}
