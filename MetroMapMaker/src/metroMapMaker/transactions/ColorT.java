package metroMapMaker.transactions;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import jtps.jTPS_Transaction;

/**
 *
 * @author fannydai
 */
public class ColorT implements jTPS_Transaction{
    private final Node shape;
    private final Color previousColor;
    private final Color color;
    
    public ColorT(Node shape, Color color){        
        this.shape = shape;
        this.previousColor = (Color) ((Shape)shape).getFill();
        this.color = color;
    }

    @Override
    public void doTransaction(){
        ((Shape)shape).setFill(color);
    }
    
    @Override 
    public void undoTransaction(){
         ((Shape)shape).setFill(previousColor);
    }
}
