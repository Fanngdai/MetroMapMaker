package metroMapMaker.transactions;

import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableText;

/**
 * When the DraggableText family font changes.
 * 
 * @author fannydai
 */
public class TextFontFamilyT implements jTPS_Transaction{
    private final DraggableText text;
    private final String fontSelected;
    private final String previousFontSelected;

    public TextFontFamilyT(DraggableText text, String font){
        this.text = text;
        this.previousFontSelected = text.getFontFamily();
        this.fontSelected = font;
    }

    @Override
    public void doTransaction(){
        // Set the text font
        text.setFontFamily(fontSelected);
        text.setTextFont();
    }
    
    @Override 
    public void undoTransaction(){
        text.setFontFamily(previousFontSelected);
        text.setTextFont();
    }

}
