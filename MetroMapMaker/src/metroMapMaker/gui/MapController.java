package metroMapMaker.gui;

import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;
import metroMapMaker.data.MapData;
import djf.AppTemplate;
import djf.ui.AppAlertDialogSingleton;
import jtps.jTPS;
import metroMapMaker.data.MapState;

/**
 * This class responds to interactions with other UI logo editing controls.
 * 
 * @author Fanng Dai
 * @version 1.0
 */
public class MapController {
    AppTemplate app;
    MapData dataManager;
    private jTPS jtps;
    
    public MapController(AppTemplate initApp) {
	app = initApp;
	dataManager = (MapData)app.getDataComponent();
        jtps = dataManager.getJTPS();
    }
    
    /**
     * This method processes a user request to take a snapshot of the
     * current scene.
     * @throws java.io.IOException
     */
    public void processExport() throws IOException {
        // Makes the file under that file name. Override if already exist
        app.getGUI().getFileController().createExport();
        // Puts the json file into this folder
        String filePath = app.getGUI().getFileController().processExportJSON();
        
        
        // below is snapshot
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
	Pane canvas = workspace.getCanvas();
        
        // Make sure you don't capture the grid
        ScrolllPane scrollPane = workspace.getScrollPane();
        boolean gridEnabled = scrollPane.gridEnabled();
        if(gridEnabled)
            scrollPane.disableGrid();
            
	WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
	File file = new File(filePath + " Metro.png");
	try {
	    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	}
	catch(IOException ioe) {
//	    ioe.printStackTrace();
	} finally{
            if(gridEnabled)
                scrollPane.enableGrid();
        }
        
        AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
        dialog.show("Export Success", "Export Success",
                "Exported files can be found in the exports folder!");
    }
    
    public void handleDoTransaction(){
        jtps.doTransaction();  
    }
    public void handleUndoTransaction(){
        dataManager.setState(MapState.FUCKING_SHIT);
        jtps.undoTransaction();
        dataManager.setState(MapState.NOTHING);
    }
}
