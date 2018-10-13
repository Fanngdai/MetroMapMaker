package metroMapMaker.transactions;

import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableText;

/**
 * For when the size of the font changes.
 * 
 * @author fannydai
 */
public class TextFontSizeT implements jTPS_Transaction{
    private final DraggableText text;
    private final Integer fontSize;
    private final Integer previousFontSize;

    public TextFontSizeT(DraggableText text, Integer size){
        this.text = text;
        this.previousFontSize = text.getFontSize();
        this.fontSize = size;
    }

    @Override
    public void doTransaction(){
        // Set the text font
        text.setFontSize(fontSize);
        text.setTextFont();
    }
    
    @Override 
    public void undoTransaction(){
        text.setFontSize(previousFontSize);
        text.setTextFont();
    }

}
