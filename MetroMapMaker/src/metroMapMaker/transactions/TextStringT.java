package metroMapMaker.transactions;

import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableText;

/**
 * When the value of the DraggableText string change.
 * Do not need for this hw...
 * 
 * @author fannydai
 */
public class TextStringT implements jTPS_Transaction{
    private final DraggableText text;
    private final String word;
    private final String previousWord;

    public TextStringT(DraggableText text, String word){
        this.text = text;
        this.word = word;
        this.previousWord = text.getText();
    }

    @Override
    public void doTransaction(){
        // Set the text font
        text.setText(word);
    }
    
    @Override 
    public void undoTransaction(){
        text.setFontFamily(previousWord);
    }

}
