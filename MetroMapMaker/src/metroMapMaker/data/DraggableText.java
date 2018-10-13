package metroMapMaker.data;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * This is a draggable text for our metroMapMaker application.
 * 
 * @author Richard McKenna
 * @author Fanng Dai
 * @version 1.0
 */

public class DraggableText extends Text implements Draggable {
    protected double startX;
    protected double startY;
    protected double initX;
    protected double initY;
    
    private String fontSelected;
    private Integer fontSize;
    private boolean isBold = false;
    private boolean isItalics = false;
    
    protected final MetroLine metroLine;
    
    public DraggableText(String text) {
        this.setText(text);
        this.metroLine = null;
        defaultValue();
    }
    
    public DraggableText(String text, MetroLine metroLine) {
        this.setText(text);
        this.metroLine = metroLine;
        defaultValue();
    }
    
    private void defaultValue(){
        ((Node)this).setOpacity(1.0);
        this.setFill(Color.BLACK);
        fontSelected = "Times New Roman";
        fontSize = 12;
        isBold = false;
        isItalics = false;
        setTextFont();
    }
    
    @Override
    public MapState getStartingState() {
        return MapState.ADD_TEXT;
    }
    
    @Override
    public void start(int x, int y) {
        initX = getX();
        initY = getY();
        startX = x;
        startY = y;
        setX(x);
        setY(y);
    }
    
    /**
     * Drags the text.
     * @param x
     *  Where the mouse is x-coordinate
     * @param y 
     *  y-coordinate
     */
    @Override
    public void drag(int x, int y) {
        double newX = initX + x - startX;
        double newY = initY + y - startY;
        startX = x;
        startY = y;
        initX = newX;
        initY = newY;
        setX(newX);
        setY(newY);
    }
    
    @Override
    public void size(int x, int y) {
	double width = x - getX();
        setX(x+width/10000);
	double height = y - getY();
        setY(y+height/10000);
    }

    @Override
    public double getWidth() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getHeight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight) {
	xProperty().set(initX);
	yProperty().set(initY);
        setX(initX+initWidth/10000);
        setY(initY+initHeight/10000);
    }
    
    @Override
    public void setSize(double initWidth, double initHeight) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void setLocation(double initX, double initY) {
	xProperty().set(initX);
	yProperty().set(initY);
    }
    
    @Override
    public String getShapeType() {
	return TEXT;
    }
    
    public MetroLine getMetroLine(){
        return this.metroLine;
    }
    
    public void setFontFamily(String font){
        this.fontSelected = font;
    }
    public void setFontSize(Integer integer){
        this.fontSize = integer;
    }
    public void setTextBold(){
        isBold = !isBold;
    }
    public void setTextBold(boolean flag){
        isBold = flag;
    }
    public void setTextItalics(){
        isItalics = !isItalics;
    }
    public void setTextItalics(boolean flag){
        this.isItalics = flag;
    }
    
    /**
     * Returns the font family.
     * Just to be sure, if the fontSelected is null, we change it to times roman.
     * @return 
     *  The text family
     */
    public String getFontFamily(){
        if(fontSelected == null)
            fontSelected = "Times New Roman";
        return fontSelected;
    }
    
    /**
     * Returns the font size.
     * Just to be sure, if the size is null, we change it to 12. (default size)
     * @return 
     *  The font size.
     */
    public Integer getFontSize(){
        if(fontSize == null)
            fontSize = 12;
        return fontSize;
    }
    public boolean getTextBold(){
        return isBold;
    }
    public boolean getTextItalics(){
        return isItalics;
    }
    
    public void setTextFont(){
        FontPosture fp = isItalics?FontPosture.ITALIC:FontPosture.REGULAR;
        FontWeight fw = isBold?FontWeight.BOLD:FontWeight.NORMAL;
        this.setFont(Font.font(fontSelected, fw, fp, fontSize));
    }
}