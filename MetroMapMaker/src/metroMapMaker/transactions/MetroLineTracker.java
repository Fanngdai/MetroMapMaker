package metroMapMaker.transactions;

import metroMapMaker.data.MetroLine;

/**
 * Keeps the location of the station in the metroLine. For undo and redo.
 * 
 * @author fannydai
 */
public class MetroLineTracker {
    private final MetroLine line;
    private final int positionLine;
    private final int positionStation;
    
    public MetroLineTracker(MetroLine line, int positionLine, int positionStation){
        this.line = line;
        this.positionLine = positionLine;
        this.positionStation = positionStation;
    }
    
    public MetroLine getMetroLine(){
        return this.line;
    }
    public int getPositionLine(){
        return this.positionLine;
    }
    public int getPositionStation(){
        return this.positionStation;
    }
}
