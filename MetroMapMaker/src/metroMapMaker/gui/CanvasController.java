package metroMapMaker.gui;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import metroMapMaker.data.MapData;
import metroMapMaker.data.Draggable;
import djf.AppTemplate;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import jtps.jTPS;
import metroMapMaker.data.DraggableCircle;
import metroMapMaker.data.DraggableImage;
import metroMapMaker.data.DraggableStation;
import metroMapMaker.data.DraggableText;
import metroMapMaker.data.MapState;
import metroMapMaker.transactions.DragT;

/**
 * This class responds to interactions with the rendering surface.
 *
 * @author Richard McKenna
 * @author Fanng Dai
 * @version 1.0
 */
public class CanvasController {
    private final AppTemplate app;
    private final MapData dataManager;
    private final jTPS jtps;
    private double initX;
    private double initY;
    private Node metroText;

    public CanvasController(AppTemplate initApp) {
        app = initApp;
        this.dataManager = (MapData) app.getDataComponent();
        jtps = dataManager.getJTPS();
    }

    /**
     * Respond to mouse presses on the rendering surface, which we call canvas,
     * but is actually a Pane.
     * @param x
     *  The x coordinate on the canvas
     * @param y
     *  The y coordinate on the canvas
     */
    public void processCanvasMousePress(int x, int y) {
        MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
        Scene scene = app.getGUI().getPrimaryScene();
        
        Node tempMetroText = dataManager.getTopShape(x, y);
        if(tempMetroText != metroText && !(tempMetroText instanceof DraggableCircle) && !dataManager.isInState(MapState.ADD_STATION) 
                && !dataManager.isInState(MapState.REMOVE_STATION) && !dataManager.isInState(MapState.ADD_IMAGE))
            workspace.nothingSelected();
        
        if(!(tempMetroText instanceof DraggableCircle))
            this.metroText = tempMetroText;
        
        // SELECT THE TOP SHAPE
        Object shape = dataManager.selectTopShape(x, y);
        // Make the text a circle...
        dataManager.selectTopShape(x, y);
            
        // This to drag
        if (dataManager.isInState(MapState.SELECTING_SHAPE)) {
            // Cannot drag line
            if(shape != null && shape instanceof Line){
                dataManager.highlightShape();
                dataManager.setState(MapState.NOTHING);
                app.getWorkspaceComponent().reloadWorkspace(dataManager);
            }
            else if (shape != null && shape instanceof Draggable) {
                scene.setCursor(Cursor.MOVE);
                dataManager.setState(MapState.DRAGGING_SHAPE);
                Draggable selectedDraggableShape = (Draggable)shape;
                
                initX = selectedDraggableShape.getX();
                initY = selectedDraggableShape.getY();
                
                selectedDraggableShape.start(x, y);
                selectedDraggableShape.drag(x, y);
            } else {
                scene.setCursor(Cursor.DEFAULT);
                dataManager.setState(MapState.NOTHING);
                dataManager.unhighlightShape();
                app.getWorkspaceComponent().reloadWorkspace(dataManager);
            }
        } else if(dataManager.isInState(MapState.ADD_STATION)){
            if(shape == null || !(shape instanceof Circle)){
                dataManager.setState(MapState.NOTHING);
                dataManager.unhighlightShape();
                workspace.nothingSelected();
            } else {
                dataManager.addStationToLine(x, y);
            }
        } else if(dataManager.isInState(MapState.REMOVE_STATION)){
            if(shape != null && shape instanceof DraggableStation){
                scene.setCursor(Cursor.CROSSHAIR);
                app.getWorkspaceComponent().reloadWorkspace(dataManager);
            } else{
                dataManager.setState(MapState.NOTHING);
                dataManager.unhighlightShape();
                workspace.nothingSelected();
            }
        } else if(dataManager.isInState(MapState.ADD_IMAGE)) {       // Sizing image
            dataManager.startImage(x, y);
            workspace.reloadWorkspace(dataManager);
        }
       }

    /**
     * Respond to mouse dragging on the rendering surface, which we call canvas,
     * but is actually a Pane.
     * @param x
     *  The x coordinate on the canvas
     * @param y
     *  The y coordinate on the canvas
     */
    public void processCanvasMouseDragged(int x, int y){
        // sizing image
        if (dataManager.isInState(MapState.ADD_IMAGE)) {
            DraggableImage newDraggableShape = (DraggableImage)dataManager.getNewShape();
            newDraggableShape.size(x, y);
        } else if (dataManager.isInState(MapState.DRAGGING_SHAPE) && !(dataManager.getSelectedShape() instanceof Line)) {
            try{
                Draggable selectedDraggableShape = (Draggable) dataManager.getSelectedShape();
                selectedDraggableShape.drag(x, y);

            } catch(NullPointerException ex){
                
            }
        }
    }

    /**
     * Respond to mouse button release on the rendering surface, which we call canvas,
     * but is actually a Pane.
     * @param x
     *  The x coordinate on the canvas
     * @param y
     *  The y coordinate on the canvas
     */
    public void processCanvasMouseRelease(int x, int y) {
        // sizing image
        if (dataManager.isInState(MapState.ADD_IMAGE)) {
            dataManager.selectSizedShape();
            dataManager.setState(MapState.SELECTING_SHAPE);
     
            // Set cursor back to normal
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.DEFAULT);
        } else if (dataManager.isInState(MapState.DRAGGING_SHAPE) && !(dataManager.getSelectedShape() instanceof Line)
                && dataManager.getSelectedShape() instanceof Draggable) {
            dataManager.setState(MapState.SELECTING_SHAPE);
            Draggable shape = (Draggable) dataManager.getSelectedShape();
            
            if(metroText != null && metroText instanceof DraggableText && ((DraggableText)metroText).getMetroLine() != null){
                if(initX != metroText.getLayoutX() || initY != metroText.getLayoutY()){
                    ((DraggableText)metroText).setLocation(x, y);
                    DragT trans = new DragT((Node)metroText, initX, initY);
                    jtps.addTransaction(trans);
                }
            } else if(shape != null && !(shape instanceof Line) && (initX != shape.getX() || initY != shape.getY())){
                    DragT trans = new DragT((Node)shape, initX, initY);
                    jtps.addTransaction(trans);
            }
            
            // Set cursor back to normal
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.DEFAULT);
        } else if(dataManager.isInState(MapState.ADD_STATION)){

//            MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
//            AppMetroStationDialogSingleton dialog = AppMetroStationDialogSingleton.getSingleton();
//            String stationName = dialog.show(workspace.metroStationObserList);
//            
//            app.getWorkspaceComponent().reloadWorkspace(dataManager);
//            
//            if(stationName != null){
//                dataManager.selectedStation = dataManager.findMetroStation(stationName);
//                if(dataManager.selectedStation != null)
//                    dataManager.addStationToLine(x, y);
//            }
//            
//            dataManager.setState(MapState.NOTHING);
//            dataManager.unhighlightShape();
//            workspace.nothingSelected();
        } else if(dataManager.isInState(MapState.REMOVE_STATION)){
            MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
            
            Node shape = dataManager.getTopShape(x, y);
            
            if(shape != null && shape instanceof DraggableStation && dataManager.selectedMetroLine.contains(shape)){
                dataManager.selectedStation = (DraggableStation) shape;
                dataManager.removeStationFromLine();
            } else {
                dataManager.setState(MapState.NOTHING);
                dataManager.unhighlightShape();
                workspace.nothingSelected();
            }
        } else if (dataManager.isInState(MapState.NOTHING)) {
            dataManager.setState(MapState.SELECTING_SHAPE);
            
            // Set cursor back to normal
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.DEFAULT);
        }
        MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
        workspace.reloadWorkspace(dataManager);
    }
}
