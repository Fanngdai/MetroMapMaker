package metroMapMaker.transactions;

import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableStation;
import metroMapMaker.data.MetroLine;

/**
 *
 * @author fannydai
 */
public class RemoveStationFromLineT implements jTPS_Transaction{
    private final MetroLine line;
    private final DraggableStation station;
    private final int indexList;
    private final int indexStation;
    
    public RemoveStationFromLineT(MetroLine line, DraggableStation station){
        this.line = line;
        this.station = station;

        line.removeAllLine();
        this.indexList = line.indexOf(station);
        this.indexStation = line.getStations().indexOf(station.getLabel());
        line.connectAll();
    }
    
    @Override
    public void doTransaction() {
       line.remove(station);
       line.getStations().remove(station.getLabel());
       if(line.getCircular() && line.getStations().size() == 2)
            line.makeNotCircular();
        line.connectAll();
    }

    @Override
    public void undoTransaction() {
        line.removeAllLine();
        line.add(indexList, station);
        line.getStations().add(indexStation, station.getLabel());
        line.connectAll();
    }
    
}
