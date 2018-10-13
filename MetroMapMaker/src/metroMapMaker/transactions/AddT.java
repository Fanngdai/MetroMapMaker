package metroMapMaker.transactions;

import djf.AppTemplate;
import javafx.scene.Node;
import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableImage;
import metroMapMaker.data.DraggableStation;
import metroMapMaker.data.MapData;

/**
 * For adding an element to the canvas.
 * 
 * @author fannydai
 */
public class AddT implements jTPS_Transaction{
    private final MapData dataManager;
    private final Node node;
    
    public AddT(AppTemplate app, Node node){
        this.dataManager = (MapData) app.getDataComponent();
        
        this.node = node;
    }
    
    @Override
    public void doTransaction(){
        if(node instanceof DraggableStation){
            dataManager.getShapes().add(dataManager.getShapes().size()-1, node);
        }
        else if(node instanceof DraggableImage){
            dataManager.getShapes().add(0, node);
        }
        else{
            dataManager.getShapes().add(node);
        }
    }
    
    @Override
    public void undoTransaction(){
        dataManager.getShapes().remove(node);
    }
}
