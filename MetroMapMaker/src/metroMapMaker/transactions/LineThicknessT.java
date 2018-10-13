package metroMapMaker.transactions;

import jtps.jTPS_Transaction;
import metroMapMaker.data.MetroLine;

/**
 * When a station radius changes.
 * 
 * @author fannydai
 */
public class LineThicknessT implements jTPS_Transaction{
    private final MetroLine line;
    private final int thickness;
    private final int prevThickness;
    
    public LineThicknessT(MetroLine line, int prevThickness){
        this.line = line;
        this.prevThickness = prevThickness;
        this.thickness = (int) line.getLineThickness();
    }
    
    @Override
    public void doTransaction(){
        this.line.setLineThickness(thickness);
    }
    
    @Override 
    public void undoTransaction(){
        this.line.setLineThickness(prevThickness);
    }
}
