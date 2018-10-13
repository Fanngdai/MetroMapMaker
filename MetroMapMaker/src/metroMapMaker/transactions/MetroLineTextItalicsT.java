package metroMapMaker.transactions;

import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableText;
import metroMapMaker.data.MetroLine;

/**
 *
 * @author fannydai
 */
public class MetroLineTextItalicsT implements jTPS_Transaction{
    // current values
    private final DraggableText name1;
    private final DraggableText name2;
    private final boolean isItalics;
    
    public MetroLineTextItalicsT(MetroLine line){
        this.name1 = line.getName1();
        this.name2 = line.getName2();
        this.isItalics = name1.getTextItalics();
    }

    @Override
    public void doTransaction(){        
        // Set the text font
        name1.setTextItalics(!isItalics);
        name1.setTextFont();
        
        name2.setTextItalics(!isItalics);
        name2.setTextFont();
    }
    
    @Override 
    public void undoTransaction(){
        // Set the text font
        name1.setTextItalics(isItalics);
        name1.setTextFont();
        
        name2.setTextItalics(isItalics);
        name2.setTextFont();
    }
}
