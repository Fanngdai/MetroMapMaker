package metroMapMaker.transactions;

import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableText;
import metroMapMaker.data.MetroLine;

/**
 *
 * @author fannydai
 */
public class MetroLineTextSizeT implements jTPS_Transaction{
    // current values
    private final DraggableText name1;
    private final DraggableText name2;
    private final Integer fontSize;
    private final Integer previousFontSize;
    
    public MetroLineTextSizeT(MetroLine line, Integer size){
        this.name1 = line.getName1();
        this.name2 = line.getName2();
        
        this.previousFontSize = name1.getFontSize();
        this.fontSize = size;
    }

    @Override
    public void doTransaction(){        
        // Set the text font
        name1.setFontSize(fontSize);
        name1.setTextFont();
        
        name2.setFontSize(fontSize);
        name2.setTextFont();
    }
    
    @Override 
    public void undoTransaction(){
        // Set the text font
        name1.setFontSize(previousFontSize);
        name1.setTextFont();
        
        name2.setFontSize(previousFontSize);
        name2.setTextFont();
    }
}
