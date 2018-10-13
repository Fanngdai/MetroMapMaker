/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metroMapMaker.transactions;

import jtps.jTPS_Transaction;
import metroMapMaker.data.DraggableText;
import metroMapMaker.data.MetroLine;

/**
 *
 * @author fannydai
 */
public class MetroLineTextFamilyT implements jTPS_Transaction{
    // current values
    private final DraggableText name1;
    private final DraggableText name2;
    private final String fontSelected;
    private final String previousFontSelected;
    
    public MetroLineTextFamilyT(MetroLine line, String font){
        this.name1 = line.getName1();
        this.name2 = line.getName2();
        
        this.previousFontSelected = name1.getFontFamily();
        this.fontSelected = font;
    }

    @Override
    public void doTransaction(){        
        // Set the text font
        name1.setFontFamily(fontSelected);
        name1.setTextFont();
        
        name2.setFontFamily(fontSelected);
        name2.setTextFont();
    }
    
    @Override 
    public void undoTransaction(){
        // Set the text font
        name1.setFontFamily(previousFontSelected);
        name1.setTextFont();
        
        name2.setFontFamily(previousFontSelected);
        name2.setTextFont();
    }
}
