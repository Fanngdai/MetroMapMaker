package metroMapMaker.transactions;

import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableStation;

/**
 * Move the station label to left/right/top/bottom
 * 
 * @author fannydai
 */
public class MoveStationLabelT implements jTPS_Transaction {
    private final DraggableStation station;
        
     public MoveStationLabelT(DraggableStation station){
         this.station = station;
    }

    @Override
    public void doTransaction() {
        station.setPostion();
    }

    @Override
    public void undoTransaction() {
        station.movePositionBack();
    }
}
