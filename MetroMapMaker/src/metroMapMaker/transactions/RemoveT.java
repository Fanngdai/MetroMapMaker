package metroMapMaker.transactions;

import djf.AppTemplate;
import javafx.scene.Node;
import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableImage;
import metroMapMaker.data.DraggableStation;
import metroMapMaker.data.MapData;

/**
 * Removing an element.
 * 
 * @author fannydai
 */
public class RemoveT implements jTPS_Transaction{
    private final MapData dataManager;
    private final Node node;
    
    public RemoveT(AppTemplate app, Node node){
        this.dataManager = (MapData) app.getDataComponent();
        
        this.node = node;
    }
    
    @Override
    public void doTransaction(){
        dataManager.getShapes().remove(node);
    }
    
    @Override 
    public void undoTransaction(){
        dataManager.getShapes().add(node);
        
        if(node instanceof DraggableStation){
            node.toFront();
        }
        else if(node instanceof DraggableImage){
            node.toBack();
        }
    }
}
