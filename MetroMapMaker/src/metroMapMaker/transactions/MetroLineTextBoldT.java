package metroMapMaker.transactions;

import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableText;
import metroMapMaker.data.MetroLine;

/**
 *
 * @author fannydai
 */
public class MetroLineTextBoldT implements jTPS_Transaction{
    // current values
    private final DraggableText name1;
    private final DraggableText name2;
    private final boolean isBold;
    
    public MetroLineTextBoldT(MetroLine line){
        this.name1 = line.getName1();
        this.name2 = line.getName2();
        this.isBold = name1.getTextBold();
    }

    @Override
    public void doTransaction(){        
        // Set the text font
        name1.setTextBold(!isBold);
        name1.setTextFont();
        
        name2.setTextBold(!isBold);
        name2.setTextFont();
    }
    
    @Override 
    public void undoTransaction(){
        // Set the text font
        name1.setTextBold(isBold);
        name1.setTextFont();
        
        name2.setTextBold(isBold);
        name2.setTextFont();
    }
}
