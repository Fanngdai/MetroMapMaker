package metroMapMaker.transactions;

import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableText;
/**
 * For when the font changes bold...
 * 
 * @author fannydai
 */
public class TextFontBoldT implements jTPS_Transaction{
    // current values
    private final DraggableText text;
    private final boolean isBold;
    
    public TextFontBoldT(DraggableText text){
        this.text = text;
        this.isBold = text.getTextBold();
    }

    @Override
    public void doTransaction(){        
        // Set the text font
        text.setTextBold(!isBold);
        text.setTextFont();
    }
    
    @Override 
    public void undoTransaction(){
        // Set the text font
        text.setTextBold(isBold);
        text.setTextFont();
    }
}
