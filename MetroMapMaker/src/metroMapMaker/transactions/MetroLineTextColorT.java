package metroMapMaker.transactions;

import javafx.scene.paint.Color;
import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableText;
import metroMapMaker.data.MetroLine;

/**
 *
 * @author fannydai
 */
public class MetroLineTextColorT implements jTPS_Transaction{
    // current values
    private final DraggableText name1;
    private final DraggableText name2;
    private final Color color;
    private final Color prevColor;
    
    public MetroLineTextColorT(MetroLine line, Color color) {
        this.name1 = line.getName1();
        this.name2 = line.getName2();
        this.color = color;
        this.prevColor = (Color) name1.getFill();
    }

    @Override
    public void doTransaction(){        
        name1.setFill(color);
        name2.setFill(color);
    }
    
    @Override 
    public void undoTransaction(){
        name1.setFill(prevColor);
        name2.setFill(prevColor);
    }
}
