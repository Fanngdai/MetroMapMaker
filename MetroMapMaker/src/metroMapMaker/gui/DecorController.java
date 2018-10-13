package metroMapMaker.gui;

import djf.AppTemplate;
import static djf.settings.AppPropertyType.LOAD_ERROR_MESSAGE;
import static djf.settings.AppPropertyType.LOAD_ERROR_TITLE;
import static djf.settings.AppPropertyType.LOAD_WORK_TITLE;
import static djf.settings.AppStartupConstants.PATH_WORK;
import djf.ui.AppAlertDialogSingleton;
import djf.ui.AppConfirmDialogSingleton;
import djf.ui.AppTextDialogSingleton;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import jtps.jTPS;
import metroMapMaker.data.Draggable;
import metroMapMaker.data.DraggableImage;
import metroMapMaker.data.DraggableText;
import metroMapMaker.data.MapData;
import metroMapMaker.data.MapState;
import metroMapMaker.transactions.BackgroundT;
import metroMapMaker.transactions.RemoveT;
import properties_manager.PropertiesManager;

/**
 * All controls for row 4. Decor box.
 * 
 * @author fannydai
 */
public class DecorController {
    AppTemplate app;
    MapData dataManager;
    private final jTPS jtps;
    
    public DecorController(AppTemplate initApp) {
	app = initApp;
	dataManager = (MapData)app.getDataComponent();
        jtps = dataManager.getJTPS();
    }
    
    /**
     * Changes the color of the background.
     * 
     * Always enabled.
     */
    public void processBackgroundColor(){
        dataManager.unhighlightShape();
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
	Color selectedColor = workspace.getBackgroundColor();
	if (selectedColor != null) {
            
            if(dataManager.getBackgroundColor() != null){                   // Color to color
                BackgroundT trans = new BackgroundT(app, dataManager.getBackgroundColor(), selectedColor);
                jtps.addTransaction(trans);
            } else if(dataManager.getBackgroundImagePath() != null){        // Image to color
                BackgroundT trans = new BackgroundT(app, dataManager.getBackgroundImagePath(), selectedColor);
                jtps.addTransaction(trans);
            }
	}
    }
    
    /**
     * Sets the background to an image
     */
    public void processImageBackground(){
        dataManager.unhighlightShape();
        AppConfirmDialogSingleton dialog = AppConfirmDialogSingleton.getSingleton();
        dialog.show("Background Image","Are you sure you want to add a background image?");
        
        // User does not want an background image
        if(dialog.getSelection().equals(AppConfirmDialogSingleton.NO)){
            return;
        }
        
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        // AND NOW ASK THE USER FOR THE FILE TO OPEN       
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(PATH_WORK));
	fc.setTitle(props.getProperty(LOAD_WORK_TITLE));

        //Show open file dialog
        File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());

        try {
            if (selectedFile != null) {
                // Get the path for the image
                String imagePath = selectedFile.getCanonicalPath();
                
                if(dataManager.getBackgroundColor() != null){                   // Color to image
                    BackgroundT trans = new BackgroundT(app, dataManager.getBackgroundColor(), imagePath);
                    jtps.addTransaction(trans);
                } else if(dataManager.getBackgroundImagePath() != null){        // Image to image
                    BackgroundT trans = new BackgroundT(app, dataManager.getBackgroundImagePath(), imagePath);
                    jtps.addTransaction(trans);
                }
            } else {
                MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
                workspace.nothingSelected();
            }
        } catch (IOException ex) {
            AppAlertDialogSingleton dialogA = AppAlertDialogSingleton.getSingleton();
            dialogA.show(props.getProperty(LOAD_ERROR_TITLE),props.getProperty(LOAD_ERROR_TITLE),
                    props.getProperty(LOAD_ERROR_MESSAGE));
        } catch (NullPointerException ex){
            AppAlertDialogSingleton dialogA = AppAlertDialogSingleton.getSingleton();
            dialogA.show("User failed to pick image","User failed to pick image",
                    "That wasn't an image was it? Hmmm nice try");
        }
    }
    
    /**
     * Adds an image to the canvas.
     * 
     * Same as the rectangle. Can be sized.
     */
    public void processAddImage(){
        dataManager.unhighlightShape();
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        // AND NOW ASK THE USER FOR THE FILE TO OPEN       
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(PATH_WORK));
	fc.setTitle(props.getProperty(LOAD_WORK_TITLE));

        //Show open file dialog
        File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());

        try {
            if (selectedFile != null) {
                dataManager.setState(MapState.ADD_IMAGE);
                BufferedImage bufferedImage = ImageIO.read(selectedFile);
                // Get the path for the image
                String imagePath = selectedFile.getCanonicalPath();
                dataManager.setImage(bufferedImage, imagePath);
                // CHANGE THE STATE

                
                Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.CROSSHAIR);
            } else {
                // Reset if user clicked cancel
                workspace.nothingSelected();
            }
        } catch (IOException ex) {
            dataManager.setState(MapState.NOTHING);
            AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
            dialog.show(props.getProperty(LOAD_ERROR_TITLE),props.getProperty(LOAD_ERROR_TITLE),
                    props.getProperty(LOAD_ERROR_MESSAGE));
            workspace.nothingSelected();
        } catch (NullPointerException ex){
            dataManager.setState(MapState.NOTHING);
            AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
            dialog.show("User failed to pick image","User failed to pick image",
                    "That wasn't an image was it? Hmmm nice try");
            workspace.nothingSelected();
        } finally{
            // ENABLE/DISABLE THE PROPER BUTTONS
//            workspace.reloadWorkspace(dataManager);
        }
    }
    
    /**
     * Adds label to the canvas.
     */
    public void processAddLabel(){
        dataManager.unhighlightShape();
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        AppTextDialogSingleton textDialog = AppTextDialogSingleton.getSingleton();
        String labelName = textDialog.showAdd("Add Label", "Please Enter Label Name", "Label Name").trim();
        if((textDialog.getSelection() == null ? AppTextDialogSingleton.CANCEL != null : !textDialog.getSelection().equals(AppTextDialogSingleton.CANCEL)) && labelName != null && !labelName.equals("")){ 
            dataManager.addText(labelName);
            if(dataManager.getSelectedShape() != null)
                workspace.loadSelectedShapeSettings(dataManager.getSelectedShape());
        }  else {
            // Reset if user clicked cancel
            workspace.nothingSelected();
        }
    }
    
    /**
     * Removes image and label
     */
    public void processRemoveElement(){
        Draggable element;
        if(dataManager.getSelectedShape() != null){
            element = (Draggable) dataManager.getSelectedShape();
            if(element instanceof DraggableImage || element instanceof DraggableText){
                RemoveT trans = new RemoveT(app, (Node) element);
                jtps.addTransaction(trans);
            }
        }
        
        // Reset if user clicked cancel
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        workspace.nothingSelected();
    }
}
