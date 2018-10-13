package metroMapMaker.transactions;

import djf.AppTemplate;
import javafx.scene.paint.Color;
import jtps.jTPS_Transaction;
import metroMapMaker.data.MetroLine;
import metroMapMaker.gui.MapWorkspace;

/**
 * For changing the line name and color.
 * 
 * @author fannydai
 */

public class LineEditT implements jTPS_Transaction {
    private final MapWorkspace workspace;
    
    private final MetroLine line;
    private final String prevName;
    private final Color prevColor;
    private final String name;
    private final Color color;
    private final boolean prevCircular;
    private final boolean circular;

    public LineEditT(AppTemplate app, MetroLine line, String prevName, Color prevColor, boolean prevCircular, String name, Color color, boolean circular) {
        this.workspace = (MapWorkspace) app.getWorkspaceComponent();
        
        this.line = line;
        this.prevName = prevName;
        this.prevColor = prevColor;
        this.prevCircular = prevCircular;
        this.name = name;
        this.color = color;
        this.circular = circular;
    }
    
    @Override
    public void doTransaction() {
        line.setNameAndColor(name, color,circular);
        if(!prevName.equalsIgnoreCase(name)){
            // Remove previous name and replace with new name
            this.workspace.addLineToCombo(name);
            this.workspace.removeLineFromCombo(prevName);
        }
    }

    @Override
    public void undoTransaction() {
        line.setNameAndColor(prevName, prevColor, prevCircular);
        if(!prevName.equalsIgnoreCase(name)){
            // Remove previous name and replace with new name
            this.workspace.addLineToCombo(prevName);
            this.workspace.removeLineFromCombo(name);
        }
    }
}
