package metroMapMaker.transactions;

import djf.AppTemplate;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import metroMapMaker.data.MapData;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import jtps.jTPS_Transaction;

/**
 * @author fannydai
 */
public class BackgroundT implements jTPS_Transaction{
    private final MapData dataManager;
    
    private Color backgroundColor;
    private Color prevBackgroundColor;
    private String imagePath;
    private String prevImagePath;
    
    // Color to color
    public BackgroundT(AppTemplate app, Color previousBackgroundColor, Color backgroundColor){
        this.dataManager = (MapData)app.getDataComponent();
        this.backgroundColor = backgroundColor;
        this.prevBackgroundColor = previousBackgroundColor;
    }
    
    // Color to image
    public BackgroundT(AppTemplate app, Color previousBackgroundColor, String imagePath){
        this.dataManager = (MapData)app.getDataComponent();
        this.imagePath = imagePath;
        this.prevBackgroundColor = previousBackgroundColor;
    }
    
    // image to color
    public BackgroundT(AppTemplate app, String prevImagePath, Color backgroundColor){
        this.dataManager = (MapData)app.getDataComponent();
        this.backgroundColor = backgroundColor;
        this.prevImagePath = prevImagePath;
    }
    
    // image to image
    public BackgroundT(AppTemplate app, String prevImagePath, String imagePath){
        this.dataManager = (MapData)app.getDataComponent();
        this.imagePath = imagePath;
        this.prevImagePath = prevImagePath;
    }
    
    @Override
    public void doTransaction(){
        // Switch to color
        if(backgroundColor != null) {
            this.dataManager.setBackgroundColor(backgroundColor);
        } else {        // Switch to image
            try {
                BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
                this.dataManager.setBackgroundImage(bufferedImage, imagePath);
            } catch (IOException ex) {
                Logger.getLogger(BackgroundT.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override 
    public void undoTransaction(){
        // Switch to color
        if(prevBackgroundColor != null) {
            this.dataManager.setBackgroundColor(prevBackgroundColor);
        } else {        // Switch to image
            try {
                BufferedImage bufferedImage = ImageIO.read(new File(prevImagePath));
                this.dataManager.setBackgroundImage(bufferedImage, prevImagePath);
            } catch (IOException ex) {
                Logger.getLogger(BackgroundT.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
