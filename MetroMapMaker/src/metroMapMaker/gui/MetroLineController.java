package metroMapMaker.gui;

import djf.AppTemplate;
import djf.ui.AppAlertDialogSingleton;
import djf.ui.AppConfirmDialogSingleton;
import djf.ui.AppMetroLineDialogSingleton;
import javafx.scene.paint.Color;
import jtps.jTPS;
import metroMapMaker.data.MapData;
import metroMapMaker.data.MapState;
import metroMapMaker.data.MetroLine;
import metroMapMaker.transactions.AddLineT;
import metroMapMaker.transactions.LineEditT;
import metroMapMaker.transactions.LineThicknessT;

/**
 * All controls for row 1. Metro Line box.
 * 
 * @author fannydai
 */
public class MetroLineController {
    private final AppTemplate app;
    private final MapData dataManager;
    private final jTPS jtps;
    
    public MetroLineController(AppTemplate initApp) {
	app = initApp;
	dataManager = (MapData)app.getDataComponent();
        jtps = dataManager.getJTPS();
    }
    
    /**
     * Edit the name and color of the line.
     * 
     * This button is only enabled when a line is selected.
     * 
     * By clicking this button, a dialog will pop up with the loaded name
     * and color of the line. User can choose to change the values
     * or click cancel.
     * 
     * If user clicks "ok" but the values are the same, this process is not
     * stored in the undo redo stack.
     * 
     * @return 
     *  The name the line changed to
     */
    public String processEditLine(){
        // Ask user for name and color
        AppMetroLineDialogSingleton lineDialog = AppMetroLineDialogSingleton.getSingleton();
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        MetroLine metroLine = null;

        metroLine = dataManager.findMetroLine(workspace.getMetroNameSelected());
        
        try{
        String lineName = lineDialog.showEditLine(metroLine.getName(), metroLine.getColor(), metroLine.getCircular()).trim();
        if(lineDialog.getSelection().equals(AppMetroLineDialogSingleton.CANCEL)){
            AppAlertDialogSingleton alertDialog = AppAlertDialogSingleton.getSingleton();
            alertDialog.show("No line", "Line was not added to the canvas", "User clicked cancel.");
            return null;
        }
        else if(lineName == null || lineName.equals("")){
            AppAlertDialogSingleton alertDialog = AppAlertDialogSingleton.getSingleton();
            alertDialog.show("No line", "Line was not added to the canvas", "User did not enter name.");
            return null;
        } else {
            lineName = dataManager.capitalizer(lineName);

            // User did not click cancel nor is the lineName empty. Name does not already exist to another metro line
            if(lineName.equalsIgnoreCase(metroLine.getName()) || !workspace.hasLineName(lineName)){
                // Not the same name && color && circular value
                if(!(lineName.equals(metroLine.getName()) && lineDialog.getColor().equals(metroLine.getColor()) && lineDialog.getCircularChecked()==metroLine.getCircular())){
                    LineEditT trans = new LineEditT(app, metroLine, metroLine.getName(), metroLine.getColor(), metroLine.getCircular(),
                            lineName, lineDialog.getColor(), lineDialog.getCircularChecked());
                    jtps.addTransaction(trans);
                    // Reload the workspace (buttons enable and disable)
                    workspace.loadSelectedShapeSettings(dataManager.getSelectedShape());
                    return lineName;
                }
            }
        }
        }catch(NullPointerException ex){
            workspace.nothingSelected();
        }
        
        return null;
    }
    
    /**
     * Adds a line to the canvas and to the list.
     * Use case 2.12 Add Line
     * 
     * Open a dialog box that prompts the user for the name and color of the line.
     * If ok is selected a line with the name of the line on two ends is added to the map without
     * any stops. The line will be in the prescribed color. The line is then selected in the Metro
     * Lines combo box and its color is loaded as the background color in the Line Edit button.
     */
    public void processAddLine(){
        dataManager.setState(MapState.ADD_LINE);
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        AppMetroLineDialogSingleton dialog = AppMetroLineDialogSingleton.getSingleton();
        String lineName = dialog.showAddLine().trim();
        
        if(dialog.getSelection().equals(AppMetroLineDialogSingleton.CANCEL)){
            AppAlertDialogSingleton alertDialog = AppAlertDialogSingleton.getSingleton();
            alertDialog.show("No line", "Line was not added to the canvas", "User clicked cancel.");
            return;
        } else if(lineName == null || lineName.equals("")) {
            AppAlertDialogSingleton alertDialog = AppAlertDialogSingleton.getSingleton();
            alertDialog.show("No line", "Line was not added to the canvas", "User did not enter name.");
            return;
        }        
        
        lineName = dataManager.capitalizer(lineName);
        
        if(!dialog.getSelection().equals(AppMetroLineDialogSingleton.CANCEL) && !workspace.hasLineName(lineName)){
            Color lineColor = dialog.getColor();
            
            AddLineT trans = new AddLineT(app, lineName, lineColor);
            jtps.addTransaction(trans);
            
            // Enable/disable proper buttons
            workspace.loadSelectedShapeSettings(dataManager.getSelectedShape());
            dataManager.setState(MapState.DRAGGING_SHAPE);
        } else {
            workspace.nothingSelected();
        }
    }
    
    /**
     * Removes a Line from the map
     * @return 
     */
    public boolean processRemoveLine(){
        AppConfirmDialogSingleton dialog = AppConfirmDialogSingleton.getSingleton();
        dialog.show("Remove Line", "Are you sure you want to delete this line?");
        
        if(dialog.getSelection().equals(AppConfirmDialogSingleton.YES)){
            // Remove line
            return true;
        } else {
            MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
            workspace.nothingSelected();
        }
        return false;
    }
    
    /**
     * Add station to line
     */
    public void processAddStation(){
        dataManager.setState(MapState.ADD_STATION);
        // Enable/disable proper buttons
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        workspace.reloadWorkspace(dataManager);
    }
    
    public void processRemoveStation(){
        // Change state
        dataManager.setState(MapState.REMOVE_STATION);

        // Enable/disable proper buttons
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        workspace.reloadWorkspace(dataManager);
    }
    
    public void processListStation(){
        AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
        StringBuilder name = new StringBuilder();
        if(dataManager.selectedMetroLine != null){
            for(int i=0; i<dataManager.selectedMetroLine.getStations().size(); i++){
                name.append("\t").append(dataManager.selectedMetroLine.getStations().get(i).getText());
                name.append("\n\n");
            }
        }
        if(name.toString().trim().equals("")){
            dialog.show("No stations", "Line has no station", "This line has no stations!");
        } else{
            dialog.show("Showing Stations", dataManager.selectedMetroLine.getName()+ " Line Stops", name.toString());
        }
    }
    
    /**
     * Change the value as the user slides
     */
    public void processLineThickness(){
        MetroLine metroLine = dataManager.selectedMetroLine;
        if(metroLine != null){
            MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
            double thickness = workspace.getLineThicknessSlider().getValue();
            metroLine.setLineThickness(thickness);
        }
    }
    
    /**
     * Transaction
     */
    public void processLineThicknessTrans(int prevThickness){
        LineThicknessT trans = new LineThicknessT(dataManager.selectedMetroLine, prevThickness);
        jtps.addTransaction(trans);
    }
}
