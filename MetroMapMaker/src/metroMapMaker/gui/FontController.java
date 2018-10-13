package metroMapMaker.gui;

import djf.AppTemplate;
import javafx.scene.paint.Color;
import jtps.jTPS;
import metroMapMaker.data.DraggableStation;
import metroMapMaker.data.DraggableText;
import metroMapMaker.data.MapData;
import metroMapMaker.data.MapState;
import metroMapMaker.data.MetroLine;
import metroMapMaker.transactions.ColorT;
import metroMapMaker.transactions.MetroLineTextBoldT;
import metroMapMaker.transactions.MetroLineTextColorT;
import metroMapMaker.transactions.MetroLineTextFamilyT;
import metroMapMaker.transactions.MetroLineTextItalicsT;
import metroMapMaker.transactions.MetroLineTextSizeT;
import metroMapMaker.transactions.TextFontBoldT;
import metroMapMaker.transactions.TextFontFamilyT;
import metroMapMaker.transactions.TextFontItalicsT;
import metroMapMaker.transactions.TextFontSizeT;

/**
 * All controls for row 5. Font box.
 * 
 * @author fannydai
 */
public class FontController {
    AppTemplate app;
    MapData dataManager;
    private final jTPS jtps;
    
    public FontController(AppTemplate initApp) {
	app = initApp;
	dataManager = (MapData)app.getDataComponent();
        jtps = dataManager.getJTPS();
    }
    
    /**
     * Changes the color of the text.
     * @param color
     *  The color to switch the text to
     */
    public void processColorPicker(Color color){
        if(dataManager.isInState(MapState.ADD_SHAPE))
            return;
        MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
        if(workspace.selectedLine() != null){
            MetroLine line = workspace.selectedLine();
            if(!line.getColor().equals(color)){
                MetroLineTextColorT trans = new MetroLineTextColorT(line, color);
                jtps.addTransaction(trans);
            }
        } else if(workspace.selectedStation()!= null){
            DraggableStation station = workspace.selectedStation();
            if(!color.equals(station.getLabel().getFill())){
                ColorT trans = new ColorT(station.getLabel(), color);
                jtps.addTransaction(trans);
            }
        } else if(dataManager.getSelectedShape() != null && dataManager.getSelectedShape() instanceof DraggableText) {
            DraggableText text = (DraggableText) dataManager.getSelectedShape();
            if(!color.equals(text.getFill())){
                ColorT trans = new ColorT(text, color);
                jtps.addTransaction(trans);
            }
        } else {
            workspace.nothingSelected();
        }
    }
    
    /**
     * Changes the text to bold/not bold depending on its current value.
     */
    public void processbold(){
        if(dataManager.isInState(MapState.ADD_SHAPE))
            return;
        MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
        if(workspace.selectedLine() != null){
            MetroLine line = workspace.selectedLine();
            MetroLineTextBoldT trans = new MetroLineTextBoldT(line);
            jtps.addTransaction(trans);
        } else if(workspace.selectedStation()!= null){
            DraggableStation station = workspace.selectedStation();
            TextFontBoldT trans = new TextFontBoldT(station.getLabel());
            jtps.addTransaction(trans);
        } else if(dataManager.getSelectedShape() != null && dataManager.getSelectedShape() instanceof DraggableText){
            DraggableText text = (DraggableText) dataManager.getSelectedShape();
            TextFontBoldT trans = new TextFontBoldT(text);
            jtps.addTransaction(trans);
        } else {
            workspace.nothingSelected();
        }
    }
    
    /**
     * Changes the text to italics/not italics depending on its current value.
     */
    public void processItalics(){
        if(dataManager.isInState(MapState.ADD_SHAPE))
            return;
        MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
        if(workspace.selectedLine() != null){
            MetroLine line = workspace.selectedLine();
            MetroLineTextItalicsT trans = new MetroLineTextItalicsT(line);
            jtps.addTransaction(trans);
        } else if(workspace.selectedStation()!= null){
            DraggableStation station = workspace.selectedStation();
            TextFontItalicsT trans = new TextFontItalicsT(station.getLabel());
            jtps.addTransaction(trans);
        } else if(dataManager.getSelectedShape() != null && dataManager.getSelectedShape() instanceof DraggableText){
            DraggableText text = (DraggableText) dataManager.getSelectedShape();
            TextFontItalicsT trans = new TextFontItalicsT(text);
            jtps.addTransaction(trans);            
        } else {
            workspace.nothingSelected();
        }
    }
    
    /**
     * Changes the text to a certain size
     * @param num 
     *  The size of the text
     */
    public void processFontSize(Integer num){
        if(dataManager.isInState(MapState.ADD_SHAPE))
            return;
        MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
        if(workspace.selectedLine() != null){
            MetroLine line = workspace.selectedLine();
            if(!line.getName1().getFontSize().equals(num)){
                MetroLineTextSizeT trans = new MetroLineTextSizeT(line, num);
                jtps.addTransaction(trans);
            }
        } else if(workspace.selectedStation()!= null){
            DraggableStation station = workspace.selectedStation();
            if(!station.getLabel().getFontSize().equals(num)){
                TextFontSizeT trans = new TextFontSizeT(station.getLabel(), num);
                jtps.addTransaction(trans);
            }
        } else if(dataManager.getSelectedShape() != null && dataManager.getSelectedShape() instanceof DraggableText){
            DraggableText text = (DraggableText) dataManager.getSelectedShape();
            // Make sure the 2 size are not the same
            if(!text.getFontSize().equals(num)){
                TextFontSizeT trans = new TextFontSizeT(text, num);
                jtps.addTransaction(trans);
            }
        } else {
            workspace.nothingSelected();
        }
    }
    
    /**
     * Changes the text family to selected family
     * @param font
     *  The font family to change to
     */
    public void processFontFamily(String font){
        MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
        if(workspace.selectedLine() != null){
            MetroLine line = workspace.selectedLine();
            if(!line.getName1().getFontFamily().equals(font)){
                MetroLineTextFamilyT trans = new MetroLineTextFamilyT(line, font);
                jtps.addTransaction(trans);
            }
            
        } else if(workspace.selectedStation()!= null){
            DraggableStation station = workspace.selectedStation();
            if(!station.getLabel().getFontFamily().equals(font)){
                TextFontFamilyT trans = new TextFontFamilyT(station.getLabel(), font);
                jtps.addTransaction(trans);
            }
        } else if(dataManager.getSelectedShape() != null && dataManager.getSelectedShape() instanceof DraggableText){
            DraggableText text = (DraggableText) dataManager.getSelectedShape();
            if(!text.getFontFamily().equals(font)){
                TextFontFamilyT trans = new TextFontFamilyT(text, font);
                jtps.addTransaction(trans);
            }
        } else {
            workspace.nothingSelected();
        }
    }
    
}
