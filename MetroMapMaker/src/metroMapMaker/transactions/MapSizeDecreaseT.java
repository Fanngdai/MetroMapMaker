package metroMapMaker.transactions;

import javafx.scene.layout.Pane;
import jtps.jTPS_Transaction;
import metroMapMaker.gui.ScrolllPane;

/**
 *
 * @author fannydai
 */
public class MapSizeDecreaseT implements jTPS_Transaction {
    private final ScrolllPane scrollPane;
    private final Pane pane;
    private final double x;
    private final double y;
    
    private final double xx;
    private final double yy;
    
    public MapSizeDecreaseT(ScrolllPane scrollPane, Pane pane){
        this.scrollPane = scrollPane;
        this.pane = pane;
        
        this.x = pane.getWidth() * .9;
        this.y = pane.getHeight() * .9;
        
        this.xx = pane.getWidth();
        this.yy = pane.getHeight();
    }
    
    @Override
    public void doTransaction() {
        pane.setPrefSize(x, y);
            
        if(scrollPane.gridEnabled())
            scrollPane.redrawGrid();
    }

    @Override
    public void undoTransaction() {
       pane.setPrefSize(xx, yy);
            
        if(scrollPane.gridEnabled())
            scrollPane.redrawGrid();
    }
}
