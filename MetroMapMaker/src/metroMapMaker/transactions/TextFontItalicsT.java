package metroMapMaker.transactions;

import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableText;
/**
 * For when the font italics change.
 * 
 * @author fannydai
 */
public class TextFontItalicsT implements jTPS_Transaction{
    // current values
    private final DraggableText text;
    private final boolean isItalics;
    
    public TextFontItalicsT(DraggableText text){
        this.text = text;
        this.isItalics = text.getTextItalics();
    }

    @Override
    public void doTransaction(){        
        // Set the text font
        text.setTextItalics(!isItalics);
        text.setTextFont();
    }
    
    @Override 
    public void undoTransaction(){
        // Set the text font
        text.setTextItalics(isItalics);
        text.setTextFont();
    }
}
