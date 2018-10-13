package metroMapMaker.data;

/**
 * This interface represents a family of draggable shapes.
 * 
 * @author Richard McKenna
 * @author Fanng Dai
 * @version 1.0
 */

public interface Draggable {
    public static final String IMAGE = "IMAGE";
    public static final String CIRCLE = "CIRCLE";
    public static final String LINE = "LINE";
    public static final String STATION = "STATION";
    public static final String TEXT = "TEXT";
    public MapState getStartingState();
    public void start(int x, int y);
    public void drag(int x, int y);
    public void size(int x, int y);
    public double getX();
    public double getY();
    public double getWidth();
    public double getHeight();
    public void setSize(double initWidth, double initHeight);
    public void setLocation(double initX, double initY);
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight);
    public String getShapeType();
}