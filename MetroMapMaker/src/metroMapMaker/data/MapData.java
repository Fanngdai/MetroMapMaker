package metroMapMaker.data;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import static metroMapMaker.data.MapState.SELECTING_SHAPE;
import metroMapMaker.gui.MapWorkspace;
import djf.components.AppDataComponent;
import djf.AppTemplate;
import djf.ui.AppAlertDialogSingleton;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import jtps.jTPS;
import metroMapMaker.gui.ScrolllPane;
import metroMapMaker.transactions.AddStationToLineT;
import metroMapMaker.transactions.AddT;
import metroMapMaker.transactions.ColorT;
import metroMapMaker.transactions.MoveStationLabelT;
import metroMapMaker.transactions.RemoveLineT;
import metroMapMaker.transactions.RemoveStationFromLineT;
import metroMapMaker.transactions.RotateStationLabelT;

/**
 * This class serves as the data management component for this application.
 *
 * @author Richard McKenna
 * @author Fanng Dai
 * @version 1.0
 */

public class MapData implements AppDataComponent {
    // FIRST THE THINGS THAT HAVE TO BE SAVED TO FILES
    
    // THESE ARE THE SHAPES PLACED ON THE CANVAS.
    public static ObservableList<Node> shapes;
    public static ArrayList<DraggableStation> stations = new ArrayList<>();
    public static ArrayList<MetroLine> metroLines = new ArrayList<>();
    
    // THE BACKGROUND COLOR
    Color backgroundColor;
    
    // AND NOW THE EDITING DATA

    // THIS IS THE SHAPE CURRENTLY BEING SIZED BUT NOT YET ADDED
    Node newShape;

    // THIS IS THE SHAPE CURRENTLY SELECTED
    Node selectedShape;
    public DraggableStation selectedStation;
    public MetroLine selectedMetroLine;

    // FOR FILL AND OUTLINE
    Color currentFillColor;
    Color currentOutlineColor;
    double currentBorderWidth;

    // CURRENT STATE OF THE APP
    MapState state;

    // THIS IS A SHARED REFERENCE TO THE APPLICATION
    AppTemplate app;
    
    // USE THIS WHEN THE SHAPE IS SELECTED
    Effect highlightedEffect;
    
    private String backgroundImagePath;
    private BufferedImage buffImage;
    private String imagePath;

    public static final String WHITE_HEX = "#FFFFFF";
    public static final String BLACK_HEX = "#000000";
    public static final String YELLOW_HEX = "#EEEE00";
    public static final Paint DEFAULT_BACKGROUND_COLOR = Paint.valueOf(WHITE_HEX);
    public static final Paint HIGHLIGHTED_COLOR = Paint.valueOf(YELLOW_HEX);
    public static final int HIGHLIGHTED_STROKE_THICKNESS = 3;

    static jTPS jtps;

    /**
     * This constructor creates the data manager and sets up the
     *
     *
     * @param initApp The application within which this data manager is serving.
     */
    public MapData(AppTemplate initApp) {
	// KEEP THE APP FOR LATER
	app = initApp;

	// NO SHAPE STARTS OUT AS SELECTED
	newShape = null;
	selectedShape = null;

	// INIT THE COLORS
	currentFillColor = Color.web(WHITE_HEX);
	currentOutlineColor = Color.web(BLACK_HEX);
	currentBorderWidth = 1;
        
        backgroundColor = Color.WHITE;
	
	// THIS IS FOR THE SELECTED SHAPE
	DropShadow dropShadowEffect = new DropShadow();
	dropShadowEffect.setOffsetX(0.0f);
	dropShadowEffect.setOffsetY(0.0f);
	dropShadowEffect.setSpread(1.0);
	dropShadowEffect.setColor(Color.YELLOW);
	dropShadowEffect.setBlurType(BlurType.GAUSSIAN);
	dropShadowEffect.setRadius(15);
	highlightedEffect = dropShadowEffect;
        
        jtps = new jTPS();
    }
    
    public void setBackgroundColor(Color initBackgroundColor) {
	MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        workspace.getBackGroundColorPicker().setValue(initBackgroundColor);
	Pane canvas = workspace.getCanvas();
	BackgroundFill fill = new BackgroundFill(initBackgroundColor, null, null);
	Background background = new Background(fill);
	canvas.setBackground(background);
        
        backgroundColor = initBackgroundColor;
        this.backgroundImagePath = null;
    }
    
    public void setBackgroundImage(BufferedImage buffer, String imagePath) {        
	MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
	Pane canvas = workspace.getCanvas();
        Image image = SwingFXUtils.toFXImage(buffer, null);
        BackgroundSize backgroundSize = new BackgroundSize(canvas.getWidth(), canvas.getHeight(), true, true, true, false);
	BackgroundImage myBI= new BackgroundImage(image,
        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
          backgroundSize);
	canvas.setBackground(new Background(myBI));
        
        // Used when saving file. Must save the background
        this.backgroundImagePath = imagePath;
        this.backgroundColor = null;
    }
    
    public void setCurrentFillColor(Color color) {
        currentFillColor = color;
        Color previousColor = null;
        if(selectedShape != null)
            previousColor = (Color)((Shape)selectedShape).getFill();

        if(!currentFillColor.equals(previousColor)){
            ColorT trans = new ColorT(selectedShape, previousColor);
            jtps.addTransaction(trans);
        }
    }
    
    /**
     * Adds each element
     * @param name
     * @param color 
     */
//    public void addLine(String name, Color color){
//        MetroLine m = new MetroLine(name, color);
//        metroLines.add(m);
//        
//        m.forEach((n) -> {
//            shapes.add(n);
//        });
//        
//        selectedMetroLine = m;
//        selectedShape = m.get(0);
//        unhighlightShape();
//    }
    
    /**
     * Adds station to the list.
     * @param name 
     *  The name of the station
     */
//    public void addStation(String name){
//        DraggableStation dragS = new DraggableStation(name);
//        stations.add(dragS);
//        shapes.add(dragS);
//        shapes.add(dragS.getLabel());
//        highlightShape(dragS);
//        highlightShape(dragS.getLabel());
//        selectedStation = dragS;
//    }
    
    /**
     * Removes station forever.
     * Removes the station form the line.
     */
//    public void removeStationForever(){
//        if(selectedStation == null)
//            return;
//        
//        // Station from all lines.
//        for(MetroLine line: metroLines){
//            if(line.contains(selectedStation)){
//                line.remove(selectedStation);
//                line.getStations().remove(selectedStation.getLabel());
//                
//                if(line.getCircular() && line.getStations().size() == 2)
//                    line.makeNotCircular();
//                            
//                line.connectAll();
//            }
//        }
//    }
    
    /**
     * Removes station forever.
     * @param name
     */
    public void removeStationForever(String name){
        DraggableStation station = findMetroStation(name);
        
        // Station from all lines.
        for(MetroLine line: metroLines){
            if(line.contains(station)){
                line.remove(station);
                line.stationNames.remove(station.getLabel());
                line.connectAll();
            }
        }
        
        // Remove from our list.
        stations.remove(station);
        shapes.remove(station);
        shapes.remove(station.getLabel());
    }
    
    /**
     * Removes station from line and map.
     */
    public void removeStationFromLine(){
        if(selectedStation == null || selectedMetroLine == null)
            return;
        if(selectedMetroLine.contains(selectedStation)){
            RemoveStationFromLineT trans = new RemoveStationFromLineT(selectedMetroLine, selectedStation);
            jtps.addTransaction(trans);
        }
    }
    
    /**
     * Removes station from line and map.
     * @param line
     *  The line to remove the station from
     * @param station
     *  The station to be removed from the line
     */
    public void removeStationFromLine(MetroLine line, DraggableStation station){        
        if(line.contains(station)){
            line.remove(station);
            line.stationNames.remove(station.getLabel());
            
            if(line.getCircular() && line.stationNames.size() == 2)
                line.makeNotCircular();
            
            line.connectAll();
        }
    }
    
    /**
     * Adds the station to the line.
     * @param x
     *  The x-coordinate of where user clicked
     * @param y
     *  The y-coordinate of where user clicked
     */
    public void addStationToLine(int x, int y){
        for(Node n: shapes){
            if(n.contains(x,y) && selectedMetroLine != null){
                if(n instanceof DraggableStation){
                    // Not able to add the station
                    if(!selectedMetroLine.addStation(selectedStation)){
                        app.getWorkspaceComponent().resetWorkspace();
                    } else {            // sucess
                        AddStationToLineT trans = new AddStationToLineT(app, selectedMetroLine, selectedStation);
                        jtps.addTransaction(trans);
                    }
                    return;
                } else if(n instanceof DraggableText && findStation((DraggableText)n) != null){
                    if(!selectedMetroLine.addStation(selectedStation)){
                        app.getWorkspaceComponent().resetWorkspace();
                    } else {            // sucess
                        AddStationToLineT trans = new AddStationToLineT(app, selectedMetroLine, selectedStation);
                        jtps.addTransaction(trans);
                    }
                    return;
                }
            }
        }
    }
    
    public void addText(String word){
        // unhiglight whatever shape was selected.
        unhighlightShape();
        
        if(word != null & !word.trim().equals("")){
            DraggableText dragText = new DraggableText(word);
            selectedShape = dragText;
            ((DraggableText)selectedShape).start(50,50);
            highlightShape();
            
            AddT trans = new AddT(app, dragText);
            jtps.addTransaction(trans);
        }
    }
    
    public void setImage(BufferedImage buffImage, String imagePath){
        this.buffImage = buffImage;
        this.imagePath = imagePath;
        
        // just to make sure it's an image.
        Image image = SwingFXUtils.toFXImage(buffImage, null);
    }
    
    public void startImage(int x, int y) {
        
        if(this.buffImage == null || this.imagePath == null) {
            AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
            dialog.show("No image", "No Image", "Try Again!");
            return;
        }
        
        DraggableImage newImage = new DraggableImage();
        newImage.start(x, y);
        newShape = newImage;
        
        unhighlightShape();
        
        selectedShape = newImage;

        Image image = SwingFXUtils.toFXImage(buffImage, null);
        ((DraggableImage)newImage).setFill(new ImagePattern(image));
        ((DraggableImage)newImage).setImagePath(this.imagePath);

        // ADD THE SHAPE TO THE CANVAS
        AddT trans = new AddT(app, newImage);
        jtps.addTransaction(trans);

        state = MapState.ADD_IMAGE;
        
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        ScrolllPane scrollPane = workspace.getScrollPane();
        if(scrollPane.gridEnabled()){
            scrollPane.redrawGrid();
        }
    }
    
    public void selectSizedShape() {
	selectedShape = newShape;
	highlightShape();
	newShape = null;
        // GO INTO SHAPE SIZING MODE
	state = MapState.ADD_IMAGE;
    }
    
    public void removeShape(Node shapeToRemove){
	shapes.remove(shapeToRemove);
    }
    
    public void removeSelectedShape() {
	if (selectedShape != null) {
	    shapes.remove(selectedShape);
	    selectedShape = null;
	}
    }

    /**
     * Un-highlight every node just in case
     */
    public void unhighlightShape() {
        nothingClicked();
        for(Node n: shapes){
            n.setEffect(null);
        }
        selectedShape = null;
    }
    
    public void highlightShape() {
        if(selectedShape != null && !(selectedShape instanceof Pane))
            selectedShape.setEffect(highlightedEffect);
    }
    
    public void highlightLine(MetroLine l){
        for(Node a: shapes)
            if(l.contains(a))
                a.setEffect(highlightedEffect);
        MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
        workspace.setLineColor(l.getColor());
    }
    
    public void highlightShape(Node shape){
        if(shape instanceof Pane)
            return;
        if(selectedShape != shape){
//            previousShape = selectedShape;
            selectedShape = shape;
        }
        shape.setEffect(highlightedEffect);
    }
    
    public void moveSelectedShapeToBack() {
	if (selectedShape != null) {
	    shapes.remove(selectedShape);
	    if (shapes.isEmpty()) {
		shapes.add(selectedShape);
	    }
	    else {
		ArrayList<Node> temp = new ArrayList<>();
		temp.add(selectedShape);
                shapes.forEach((node) -> {
                    temp.add(node);
                });
		shapes.clear();
                temp.forEach((node) -> {
                    shapes.add(node);
                });
	    }
	}
    }
    
    public void moveSelectedShapeToFront() {
	if (selectedShape != null) {
	    shapes.remove(selectedShape);
	    shapes.add(selectedShape);
	}
    }
    
    /**
     * No draggable circles on the screen
     */
    public void nothingClicked(){        
        DraggableCircle circle = null;
        for(Node n: shapes){
            if(n instanceof DraggableCircle){
                circle = (DraggableCircle)n;
            }
        }
        if(circle != null)
            circle.metroLine.connectText();
        sort();
    }
    
    public void nothingSelected(){
        MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
        workspace.nothingSelected();
    }
    /**
     * image -> line -> text -> circle/stations
     */
    public void sort(){    
        // Everything that should go in shapes first goes here first
        LinkedList<Node> temp = new LinkedList<>();
        // Lines
        LinkedList<Node> line = new LinkedList<>();
        // Text
        LinkedList<Node> text = new LinkedList<>();
        // Stations/drag circle
        LinkedList<Node> circle = new LinkedList<>();
        
        for(Node n: shapes){
            if(n instanceof Circle)
                circle.add(n);
            else if(n instanceof Text)
                text.add(n);
            else if(n instanceof Line)
                line.add(n);
            else 
                temp.add(n);
        }
        
        shapes.clear();
        for(Node n: temp)
            shapes.add(n);
        for(Node n: line)
            shapes.add(n);
        for(Node n: text)
            shapes.add(n);
        for(Node n: circle)
            shapes.add(n);
    }
    
    public MetroLine findMetroLine(Node l){
        for(MetroLine node: metroLines){
            if(node.contains(l))
                return node;
        }
        return null;
    }
    
    public MetroLine findMetroLine(String name){
        unhighlightShape();
        for(MetroLine node: metroLines){
            if(node.getName().equalsIgnoreCase(name)){
                
                lineFound:
                for(int i = 0; i < node.size(); i++){
                    if((node.get(i) instanceof Line)){
                        selectedShape = node.get(i);
                        break lineFound;
                    }
                }
                highlightLine(node);
                selectedMetroLine = node;
                
                // Reload the controllers
                MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
                workspace.loadSelectedShapeSettings(selectedShape);
                
                return node;
            }
        }
        return null;
    }
    
    public DraggableStation findStation(DraggableText text){
        for(DraggableStation d: stations){
            if(d.getLabel() == text)
                return d;
        }
        return null;
    }
    
    /**
     * Checks if it is a regular text object and not associated with station nor line
     * @param node
     *  Node to check
     * @return 
     *  False if it is part of a metroline or station
     */
    public boolean regularText(Node node){
        if(!(node instanceof DraggableText))
            return false;
        if(findMetroLine(node) != null)
            return false;
        return findStation((DraggableText)node) == null;
    }
    
    public void removeLine(String name){
        MetroLine metroLine = findMetroLine(name);
        if(metroLine != null){
            RemoveLineT trans = new RemoveLineT(app, metroLine);
            jtps.addTransaction(trans);
        } else{
            AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
            dialog.show("Error Line not found", "Line was not found. Try Again", "Well that's odd...");
        }
    }
    
    public void removeLineTransaction(String name){
        MetroLine metroLine = findMetroLine(name);
        if(metroLine != null) {
            metroLines.remove(metroLine);
            metroLine.removeMetroLine();
            for(MetroLine l: metroLines)
            l.connectAll();
        }
    }

    public DraggableStation findMetroStation(String name){
        unhighlightShape();
        for(DraggableStation n: stations){
            if(n.getName().equalsIgnoreCase(name)){
                selectedStation = n;
                selectedShape = n;

                MapWorkspace workspace = (MapWorkspace) app.getWorkspaceComponent();
                workspace.loadSelectedShapeSettings(selectedStation);
                
                highlightShape(selectedStation);
                highlightShape(selectedStation.getLabel());
                
                return n;
            }
        }
        return null;
    }
    
    public Node selectTopShape(int x, int y) {
        Node shape = getTopShape(x, y);
        
        if(shape == null)
            return null;
        else if (shape == selectedShape && !(shape instanceof DraggableCircle) && findMetroLine(shape) == null) // Text not part of a metroLine
            return shape;       //         If it is a circle... it can be dragged...
        else if(shape instanceof DraggableCircle)
            return shape;
        
        nothingClicked();
        unhighlightShape();
        selectedShape = shape;
                
        // Check what type of text is clicked
        if(shape instanceof DraggableText){
            DraggableStation station = findStation((DraggableText)shape);
            MetroLine metroLine = findMetroLine(shape);
            if(station != null){                            // Station
                shape = station;
                selectedStation = station;
                selectedShape = station;
                highlightShape(station);
                highlightShape(selectedStation.getLabel());
            } else if(metroLine != null){                   // Part of a metro line         
                DraggableText text = (DraggableText) shape;
                // Remove text insert circle
                selectedMetroLine = text.getMetroLine();
                shape = text.getMetroLine().dragStartEnd(text);
                selectedShape = shape;
                // shape is now a circle
                ((DraggableCircle)shape).start(x, y);
                ((DraggableCircle)shape).drag(x, y);
            } else{
                // A normal text
            }
        } else if(shape instanceof DraggableStation){
            selectedStation = (DraggableStation)shape;
            highlightShape(selectedStation.getLabel());
        }
        
        if(shape instanceof Line){
            Line l = (Line) shape;
            selectedMetroLine = findMetroLine(l);
            if(selectedMetroLine == null)
                return null;
            highlightLine(selectedMetroLine);
            
            if(selectedMetroLine.get(0) instanceof DraggableText){
                selectedShape = selectedMetroLine.get(0);
            } else {
                selectedShape = selectedMetroLine.get(selectedMetroLine.size()-1);
            }
        }    
        if(shape != null) {
            MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
            workspace.loadSelectedShapeSettings(shape);
            highlightShape(shape);
            
            if(shape instanceof Circle)
                workspace.snapButtonOn();
            
            return shape;
        }
        return null;
    }
    
    public Node getTopShape(int x, int y) {
        sort();
	for (int i = shapes.size() - 1; i >= 0; i--) {
	    Node shape = shapes.get(i);
            // Get the object (text station) first
            if(shape.contains(x,y))
                return shape;
	}
	return null;
    }

    /**
     * This function clears out the HTML tree and reloads it with the minimal
     * tags, like html, head, and body such that the user can begin editing a
     * page.
     */
    @Override
    public void resetData() {
	setState(SELECTING_SHAPE);
        newShape = null;
	selectedShape = null;

	// INIT THE COLORS
	currentFillColor = Color.web(WHITE_HEX);
	currentOutlineColor = Color.web(BLACK_HEX);
        
        stations.clear();
        shapes.clear();
        metroLines.clear();
        jtps.resetJTPS();
        
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
        // Make sure nothing is enabled... That shouldn't be
        workspace.clearEditToolbar();
        Pane canvas = workspace.getCanvas();
        
        backgroundColor = Color.WHITE;
        backgroundImagePath = null;
        BackgroundFill fill = new BackgroundFill(Color.WHITE, null, null);
        Background background = new Background(fill);
        canvas.setBackground(background);
	
	shapes.clear();
	((MapWorkspace)app.getWorkspaceComponent()).getCanvas().getChildren().clear();
        // Clear the combo box.
        ((MapWorkspace)app.getWorkspaceComponent()).resetWorkspace();
    }
    
    public void addLineToCanvas(MetroLine line){
        ((MapWorkspace)app.getWorkspaceComponent()).addLineToCombo(line.getName());
        metroLines.add(line);
        line.connectAll();
    }
    
    public void addStationToCanvas(DraggableStation station){
        ((MapWorkspace)app.getWorkspaceComponent()).addStationToCombo(station.getName());
        stations.add(station);
        shapes.add(station);
        shapes.add(station.getLabel());
    }
    
    public void moveTextPosition(){
        if(selectedStation != null){
            MoveStationLabelT trans = new MoveStationLabelT(selectedStation);
            jtps.addTransaction(trans);
        }
    }
    
    public void rotateText(){
        if(selectedStation != null){
            RotateStationLabelT trans = new RotateStationLabelT(selectedStation);
            jtps.addTransaction(trans);
        }
    }
    
    /**
     * For loading json files.
     * @param shapeToAdd 
     *  The shape to be added to canvas
     */
    public void addShape(Node shapeToAdd) {
	shapes.add(shapeToAdd);
    }
    
    public boolean isInState(MapState testState) {
	return state == testState;
    }
        
    public Node getNewShape() {
	return newShape;
    }

    public Node getSelectedShape() {
	return selectedShape;
    }
    
//    public Node getPreviousShape(){
//        return previousShape;
//    }
    
    public MapState getState() {
	return state;
    }
    
    public ObservableList<Node> getShapes() {
	return shapes;
    }
    
    public ArrayList<MetroLine> getLines(){
        return this.metroLines;
    }
    
    public ArrayList<DraggableStation> getStations(){
        return this.stations;
    }

    public Color getBackgroundColor() {
	return backgroundColor;
    }
    
    public String getBackgroundImagePath() {
        return this.backgroundImagePath;
    }
    
    public Color getCurrentFillColor() {
	return currentFillColor;
    }

    public Color getCurrentOutlineColor() {
	return currentOutlineColor;
    }

    public double getCurrentBorderWidth() {
	return currentBorderWidth;
    }
    
    public jTPS getJTPS(){
        return MapData.jtps;
    }
    
    public void setShapes(ObservableList<Node> initShapes) {
	shapes = initShapes;
    }

    public void setState(MapState initState) {
	state = initState;
    }
    
    public void setSelectedShape(Node initSelectedShape) {
	selectedShape = initSelectedShape;
    }
    
    public double mapSizeX(){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
	Pane canvas = workspace.getCanvas();
        return canvas.getWidth();
    }
    
    public double mapSizeY(){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
	Pane canvas = workspace.getCanvas();
        return canvas.getHeight();
    }
    
    public void setMapSize(double x, double y){
        MapWorkspace workspace = (MapWorkspace)app.getWorkspaceComponent();
	Pane canvas = workspace.getCanvas();
        canvas.setPrefSize(x, y);
    }
    
    public double distanceFormula(double x2, double x1, double y2, double y1){
        double diffX = x2 - x1;
        double diffY = y2 - y1;
        
        diffX = diffX * diffX;
        diffY = diffY * diffY;
        
        return Math.sqrt(diffX + diffY);
    }
    
    /**
     * Copied code from online. Changes the first letter after a space to upper case
     * @param word
     * The word
     * @return 
     *  The Word Like This
     */
    public String capitalizer(String word){

        String[] words = word.split(" ");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0))).append(words[0].subSequence(1, words[0].length()).toString().toLowerCase());
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                sb.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].subSequence(1, words[i].length()).toString().toLowerCase());
            }
        }
        return  sb.toString();
    }
    
//    public void setCurrentFillColor(Color initColor) {
//	currentFillColor = initColor;
//	if (selectedShape != null)
//	    ((Shape)selectedShape).setFill(currentFillColor);
//    }
}
