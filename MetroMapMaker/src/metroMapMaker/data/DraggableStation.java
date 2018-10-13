package metroMapMaker.data;

import java.util.LinkedList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * This is a draggable station for our metroMapMaker application.
 * 
 * @author Richard McKenna
 * @author Fanng Dai
 * @version 1.0
 */

public class DraggableStation extends Circle implements Draggable {
    protected double startCenterX;
    double startCenterY;
    
    private DraggableText name;
    
    // Is it assigned to a line already?
    private LinkedList<MetroLine> metroLine;
    
    protected Node leftObject;
    protected Node rightObject;
    
    // 0,1,2,3 where 0 is the top right, 1 is bottom right, 2 is bottom left, 3 is top left
    private int position;
    
    public DraggableStation(String name) {
        this.setFill(Color.WHITE);
        setStrokeWidth(2);
        setStroke(Color.BLACK);
	setCenterX(50.0);
	setCenterY(50.0);
	setRadius(10);
	setOpacity(1.0);
	startCenterX = 0.0;
	startCenterY = 0.0;
        
        this.position = 1;
        
        // The name of the station
        this.name = new DraggableText(name);
        this.name.setFill(Color.WHITE);
        this.name.setStroke(Color.BLACK);
        this.name.setStrokeWidth(0.25);
        this.name.setWrappingWidth(70);
        
        this.name.xProperty().bind(this.centerXProperty().add(this.radiusProperty()));
        this.name.yProperty().bind(this.centerYProperty().subtract(this.radiusProperty()));
    }
    
    public DraggableStation(String name, LinkedList<MetroLine> metroLine) {
        this.metroLine = metroLine;    
        
        // The name of the station
        this.name = new DraggableText(name);
        this.name.xProperty().bind(this.centerXProperty().add(this.radiusProperty()));
        this.name.yProperty().bind(this.centerYProperty().subtract(this.radiusProperty()));
        
        this.name.setFill(Color.WHITE);
        this.name.setStroke(Color.BLACK);
        this.name.setStrokeWidth(0.25);
        this.name.setWrappingWidth(70);
        
        this.position = 1;
        
        this.setFill(Color.WHITE);
	setCenterX(50.0);
	setCenterY(50.0);
	setRadius(10);
	setOpacity(1.0);
	startCenterX = 0.0;
	startCenterY = 0.0;
    }
    
    @Override
    public MapState getStartingState() {
	return MapState.ADD_STATION;
    }
    
    @Override
    public void start(int x, int y) {
	startCenterX = x;
	startCenterY = y;
    }
    
    @Override
    public void drag(int x, int y) {
	double diffX = x - startCenterX;
	double diffY = y - startCenterY;
	double newX = getCenterX() + diffX;
	double newY = getCenterY() + diffY;
	setCenterX(newX);
	setCenterY(newY);
	startCenterX = x;
	startCenterY = y;
        
//        this.name.xProperty().bindBidirectional(new SimpleDoubleProperty(this.getCenterX() + this.getRadius()));
//        this.name.yProperty().bindBidirectional(new SimpleDoubleProperty(this.getCenterY() - this.getRadius()));
    }
    
    @Override
    public void size(int x, int y) {
	double width = x - startCenterX;
	double height = y - startCenterY;
	double centerX = startCenterX + (width/2);
	double centerY = startCenterY + (height/2);
	setCenterX(centerX);
	setCenterY(centerY);
	setRadius(width/2);	
    }
        
    @Override
    public double getX() {
	return getCenterX() - getRadius();
    }

    @Override
    public double getY() {
	return getCenterY() - getRadius();
    }

    @Override
    public double getWidth() {
	return getRadius() * 2;
    }

    @Override
    public double getHeight() {
	return getRadius() * 2;
    }
    
    @Override
    public void setSize(double initWidth, double initHeight){
        setRadius(initWidth/2);
	setRadius(initHeight/2);
    }
    
    @Override   // MUST DECLARE setSize FIRST!!!
    public void setLocation(double initX, double initY){
        setCenterX(initX + getRadius());
	setCenterY(initY + getRadius());
    }
    
    @Override
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight) {
	setCenterX(initX + (initWidth/2));
	setCenterY(initY + (initHeight/2));
	setRadius(initWidth/2);
	setRadius(initHeight/2);
    }
    
    @Override
    public String getShapeType() {
	return STATION;
    }
    
    /**
     * Is this station already on the canvas?
     * @return 
     *  The line. null if there isn't any. That means it is not on the canvas.
     */
    public LinkedList<MetroLine> getMetroLines(){
        return this.metroLine;
    }
    
    /**
     * Set the name of the line
     * @param name 
     *  The name of the line
     */
    public void setName(String name){
        this.name.setText(name);
    }
    
    /**
     * Get the name of the line
     * @return
     *  The name of the line
     */
    public String getName(){
        return this.name.getText();
    }
    
    public DraggableText getLabel(){
        return this.name;
    }
        
    public int getPosition(){
        return this.position;
    }
    
    public void setPosition(int num){
        this.position = num;
    }
    
    public void setPostion(){
        this.position++;
        this.position %= 4;
        moveTextPosition();
    }
    
    public void movePositionBack(){
        this.position--;
        this.position %= 4;
        moveTextPosition();
    }
    
    public void rotateText(){
        if(name.getRotate() == 0){
            name.setRotate(90);
            moveTextPosition();
        }
        else if(name.getRotate() == 90){
            name.setRotate(-90);
            moveTextPosition();
        } else {
            name.setRotate(0);
            moveTextPosition();
        }
    }
    
    public void moveRotateBack(){
        if(name.getRotate() == -90){
            name.setRotate(90);
            moveTextPosition();
        }
        else if(name.getRotate() == 90){
            name.setRotate(0);
            moveTextPosition();
        } else {
            name.setRotate(-90);
            moveTextPosition();
        }
    }
    
    public void rotateText(int num){
        name.setRotate(num);
    }
    
    public void moveTextPosition(){
        if(position == 3 || position == -1) {                  // bottom left
            this.name.xProperty().bind(this.centerXProperty().add(this.radiusProperty()).subtract(this.name.getLayoutBounds().getWidth()));
            this.name.yProperty().bind(this.centerYProperty().add(this.radiusProperty()));
        } else if(position == 2 || position == -2) {           // bottom right
            this.name.xProperty().bind(this.centerXProperty().add(this.radiusProperty()));
            this.name.yProperty().bind(this.centerYProperty().add(this.radiusProperty()));
            
            if(this.name.getRotate() != 0){
                this.name.xProperty().bind(this.centerXProperty().subtract(this.name.getLayoutBounds().getHeight()));
            }
        } else if(position == 1 || position == -3) {           // top right
            this.name.xProperty().bind(this.centerXProperty().add(this.radiusProperty()));
            this.name.yProperty().bind(this.centerYProperty().subtract(this.radiusProperty()));
            
            if(this.name.getRotate() != 0){
                this.name.xProperty().bind(this.centerXProperty().subtract(this.name.getLayoutBounds().getHeight()));
            }
        } else {                            // top left
            this.name.xProperty().bind(this.centerXProperty().add(this.radiusProperty()).subtract(this.name.getLayoutBounds().getWidth()));
            this.name.yProperty().bind(this.centerYProperty().subtract(this.radiusProperty()));
        }
    }
}
