package metroMapMaker.transactions;

import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableStation;

/**
 * When a station radius changes.
 * 
 * @author fannydai
 */
public class StationRadiusT implements jTPS_Transaction{
    private final DraggableStation station;
    private final double radius;
    private final double prevRadius;
    
    public StationRadiusT(DraggableStation station, double prevRadius){
        this.station = station;
        this.prevRadius = prevRadius;
        this.radius = station.getRadius();
    }
    
    @Override
    public void doTransaction(){
        this.station.setRadius(radius);
    }
    
    @Override 
    public void undoTransaction(){
        this.station.setRadius(prevRadius);
    }
}
