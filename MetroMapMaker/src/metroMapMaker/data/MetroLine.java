package metroMapMaker.data;

import djf.ui.AppAlertDialogSingleton;
import java.util.LinkedList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import static metroMapMaker.data.Draggable.LINE;

/**
 *  A linked list with the text on the end and stores stations
 * 
 * @author Fanny Dai
 */
public final class MetroLine extends LinkedList<Node>{
    // Text at the end of the line
    private final DraggableText name1;
    private final DraggableText name2;
    
    // The line name and color. Keep for reference
    private String lineName;
    private Color lineColor;
    // Default circles at the end of line
    private final DraggableCircle circle;
    
    protected boolean circular;
    
    LinkedList<DraggableText> stationNames;
    
    double lineThickness;
    
    public MetroLine(String name, Color color){
        this.lineName = name;
        this.lineColor = color;
        this.circular = false;
        this.stationNames = new LinkedList<>();
        this.lineThickness = 5;
        
        this.name1 = new DraggableText(name, this);
        this.name2 = new DraggableText(name, this);
        // The name and color of the line
        this.name1.setFill(color);
        this.name2.setFill(color);

        // The circles at the end of the line
        circle = new DraggableCircle(this);
        circle.setFill(color);
        
        connectTextStart();
    }
    
    /**
     * Connects two text and a line
     */    
    public void connectTextStart() {
        Line line = new Line(10, 10, 100, 100);
        line.setStrokeWidth(lineThickness);
        line.setStroke(lineColor);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
            
        this.addFirst(name1);
        this.add(line);
        this.addLast(name2);

        name1.xProperty().set(line.getStartX() + 10);
        name1.yProperty().set(line.getStartY() + 10);

        name2.xProperty().set(line.getEndX() + 10);
        name2.yProperty().set(line.getEndY() + 10);
    }
    
    /**
     * Bring text back. After dragging.
     * 
     * Only for when the line is not circular
     * 
     * @return 
     *  The node which wasn't there
     */
    public DraggableText connectText() {
        // Do not connect text. We do not want
        if(circular && stationNames.size() > 2) {
            connectAll();
            return null;
        }
        
        // Remove the circles
        if(this.contains(circle))
            this.remove(circle);
        if(MapData.shapes.contains(circle))
            MapData.shapes.remove(circle);
            
        if(!this.contains(name1)){
            name1.setX(circle.centerXProperty().get());
            name1.setY(circle.centerYProperty().get());
            
            this.addFirst(name1);
            MapData.shapes.add(name1);
            
            connectAll();
            return name1;
        } else if(!this.contains(name2)) {
            name2.setX(circle.centerXProperty().get());
            name2.setY(circle.centerYProperty().get());
            
            this.addLast(name2);
            MapData.shapes.add(name2);
            
            connectAll();
            return name2;
        }
        return null;
    }
        
     /**
     * When dragging, text disappears while circle becomes visible
     * @param text 
     *  The text which was clicked
     * @return 
     *  The circle it is associated with
     */
    public DraggableCircle dragStartEnd(DraggableText text){  
        // Remove all line. Which also unbinds every node
        removeAllLine();
        if(text == name1){
            if(!this.contains(name2)){
                name2.setX(circle.getCenterX());
                name2.setY(circle.getCenterY());
                this.addLast(name2);
                MapData.shapes.add(name2);
            }
                        
            circle.setCenterX(name1.getX());
            circle.setCenterY(name1.getY());
            
            this.remove(name1);
            MapData.shapes.remove(name1);
            
            this.addFirst(circle);
            connectAll();
        } else {
            if(!this.contains(name1)){
                name1.setX(circle.getCenterX());
                name1.setY(circle.getCenterY());
                this.addFirst(name1);
                MapData.shapes.add(name1);
            }
            
            circle.setCenterX(name2.getX());
            circle.setCenterY(name2.getY());
            
            this.remove(name2);
            MapData.shapes.remove(name2);
            this.addLast(circle);
            MapData.shapes.remove(circle);
        }
        connectAll();
        
        return circle;
    }
    
    /**
     * Removes all lines in linked list
     */
    public void removeAllLine(){
        LinkedList<Node> temp = new LinkedList<>();
        // Remove all lines
        for(Node n: this){
            if(!(n instanceof Line)) {
                temp.add(n);
            } else if(MapData.shapes.contains(n)) {         // Remove line from the canvas
                MapData.shapes.remove(n);
            }
        }
        // Add all nodes back in except lines
        this.clear();
        for(Node n: temp) {
            this.add(n);
        }
    }
    
    /**
     * If a station was added or removed, we have to check to make sure that the
     * circular is the same
     */
    private void changeCircular(){
        // Remove what is not needed
        if(circular && stationNames.size() > 2) {
            if(this.contains(name1))
                this.remove(name1);
            if(this.contains(name2))
                this.remove(name2);
            if(this.contains(circle))
                this.remove(circle);
            if(MapData.shapes.contains(name1))
                MapData.shapes.remove(name1);
            if(MapData.shapes.contains(name2))
                MapData.shapes.remove(name2);
            if(MapData.shapes.contains(circle))
                MapData.shapes.remove(circle);
        } else if((circular && stationNames.size() <= 2) || !circular) {        // Cannot be circular if less than 2
            makeNotCircular();
        }
    }
    /**
     * Connects all nodes with a line
     */
    public void connectAll(){        
        removeAllLine();
        
        LinkedList<Node> temp = new LinkedList<>();
        temp.add(this.get(0));
        
        for(int i=0; i<this.size()-1; i++){
            Line line = new Line();
            line.setStrokeWidth(lineThickness);
            line.setStroke(lineColor);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            
            Node a = this.get(i);
            Node b = this.get(i+1);
            
            temp.add(line);
            temp.add(b);
            
            if(a instanceof DraggableText){                
                DraggableText c = (DraggableText) a;
                line.setStartX(c.xProperty().get());
                line.setStartY(c.yProperty().get());
                
                line.startXProperty().bind(c.xProperty());
                line.startYProperty().bind(c.yProperty());
            } else if(a instanceof DraggableStation){
                DraggableStation c = (DraggableStation) a;
                
                line.setStartX(c.centerXProperty().get());
                line.setStartY(c.centerYProperty().get());
                
                line.startXProperty().bind(c.centerXProperty());
                line.startYProperty().bind(c.centerYProperty());
            } else if(a instanceof DraggableCircle){
                line.setStartX(circle.centerXProperty().get());
                line.setStartY(circle.centerYProperty().get());
                
                line.startXProperty().bind(circle.centerXProperty());
                line.startYProperty().bind(circle.centerYProperty());
            }
            
            if(b instanceof DraggableText){
                DraggableText c = (DraggableText) b;
                
                line.setEndX(c.xProperty().get());
                line.setEndY(c.yProperty().get());
                
                line.endXProperty().bind(c.xProperty());
                line.endYProperty().bind(c.yProperty());
            } else if(b instanceof DraggableStation){
                DraggableStation c = (DraggableStation) b;
                
                line.setEndX(c.centerXProperty().get());
                line.setEndY(c.centerYProperty().get());
                
                line.endXProperty().bind(c.centerXProperty());
                line.endYProperty().bind(c.centerYProperty());
            } else if(b instanceof DraggableCircle){
                line.setEndX(circle.centerXProperty().get());
                line.setEndY(circle.centerYProperty().get());
                
                line.endXProperty().bind(circle.centerXProperty());
                line.endYProperty().bind(circle.centerYProperty());
            }
        }
        
        this.clear();
        for(Node n: temp){
            this.add(n);
            // Add the line to shapes
            if(!MapData.shapes.contains(n))
                MapData.shapes.add(n);
        }
        
        if(circular && stationNames.size() > 2)
            makeCircular();
    }
    
    /**
     * Removes the name and 
     */
    private void makeCircular() {  
//        if(!circular)
//            return;

        // Remove those that we do not need.
        if(this.contains(name1))
            this.remove(name1);
        if(this.contains(name2))
            this.remove(name2);
        if(this.contains(circle))
            this.remove(circle);
        if(MapData.shapes.contains(name1))
            MapData.shapes.remove(name1);
        if(MapData.shapes.contains(name2))
            MapData.shapes.remove(name2);
        if(MapData.shapes.contains(circle))
            MapData.shapes.remove(circle);

        // Get the first and last station
        DraggableStation station1 = null;
        DraggableStation station2 = null;

        // No need to bind if the station is under 2
        if(stationNames.size() < 2)
            return;
        
        station1 = getFirstDraggableStation();
        station2 = getLastDraggableStation();

        if(station1 != null && station2 != null){
            Line line = new Line();
            line.setStrokeWidth(lineThickness);
            line.setStroke(lineColor);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            
            line.setStartX(station1.centerXProperty().get());
            line.setStartY(station1.centerYProperty().get());
            line.startXProperty().bind(station1.centerXProperty());
            line.startYProperty().bind(station1.centerYProperty());
            
            line.setEndX(station2.centerXProperty().get());
            line.setEndY(station2.centerYProperty().get());
            line.endXProperty().bind(station2.centerXProperty());
            line.endYProperty().bind(station2.centerYProperty());
            
            this.add(line);
            MapData.shapes.add(line);
        }
    }
    
    public DraggableStation getFirstDraggableStation(){
        if(this.isEmpty())
            return null;
        
        for(Node node: this)
            if(node instanceof DraggableStation)
                return (DraggableStation)node;
        
        return null;
    }
    
    public DraggableStation getLastDraggableStation(){
        if(this.isEmpty())
            return null;
        
        for(int i = this.size()-1 ; i >= 0; i--){
            Node tempNode = this.get(i);
            if(tempNode instanceof DraggableStation){
                return (DraggableStation)tempNode;
            }
        }
        
        return null;
    }
    
    /**
     * Put the text back
     */
    public void makeNotCircular(){
        if(stationNames.isEmpty())
            return;
        
        if(!this.contains(name1)){
            this.addFirst(name1);
            name1.setLocation(getFirstDraggableStation().getCenterX(), getFirstDraggableStation().getCenterY());
            if(!MapData.shapes.contains(name1))
                MapData.shapes.add(name1);
        }
        if(!this.contains(name2)){
            this.addLast(name2);
            name2.setLocation(getLastDraggableStation().getCenterX(), getLastDraggableStation().getCenterY());
            if(!MapData.shapes.contains(name2))
                MapData.shapes.add(name2);
        }
        if(this.contains(circle)){
            this.remove(circle);
            if(!MapData.shapes.contains(circle))
                MapData.shapes.remove(circle);
        }
        connectAll();
    }
    
    /**
     * Remove all lines, the name, and the circle from the canvas.
     */
    public void removeMetroLine(){
        removeAllLine();
        
        if(MapData.shapes.contains(name1)){
            MapData.shapes.remove(name1);
        }
        if(MapData.shapes.contains(name2)){
            MapData.shapes.remove(name2);
        }
        if(MapData.shapes.remove(circle)){
            MapData.shapes.remove(circle);
        }
        // Make sure to remove this.
        if(MapData.metroLines.contains(this)){
            MapData.metroLines.remove(this);
        }
    }
    
    /**
     * When you edit your line, you change the name and color
     * @param name
     *  The name of the line
     * @param color 
     *  The color of the line
     */
    public void setNameAndColor(String name, Color color, boolean circular){
        // Make sure that the name and color are not the same as before
        if(this.lineName.equals(name) && this.lineColor.equals(color) && this.circular==circular)
            return;
        
        if(this.circular != circular)
            setCircular(circular);
        
        this.lineName = name;
        this.lineColor = color;
        
        name1.setText(name);
        name2.setText(name);
        
//        name1.setFill(color);
//        name2.setFill(color);
//        circle.setFill(color);
        
        for(Node n: this){
            if(n instanceof Line)
                ((Line)n).setStroke(color);
        }
        connectText();
    }
    
    public String getName(){
        return this.lineName;
    }
    public Color getColor(){
        return this.lineColor;
    }   
    
        // Adds anywhere in the line
//    public void addStation(Node node, DraggableStation station){
//        for(int i=0; i<this.size(); i++){
//            if(this.get(i)==node){
//                this.add(i+1, station);
//                stationNames.add(station.getLabel());
//                connectAll();
//            }
//        }
//    }
    
    
    public void addStationFile(DraggableStation station){
        if(this.get(this.size()-1) instanceof DraggableText || this.get(this.size()-1) instanceof DraggableCircle){
            this.add(this.size()-2, station);
        }
        else
            this.add(station);
        stationNames.add(station.getLabel());
    }
    /**
     * Adds the station to the linked list
     * @param station 
     *  The station to be addde to the linked list. (This object)
     * @return 
     *  true if successful, false o.w.
     */
    public boolean addStation(DraggableStation station){
        if(this.contains(station)){
            AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
            dialog.show("Station Exist", "Station already in this line", "Station is already in this line");
            return false;
        }
        if(this.get(this.size()-1) instanceof DraggableText || this.get(this.size()-1) instanceof DraggableCircle){
            this.add(this.size()-2, station);
        }
        else
            this.add(station);
        stationNames.add(station.getLabel());
        
        // Make sure to change from non circular to circular
        if(circular && stationNames.size() == 3)
            changeCircular();
        
        connectAll();
        return true;
    }
    
    public DraggableText getName1(){
        return name1;
    }
    
    public DraggableText getName2(){
        return name2;
    }
    
    public String getType(){
        return LINE;
    }
    
    public boolean getCircular(){
        return this.circular;
    }
    
    public LinkedList<DraggableText> getStations(){
        return this.stationNames;
    }
    
    public double getLineThickness(){
        return this.lineThickness;
    }
    
    public void setLineThickness(double num){
        this.lineThickness = num;
        connectAll();
    }
    
    public double getName1X(){
        return name1.getX();
    }
    
    public double getName1Y(){
        return name1.getY();
    }
    
    public void setName1Location(double x, double y){
        name1.setX(x);
        name1.setY(y);
    }
        
    public double getName2X(){
        return name2.getX();
    }
    
    public double getName2Y(){
        return name2.getY();
    }
    
    public void setName2Location(double x, double y){
        name2.setX(x);
        name2.setY(y);
    }
    
    public void setCircular(boolean flag){
        if(this.circular != flag){
            this.circular = flag;
            connectAll();
            
            if(circular && stationNames.size() > 2)
                makeCircular();
            else if(!circular)
                makeNotCircular();
        }
    }
}