package metroMapMaker.transactions;

import djf.AppTemplate;
import javafx.scene.paint.Color;
import jtps.jTPS_Transaction;
import metroMapMaker.data.MapData;
import static metroMapMaker.data.MapData.shapes;
import metroMapMaker.data.MetroLine;
import metroMapMaker.gui.MapWorkspace;

/**
 * For when we add line to the canvas. 
 * Since it has 4 coordinates, we need another transaction for it.
 * 
 * @author fannydai
 */
public class AddLineT implements jTPS_Transaction{
    private final MapWorkspace workspace;
    private final MapData dataManager;
    
    private final MetroLine line;
    private final String lineName;
    
    public AddLineT(AppTemplate app, String lineName, Color lineColor){
        this.workspace = (MapWorkspace) app.getWorkspaceComponent();
        this.dataManager = (MapData) app.getDataComponent();

        this.line = new MetroLine(lineName, lineColor);
        this.lineName = lineName;
    }
    
    @Override
    public void doTransaction(){
        workspace.addLineToCombo(lineName);
        // Adds the new line to the canvas
        dataManager.getLines().add(line);
        line.forEach((n) -> {
            shapes.add(n);
        });
        
        line.connectAll();
        
        dataManager.selectedMetroLine = line;
        dataManager.setSelectedShape(line.get(0));
        dataManager.unhighlightShape();
    }
    
    @Override
    public void undoTransaction(){
        workspace.removeLineFromCombo(lineName);
        // Because the other one has an transaction in it already...
        dataManager.removeLineTransaction(lineName);
//        dataManager.getJTPS().undoTransaction();
    }
}
