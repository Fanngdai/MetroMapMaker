package metroMapMaker.data;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import static metroMapMaker.data.Draggable.IMAGE;

/**
 * This is a draggable image for our metroMapMaker application.
 * 
 * @author Richard McKenna
 * @author Fanng Dai
 * @version 1.0
 */
public class DraggableImage extends Rectangle implements Draggable {
    private double startX;
    private double startY;
    private static double initX;
    private static double initY;
    
    private String imagePath = "";
    
    public DraggableImage() {
	((Node)this).setOpacity(1.0);
        this.setArcHeight(25);
        this.setArcWidth(25);
    }
    
    @Override
    public MapState getStartingState() {
	return MapState.ADD_IMAGE;
    }
    
    @Override
    public void start(int x, int y) {
	initX = getX();
        initY = getY();
	startX = x;
	startY = y;
        setX(x);
        setY(y);
    }
    
    @Override
    public void drag(int x, int y) {
	 double newX = initX + x - startX;
        double newY = initY + y - startY;
        startX = x;
        startY = y;
        initX = newX;
        initY = newY;
        xProperty().set(newX);
        yProperty().set(newY);
    }
    
//    public String cT(double x, double y) {
//	return "(x,y): (" + x + "," + y + ")";
//    }
    
    @Override
    public void size(int x, int y) {
	double width = x - getX();
	widthProperty().set(width);
	double height = y - getY();
	heightProperty().set(height);	
    }
    
    @Override
    public String getShapeType() {
	return IMAGE;
    }

    @Override
    public void setLocation(double initX, double initY) {
	xProperty().set(initX);
	yProperty().set(initY);
    }
    
    @Override
    public void setSize(double initWidth, double initHeight){
        widthProperty().set(initWidth);
	heightProperty().set(initHeight);
    }

    @Override
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight) {
	xProperty().set(initX);
	yProperty().set(initY);
	widthProperty().set(initWidth);
	heightProperty().set(initHeight);
    }
    
    public void setImagePath(String image){
        this.imagePath = image;
    }
    
    public String getImagePath(){
        return this.imagePath;
    }
}
