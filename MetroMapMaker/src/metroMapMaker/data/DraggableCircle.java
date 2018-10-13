package metroMapMaker.data;

import javafx.scene.shape.Circle;

/**
 * DraggableCircle. For dragging line ends.
 * @author fannydai
 */

public class DraggableCircle extends Circle implements Draggable {
    protected double startCenterX;
    protected double startCenterY;
    // The parent. Where it belongs
    protected final MetroLine metroLine;
    
    public DraggableCircle(MetroLine metroLine) {
	setRadius(10);
	setOpacity(1.0);
        setStrokeWidth(2);
        this.metroLine = metroLine;
    }
    
    /**
     * Will not be needing this since it is part of the line
     * @return 
     *  ADD_LINE since it is part of the line
     */
    @Override
    public MapState getStartingState() {
	return MapState.ADD_LINE;
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
//        startCenterX = initX;
//        startCenterY = initY;
//        setCenterX(initX + getRadius());
//	setCenterY(initY + getRadius());
        setCenterX(initX + (getWidth()/2));
	setCenterY(initY + (getHeight()/2));
    }
    
    @Override
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight) {
	startCenterX = initX;
        startCenterY = initY;
        setCenterX(initX + (initWidth/2));
	setCenterY(initY + (initHeight/2));
	setRadius(initWidth/2);
	setRadius(initHeight/2);
    }
    
    @Override
    public String getShapeType() {
	return CIRCLE;
    }
    
    public MetroLine getMetroLine(){
        return metroLine;
    }
    
    /**
     * The center of the circle (x-coordinate)
     * @param num 
     *  The center x-coordinate of the circle
     */
    public void setStartCenterX(double num){
        this.startCenterX = num;
    }
    
    /**
     * The center of the circle (y-coordinate)
     * @param num 
     *  The center y-coordinate of the circle
     */
    public void setStartCenterY(double num){
        this.startCenterY = num;
    }
}
