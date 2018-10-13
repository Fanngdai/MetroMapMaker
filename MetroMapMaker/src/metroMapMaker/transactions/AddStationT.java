package metroMapMaker.transactions;

import djf.AppTemplate;
import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableStation;
import metroMapMaker.data.MapData;
import static metroMapMaker.data.MapData.shapes;
import static metroMapMaker.data.MapData.stations;
import metroMapMaker.gui.MapWorkspace;

/**
 *
 * @author fannydai
 */
public class AddStationT implements jTPS_Transaction{
    private final MapWorkspace workspace;
    private final MapData dataManager;
    
    private final DraggableStation station;
    private final String name;
    
    public AddStationT(AppTemplate app, String name){
        this.workspace = (MapWorkspace) app.getWorkspaceComponent();
        this.dataManager = (MapData) app.getDataComponent();
        
        this.station = new DraggableStation(name);
        this.name = name;
    }

    @Override
    public void doTransaction() {
        workspace.addStationToCombo(name);
        // Adds the new line to the canvas
        
        stations.add(station);
        shapes.add(station);
        shapes.add(station.getLabel());
        
        dataManager.highlightShape(station);
        dataManager.highlightShape(station.getLabel());
        dataManager.selectedStation = station;
//        dataManager.addStation(name);
        workspace.setRouteComboBox();
    }

    @Override
    public void undoTransaction() {
        workspace.removeStationFromCombo(name);
        // Adds the new line to the canvas
        dataManager.removeStationForever(name);
        workspace.setRouteComboBox();
    }
}
