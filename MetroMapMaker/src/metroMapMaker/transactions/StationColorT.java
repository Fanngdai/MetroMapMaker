package metroMapMaker.transactions;

import javafx.scene.paint.Color;
import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableStation;

/**
 *
 * @author fannydai
 */

public class StationColorT implements jTPS_Transaction{
    private final DraggableStation station;
    private final Color color;
    private final Color prevColor;
    
    public StationColorT(DraggableStation station, Color color){        
        this.station = station;
        this.prevColor = (Color) station.getFill();
        this.color = color;
    }
    
    @Override
    public void doTransaction(){
        this.station.setFill(color);
    }
    
    @Override 
    public void undoTransaction(){
        this.station.setFill(prevColor);
    }
}
