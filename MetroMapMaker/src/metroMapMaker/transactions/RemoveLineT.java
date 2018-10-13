package metroMapMaker.transactions;

import djf.AppTemplate;
import jtps.jTPS_Transaction;
import metroMapMaker.data.MapData;
import metroMapMaker.data.MetroLine;
import metroMapMaker.gui.MapWorkspace;

/**
 *
 * @author fannydai
 */
public class RemoveLineT implements jTPS_Transaction {
    private final MapWorkspace workspace;
    private final MetroLine line;
    
    public RemoveLineT(AppTemplate app, MetroLine line){
        this.workspace = (MapWorkspace) app.getWorkspaceComponent();
        this.line = line;
    }

    @Override
    public void doTransaction() {
        line.removeMetroLine();
        if(workspace.metroLineObserList.contains(line.getName()))
            workspace.metroLineObserList.remove(line.getName());
    }

    @Override
    public void undoTransaction() {
        MapData.metroLines.add(line);
        workspace.metroLineObserList.add(line.getName());
        line.connectAll();
    }
}