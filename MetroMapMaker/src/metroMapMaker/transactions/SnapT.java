package metroMapMaker.transactions;

import javafx.scene.shape.Circle;
import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableCircle;
import metroMapMaker.data.DraggableStation;
import metroMapMaker.data.DraggableText;

/**
 * When a station radius changes.
 * 
 * @author fannydai
 */
public class SnapT implements jTPS_Transaction{
    private final Circle circle;
    private final double locationX;
    private final double locationY;
    private final double prevLocationX;
    private final double prevLocationY;
    private final DraggableText text;
    
    public SnapT(Circle circle, double locationX, double locationY){
        this.circle = circle;
        this.locationX = locationX;
        this.locationY = locationY;
        this.prevLocationX = circle.getCenterX() - circle.getRadius();
        this.prevLocationY = circle.getCenterY() - circle.getRadius();
        
        if(circle instanceof DraggableCircle && ((DraggableCircle)circle).getMetroLine().contains(((DraggableCircle)circle).getMetroLine().getName1())){
            this.text = ((DraggableCircle)circle).getMetroLine().getName2();
        } else if(circle instanceof DraggableCircle) {
            this.text = ((DraggableCircle)circle).getMetroLine().getName1();
        } else {
            text = null;
        }
    }
    
    @Override
    public void doTransaction(){
        if(circle instanceof DraggableCircle){
            ((DraggableCircle)circle).setLocation(locationX, locationY);
            text.setLocation(locationX, locationY);
        } else {
            ((DraggableStation)circle).setLocation(locationX, locationY);
        }
    }
    
    @Override 
    public void undoTransaction(){
        if(circle instanceof DraggableCircle){
            ((DraggableCircle)circle).setLocation(prevLocationX, prevLocationY);
            text.setLocation(prevLocationX, prevLocationY);
        } else {
            ((DraggableStation)circle).setLocation(prevLocationX, prevLocationY);
        }
    }
}
