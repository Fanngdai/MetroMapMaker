/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metroMapMaker.gui;

import djf.AppTemplate;
import djf.ui.AppAlertDialogSingleton;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import static javafx.scene.paint.Color.BLACK;
import javafx.scene.transform.Scale;
import metroMapMaker.data.MapData;
import metroMapMaker.transactions.MapSizeDecreaseT;
import metroMapMaker.transactions.MapSizeIncreaseT;

/**
 * @author fannydai
 */
public class ScrolllPane extends ScrollPane{
    // The group to zoom
    protected Group zoom;
    // The scale to zoom in/out on
    protected Scale scale;
    // The pane inside the scrollPane the canvas...
    protected Pane pane;
    
    // The grid
    private Canvas grid;
    private boolean gridEnabled;
    
    // The scale of the map
    private double scaleAmt;
    private final MapData dataManager;
    
    public ScrolllPane(AppTemplate app, Pane pane){
        this.dataManager = (MapData) app.getDataComponent();
        this.pane = pane;
        Group paneGroup = new Group();
        zoom = new Group();
        zoom.getChildren().add(pane);
        paneGroup.getChildren().add(zoom);
        this.setContent(paneGroup);
        scale = new Scale();
        zoom.getTransforms().add(scale);
        
        // Set the background to black
        BackgroundFill fill = new BackgroundFill(Color.BLACK, null, null);
        Background background = new Background(fill);
        this.setBackground(background);
        
        pane.setPrefSize(1100, 700);
        scaleAmt = 1;
        
        gridEnabled = false;
        
        // Hide the scroll bars
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollBarPolicy.NEVER);
    }
    
    public void increasePane(){
        if(pane.getWidth()*1.1 > 2500 || pane.getHeight()*1.1 > 2500){
            AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
            dialog.show("Unable to increase size", "Unable to increase map size", "This is the biggest the map can be!");
        } else{
            MapSizeIncreaseT trans = new MapSizeIncreaseT(this, pane);
            dataManager.getJTPS().addTransaction(trans);
        }
    }
    
    public void decreasePane(){
        if(pane.getWidth()*.9 < 200 || pane.getHeight()*.9 < 200){
            AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
            dialog.show("Unable to decrease size", "Unable to decrease map size", "This is the smallest the map can be!");
        } else{
            MapSizeDecreaseT trans = new MapSizeDecreaseT(this, pane);
            dataManager.getJTPS().addTransaction(trans);
        }
    }
    
    /**
     * Zoom in
     */
    public void increaseScale(){
        AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
        // 1.99 because we want it to stop when the size is double
        if(scaleAmt >= 1.99){
            dialog.show("Unable to zoom in", "Unable to zoom in on map.", "This is double the size of the original map size. I can no longer zoom in anymore. Sorry");
        } else{
            scaleAmt += .1;
            this.scale.setX(scaleAmt);
            this.scale.setY(scaleAmt);
            
            if(pane.getChildren().contains(grid))
                redrawGrid();
        }
    }
    
    /**
     * Zoom out
     */
    public void decreaseScale(){
        AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
        // .51 bc we want it to stop when the size is half the size
        if(scaleAmt <= .51){
            dialog.show("Unable to zoom out", "Unable to zoom out on map.", "This is half the size of the original map size. I can no longer zoom out anymore. Sorry");
        } else{
            scaleAmt -= .1;
            this.scale.setX(scaleAmt);
            this.scale.setY(scaleAmt);
            
            if(pane.getChildren().contains(grid))
                redrawGrid();
        }
    }
    
    public void enableGrid(){
        double width = pane.getWidth();
        double height = pane.getHeight();
        
        // Add grid
        grid = new Canvas(width,height);
        double scaledOffset = 20 + this.scale.getX() / 100;
        
        // So we cannot click on it.
        grid.setMouseTransparent(true);
        
        GraphicsContext gc = grid.getGraphicsContext2D();
        
        gc.setLineWidth(this.scale.getX() / 100 - 0.5);
        gc.setStroke(BLACK);
        
        // Just to make sure that the whole canvas gets the grid.
        for(double i = scaledOffset; i<width+height; i+= scaledOffset){
            gc.strokeLine(i, 0, i, height);
            gc.strokeLine(0, i, width, i);
        }
        
        pane.getChildren().add(grid);
        grid.toBack();
        
        gridEnabled = true;
    }
    
    public void disableGrid(){
        if(this.pane.getChildren().contains(grid))
            this.pane.getChildren().remove(grid);
        gridEnabled = false;
    }
    
    public void redrawGrid(){
        disableGrid();
        enableGrid();
        gridEnabled = true;
    }
    
    public boolean gridEnabled(){
        return this.gridEnabled;
    }
}
