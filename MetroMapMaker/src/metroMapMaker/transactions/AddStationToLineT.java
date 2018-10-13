package metroMapMaker.transactions;

import djf.AppTemplate;
import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableStation;
import metroMapMaker.data.MapData;
import metroMapMaker.data.MetroLine;

/**
 *
 * @author fannydai
 */
public class AddStationToLineT implements jTPS_Transaction {
    private final MapData dataManager;
    private final MetroLine line;
    private final DraggableStation station;
    
    public AddStationToLineT(AppTemplate app, MetroLine line, DraggableStation station){
        this.dataManager = (MapData) app.getDataComponent();
        this.line = line;
        this.station = station;
    }

    @Override
    public void doTransaction() {
        if(!line.contains(station))
            line.addStation(station);
    }

    @Override
    public void undoTransaction() {
        dataManager.removeStationFromLine(line, station);
    }
}
