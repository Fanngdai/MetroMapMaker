package metroMapMaker.transactions;

import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableStation;

/**
 *
 * @author fannydai
 */
public class RotateStationLabelT implements jTPS_Transaction{
    private final DraggableStation station;
    
    public RotateStationLabelT(DraggableStation station){
        this.station = station;
    }

    @Override
    public void doTransaction() {
        this.station.rotateText();
    }

    @Override
    public void undoTransaction() {
        this.station.moveRotateBack();
    }
}
