package metroMapMaker.transactions;

import javafx.scene.Node;
import jtps.jTPS_Transaction;
import metroMapMaker.data.Draggable;

/**
 * For dragging an element
 * 
 * @author fannydai
 */
public class DragT implements jTPS_Transaction{
    private final Node shape;
    private final double initX;
    private final double initY;
    private final double endX;
    private final double endY;
    
    
    public DragT(Node shape, double initX, double initY){
        this.shape = shape;
        this.initX = initX;
        this.initY = initY;
        this.endX = ((Draggable)shape).getX();
        this.endY = ((Draggable)shape).getY();
    }
    
    @Override
    public void doTransaction(){
        ((Draggable)shape).setLocation(endX,endY);
    }
    
    @Override 
    public void undoTransaction(){
        ((Draggable)shape).setLocation(initX,initY);
    }
}
