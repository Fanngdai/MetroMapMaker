package metroMapMaker.transactions;

import djf.AppTemplate;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableStation;
import metroMapMaker.data.MapData;
import metroMapMaker.data.MetroLine;
import metroMapMaker.gui.MapWorkspace;

/**
 *
 * @author fannydai
 */
public class RemoveStationT implements jTPS_Transaction{
    private final MapWorkspace workspace;
    private final MapData dataManager;
    private final DraggableStation station;
    private final Color color;
    // The line, position, and station position
    private final ArrayList<MetroLineTracker> lines;
    
    public RemoveStationT(AppTemplate app, DraggableStation station){
        this.workspace = (MapWorkspace) app.getWorkspaceComponent();
        this.dataManager = (MapData) app.getDataComponent();
        this.station = station;
        this.color = (Color) station.getFill();
        this.lines = new ArrayList<>();
        
        // Get the lines that this station is part of
        for(MetroLine l: dataManager.getLines()){
            if(l.contains(station)){
                l.removeAllLine();
                lines.add(new MetroLineTracker(l, l.indexOf(station), l.getStations().indexOf(station.getLabel())));
                l.connectAll();
            }
        }
    }
    
    @Override
    public void doTransaction() {
        // Remove station from canvas
        for(MetroLine line: dataManager.getLines()){
            if(line.contains(station)){
                line.remove(station);
                line.getStations().remove(station.getLabel());
                
                if(line.getCircular() && line.getStations().size() == 2)
                    line.makeNotCircular();
                            
                line.connectAll();
            }
        }

        dataManager.getStations().remove(station);
        
        dataManager.getShapes().remove(station);
        dataManager.getShapes().remove(station.getLabel());
        
        workspace.removeStationFromCombo(station.getName());
        workspace.setRouteComboBox();
    }

    @Override
    public void undoTransaction() {
        // Add station back to canvas
        dataManager.getStations().add(station);
        station.setFill(color);
        dataManager.getShapes().add(station);
        dataManager.getShapes().add(station.getLabel());

        for(MetroLineTracker line: lines){
            MetroLine l = line.getMetroLine();
            l.removeAllLine();
            l.add(line.getPositionLine(), station);
            l.getStations().add(line.getPositionStation(), station.getLabel());
            l.connectAll();
        }
        
        workspace.addStationToCombo(station.getName());
        workspace.setRouteComboBox();
    }   
}