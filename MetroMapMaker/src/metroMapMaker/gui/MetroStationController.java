package metroMapMaker.gui;

import djf.AppTemplate;
import djf.ui.AppAlertDialogSingleton;
import djf.ui.AppTextDialogSingleton;
import javafx.scene.Node;
import jtps.jTPS;
import metroMapMaker.data.DraggableCircle;
import metroMapMaker.data.DraggableStation;
import metroMapMaker.data.MapData;
import metroMapMaker.transactions.AddStationT;
import metroMapMaker.transactions.SnapT;
import metroMapMaker.transactions.StationRadiusT;

/**
 * All controls for row 2. Metro Station box.
 * 
 * @author fannydai
 */
public class MetroStationController {
    AppTemplate app;
    MapData dataManager;
    private final jTPS jtps;
    
    public MetroStationController(AppTemplate initApp) {
	app = initApp;
	dataManager = (MapData)app.getDataComponent();
        jtps = dataManager.getJTPS();
    }
    
    /**
     * Select the station in the combo box.
     * 
     * This combo box is only enabled when there is at least on metro station in the system.
     * 
     * If another node is already selected, unselect, unhighlight.
     * Disable all colorpicker except line and station.
     * 
     * Select this. Highlight this station.
     * Load combo box with the station color.
     */
    public void processStationSelect(){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        workspace.reloadWorkspace(dataManager);
    }
    
    /**
     * Adds station to list of stations.
     * Will not be added to any line.
     * Default color is white.
     * 
     * Enabled at all times. If this is selected, all nodes will be unselected,
     * highlighted. Program will be the the "doing nothing" state.
     */
    public void processAddStation(){
        AppTextDialogSingleton dialog = AppTextDialogSingleton.getSingleton();
        String stationName = dialog.showAdd("Add Metro", "Enter Station Name", "Enter Station Name Here").trim();
        
        if(dialog.getSelection().equals(AppTextDialogSingleton.CANCEL)){
            AppAlertDialogSingleton alertDialog = AppAlertDialogSingleton.getSingleton();
            alertDialog.show("No Station", "Station was not added to the canvas", "User either clicked cancel. No station added.");
            MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
            workspace.nothingSelected();
        }
        else if(stationName.equals("")){
            AppAlertDialogSingleton alertDialog = AppAlertDialogSingleton.getSingleton();
            alertDialog.show("No Station", "Station was not added to the canvas", "Name Empty. Not allowed.");
            MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
            workspace.nothingSelected();
        } else {
            stationName = dataManager.capitalizer(stationName);

            MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
            if(!workspace.hasStationName(stationName)){
                
                // Adds the new line to the canvas
                AddStationT trans = new AddStationT(app, stationName);
                jtps.addTransaction(trans);
                
                if(dataManager.selectedStation != null)
                    workspace.loadSelectedShapeSettings(dataManager.selectedStation);
            }
        }
    }
    
    /**
     * Snaps the station to the nearest line.
     * Enable only when a station is selected.
     */
    public void processSnap(){
        Node selectedNode = dataManager.getSelectedShape();

        if(selectedNode != null && selectedNode instanceof DraggableCircle) {
            DraggableCircle circle = (DraggableCircle)selectedNode;
            double xoffset = circle.getCenterX() % 20;
            double yoffset = circle.getCenterY() % 20;
            SnapT trans = new SnapT(circle, circle.getX() - xoffset, circle.getY() - yoffset);
            jtps.addTransaction(trans);

        } else if(selectedNode != null && selectedNode instanceof DraggableStation) {
            DraggableStation circle = (DraggableStation)selectedNode;
            double xoffset = circle.getCenterX() % 20;
            double yoffset = circle.getCenterY() % 20;
            SnapT trans = new SnapT(circle, circle.getX() - xoffset, circle.getY() - yoffset);
            jtps.addTransaction(trans);
        }
    }
    
    /**
     * Moves the label/station name to 4 designated locations.
     * Top, left, bottom or right of the station ellipse.
     * 
     * Enable only when a station is selected.
     */
    public void processMoveLabel(){
        dataManager.moveTextPosition();
    }
    
    /**
     * Rotates the label/station name.
     * 
     * Enable only when a station is selected.
     */
    public void processRotateLabel(){
        dataManager.rotateText();
    }
    
    /**
     * Changes the radius of the ellipse.
     * 
     * Enable only when a station is selected.
     */
    public void processCircleRadius(){
        DraggableStation station = dataManager.selectedStation;
        if(station != null){
            MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
            double radius = workspace.getCircleRadiuSlider().getValue();
            station.setRadius(radius);
        } else {
            MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
            workspace.nothingSelected();
        }
    }
    
    public void processCircleRadiusTrans(double prevRadius){
        if(dataManager.selectedStation == null){
            MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
            workspace.nothingSelected();
            return;
        }
        
        StationRadiusT trans = new StationRadiusT(dataManager.selectedStation, prevRadius);
        jtps.addTransaction(trans);
    }
}
