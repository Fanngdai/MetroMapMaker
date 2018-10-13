package metroMapMaker.file;

import metroMapMaker.data.Draggable;
import metroMapMaker.data.MapData;
import metroMapMaker.data.DraggableImage;
import metroMapMaker.data.DraggableText;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import djf.components.AppDataComponent;
import djf.components.AppFileComponent;
import djf.ui.AppAlertDialogSingleton;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import static metroMapMaker.data.Draggable.TEXT;
import static metroMapMaker.data.Draggable.IMAGE;
import metroMapMaker.data.DraggableStation;
import metroMapMaker.data.MetroLine;

/**
 * This class serves as the file management component for this application,
 * providing all I/O services.
 *
 * @author Richard McKenna
 * @author Fanng Dai
 * @version 1.0
 */

public class MapFiles implements AppFileComponent {
    static final String NAME = "name";
    
    // FOR JSON LOADING
    static final String JSON_BACKGROUND_COLOR = "background_color";
    static final String JSON_BACKGROUND_IMAGE = "background_image";
    static final String JSON_RED = "red";
    static final String JSON_GREEN = "green";
    static final String JSON_BLUE = "blue";
    static final String JSON_ALPHA = "alpha";
    static final String JSON_SHAPES = "shapes";
    static final String JSON_TYPE = "type";
    static final String JSON_X = "x";
    static final String JSON_Y = "y";
    static final String JSON_WIDTH = "width";
    static final String JSON_HEIGHT = "height";
    static final String JSON_FILL_COLOR = "fill_color";
    
    // For shapes
    static final String JSON_IMAGE = "image";
    static final String JSON_TEXT = "text";
    static final String JSON_FAMILY = "family";
    static final String JSON_SIZE = "size";
    static final String JSON_BOLD = "bold";
    static final String JSON_ITALICS = "italics";
    
    // For metro lines
    static final String LINES = "lines";
    static final String CIRCULAR = "circular";
    static final String COLOR = "color";
    static final String STATION_NAMES = "station_names";
    static final String LINE_THICKENSS = "line_thickness";
    static final String NAME1X = "name1x";
    static final String NAME1Y = "name1y";
    static final String NAME2X = "name2x";
    static final String NAME2Y = "name2y";
    
    // for stations color & radii
    static final String STATIONS = "stations";
    static final String LABEL_POSITION = "label_position";
    static final String LABEL_ROTATE = "label_rotate";
    
    static final String STATION_RADII = "station_radii";
    
    // map size
    static final String MAP_SIZE = "map_size";
    
 
    /**
     * This method is for saving user work, which in the case of this
     * application means the data that together draws the logo.
     * 
     * @param data The data management component for this application.
     * 
     * @param filePath Path (including file name/extension) to where
     * to save the data to.
     * 
     * @throws IOException Thrown should there be an error writing 
     * out data to the file.
     */
    @Override
    public void saveData(AppDataComponent data, String filePath) throws IOException {
	// GET THE DATA
	MapData dataManager = (MapData)data;
        dataManager.nothingClicked();

	// NOW BUILD THE JSON OBJCTS TO SAVE
	JsonArrayBuilder shapeBuilder = Json.createArrayBuilder();
        ArrayList<MetroLine> metroLines = dataManager.getLines();
        ArrayList<DraggableStation> stations = dataManager.getStations();
	ObservableList<Node> shapes = dataManager.getShapes();
        
        JsonArrayBuilder lineBuilder = Json.createArrayBuilder();
        metroLines.forEach((MetroLine line) -> {
            String lineName = line.getName();
            boolean circular = line.getCircular();
            JsonObject fillColorJson = makeJsonColorObject(line.getColor());
            double lineThickness = line.getLineThickness();
            JsonObject text = makeJsonTextObject((Shape)line.getName1());

            JsonObject shapeJson = Json.createObjectBuilder()
                .add(NAME, lineName)                                    // Name of line
                .add(CIRCULAR, circular)                                // boolean. circular?
                .add(COLOR, fillColorJson)                              // color
                .add(STATION_NAMES, makeJsonStationNameObject(line))    // stations
                .add(LINE_THICKENSS, lineThickness)                     // thickness
                .add(NAME1X, line.getName1X())                          // position of text1x
                .add(NAME1Y, line.getName1Y())                          // position of text1y
                .add(NAME2X, line.getName2X())                          // position of text2x
                .add(NAME2Y, line.getName2Y())                          // position of text2y
                .add(JSON_TEXT, text)
                .build();

            lineBuilder.add(shapeJson);
        });
        
        JsonArrayBuilder stationBuilder = Json.createArrayBuilder();
        stations.forEach((DraggableStation station) -> {
            String type = station.getShapeType();
            double x = station.getX();
            double y = station.getY();
            JsonObject fillColorJson = makeJsonColorObject((Color)station.getFill());
            double radii = station.getRadius();
            double position = station.getPosition();
            double rotate = station.getLabel().getRotate();
            JsonObject text = makeJsonTextObject((Shape)station.getLabel());

            JsonObject shapeJson = Json.createObjectBuilder()
                .add(NAME, station.getName())               // Name of station
                .add(JSON_X, x)                             // x coordinate
                .add(JSON_Y, y)                             // y coordinate
                .add(JSON_FILL_COLOR, fillColorJson)        // color
                .add(STATION_RADII, radii)                  // radius
                .add(LABEL_POSITION, position)            // label position, left -> top -> right -> bottom
                .add(LABEL_ROTATE, rotate)                  // 90 degrees...
                .add(JSON_TEXT, text)
                .build();

            stationBuilder.add(shapeJson);
        });
        
        shapes.forEach((Node node) -> {
            if(node instanceof DraggableImage){         // IMAGE
                Shape shape = (Shape)node;
                Draggable draggableShape = ((Draggable)shape);
                String type = draggableShape.getShapeType();
                double x = draggableShape.getX();
                double y = draggableShape.getY();
                double width = draggableShape.getWidth();
                double height = draggableShape.getHeight();

                JsonObject shapeJson = Json.createObjectBuilder()
                    .add(JSON_TYPE, type)
                    .add(JSON_X, x)
                    .add(JSON_Y, y)
                    .add(JSON_WIDTH, width)
                    .add(JSON_HEIGHT, height)
                    .add(JSON_IMAGE, ((DraggableImage)node).getImagePath())
                    .build();
                
                shapeBuilder.add(shapeJson);
            } else if(dataManager.regularText(node)) {       // TEXT checks if it is a text. regular text.
                shapeBuilder.add(makeJsonTextObject((Shape)node));
            }
        });
        
        JsonArray lineArray = lineBuilder.build();
        JsonArray stationArray = stationBuilder.build();
        JsonArray shapesArray = shapeBuilder.build();
	
        String fileName = filePath.substring(filePath.lastIndexOf('/')+1, filePath.length());
        
        JsonObject mapSize = Json.createObjectBuilder()
            .add(JSON_X, dataManager.mapSizeX())
            .add(JSON_Y, dataManager.mapSizeY())
            .build();
        	
        JsonObject dataManagerJSO;
        
	// The background image
        if(dataManager.getBackgroundImagePath() != null) {
            String bgImagePath = dataManager.getBackgroundImagePath();
            
            // THEN PUT IT ALL TOGETHER IN A JsonObject
            dataManagerJSO = Json.createObjectBuilder()
                .add(NAME, fileName)
                .add(MAP_SIZE, mapSize)
                .add(JSON_BACKGROUND_IMAGE, bgImagePath)
                .add(LINES, lineArray)
                .add(STATIONS, stationArray)
                .add(JSON_SHAPES, shapesArray)
                .build();
        } else {        // The background color
            Color bgColor = dataManager.getBackgroundColor();
            JsonObject bgColorJson;
            
            // If there is not image nor color, make the color white.
            if(bgColor == null){
                bgColorJson = makeJsonColorObject(Color.WHITE);
            } else {
                bgColorJson = makeJsonColorObject(bgColor);
            }
            
            // THEN PUT IT ALL TOGETHER IN A JsonObject
            dataManagerJSO = Json.createObjectBuilder()
                .add(NAME, fileName)
                .add(MAP_SIZE, mapSize)
                .add(JSON_BACKGROUND_COLOR, bgColorJson)
                .add(LINES, lineArray)
                .add(STATIONS, stationArray)
                .add(JSON_SHAPES, shapesArray)
                .build();
        }
	
	// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
	Map<String, Object> properties = new HashMap<>(1);
	properties.put(JsonGenerator.PRETTY_PRINTING, true);
	JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
	StringWriter sw = new StringWriter();
	JsonWriter jsonWriter = writerFactory.createWriter(sw);
	jsonWriter.writeObject(dataManagerJSO);
	jsonWriter.close();

	// INIT THE WRITER
	OutputStream os = new FileOutputStream(filePath);
	JsonWriter jsonFileWriter = Json.createWriter(os);
	jsonFileWriter.writeObject(dataManagerJSO);
	String prettyPrinted = sw.toString();
	PrintWriter pw = new PrintWriter(filePath);
	pw.write(prettyPrinted);
	pw.close();
    }
    
    private JsonObject makeJsonColorObject(Color color) {
	JsonObject colorJson = Json.createObjectBuilder()
		.add(JSON_RED, color.getRed())
		.add(JSON_GREEN, color.getGreen())
		.add(JSON_BLUE, color.getBlue())
		.add(JSON_ALPHA, color.getOpacity()).build();
	return colorJson;
    }
    
    private JsonArrayBuilder makeJsonStationNameObject(MetroLine line) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        LinkedList<DraggableText> dragText = line.getStations();

        for(int i=0; i<dragText.size(); i++){
            arrayBuilder.add(dragText.get(i).getText());
        }

        return arrayBuilder;
    }
    
    private JsonObject makeJsonTextObject(Shape node){
        Shape shape = node;
        Draggable draggableShape = ((Draggable)shape);
        String type = draggableShape.getShapeType();
        double x = draggableShape.getX();
        double y = draggableShape.getY();
        JsonObject fillColorJson = makeJsonColorObject((Color)shape.getFill());

        JsonObject shapeJson = Json.createObjectBuilder()
            .add(JSON_TYPE, type)
            .add(JSON_X, x)
            .add(JSON_Y, y)
            .add(JSON_FAMILY, ((DraggableText)node).getFont().getFamily())
            .add(JSON_SIZE, ((DraggableText)node).getFont().getSize())
            .add(JSON_BOLD, ((DraggableText)node).getTextBold())
            .add(JSON_ITALICS, ((DraggableText)node).getTextItalics())
            .add(JSON_TEXT, ((DraggableText)node).getText())
            .add(JSON_FILL_COLOR, fillColorJson)
            .build();
        
        return shapeJson;
    }
      
    /**
     * This method loads data from a JSON formatted file into the data 
     * management component and then forces the updating of the workspace
     * such that the user may edit the data.
     * 
     * @param data Data management component where we'll load the file into.
     * 
     * @param filePath Path (including file name/extension) to where
     * to load the data from.
     */
    @Override
    public void loadData(AppDataComponent data, String filePath) {
        MapData dataManager = (MapData)data;
        try{
	// CLEAR THE OLD DATA OUT
        dataManager.resetData();
	
        // LOAD THE JSON FILE WITH ALL THE DATA
	JsonObject json = loadJSONFile(filePath);
        if(json.containsKey(JSON_BACKGROUND_COLOR)){
            Color bgColor = loadColor(json, JSON_BACKGROUND_COLOR);
            dataManager.setBackgroundColor(bgColor);
        } else if(json.containsKey(JSON_BACKGROUND_IMAGE)) {
            String bgImage = json.getString(JSON_BACKGROUND_IMAGE);
            BufferedImage bufferedImage = ImageIO.read(new File(bgImage));
            dataManager.setBackgroundImage(bufferedImage, bgImage);
        }
        
        if(json.containsKey(MAP_SIZE)){
            JsonObject mapSize = json.getJsonObject(MAP_SIZE);
            double x = getDataAsDouble(mapSize, JSON_X);
            double y = getDataAsDouble(mapSize, JSON_Y);
            dataManager.setMapSize(x, y);
        }
        
        // Make sure the json file has this shape
        if(json.containsKey(JSON_SHAPES)){
            // AND NOW LOAD ALL THE SHAPES
            JsonArray jsonShapeArray = json.getJsonArray(JSON_SHAPES);
            for (int i = 0; i < jsonShapeArray.size(); i++) {
                JsonObject jsonShape = jsonShapeArray.getJsonObject(i);
                String type = jsonShape.getString(JSON_TYPE);

                switch (type) {
                    case IMAGE:
                        {
                            Node shape = loadImage(jsonShape);
                            dataManager.addShape(shape);
                            break;
                        }
                    case TEXT:
                        {
                            Node shape = loadText(jsonShape);
                            // Make sure that loading the text is fine
                            if(shape != null)
                                dataManager.addShape(shape);
                            break;
                        }
                    default:
                        throw new IOException();
                }
            }
        }
        
        if(json.containsKey(STATIONS)){
            // Load the stations
            JsonArray jsonStationArray = json.getJsonArray(STATIONS);
            for (int i = 0; i < jsonStationArray.size(); i++) {
                JsonObject jsonStation = jsonStationArray.getJsonObject(i);
                DraggableStation station = loadStation(jsonStation);
                dataManager.addStationToCanvas(station);
            }
        }
        
        if(json.containsKey(LINES)){
            // Load the lines last.
            JsonArray jsonLineArray = json.getJsonArray(LINES);
            for (int i = 0; i < jsonLineArray.size(); i++) {
                JsonObject jsonLine = jsonLineArray.getJsonObject(i);
                loadLine(data, jsonLine);       // Load the line
            }
        }
        } catch(IOException ex){
            AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
            dialog.show("File cannot be loaded", "File Format Wrong", "Something wrong with the format. I cannot read it... Sad...");
        } catch(JsonParsingException ex){
            AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
            dialog.show("File cannot be loaded", "File Format Wrong", "Something wrong with the format. I cannot read it... Sad... I have made you a file under this name. If you wish to to save, note that this file will override the previous file!");
        } finally {
            dataManager.sort();
            dataManager.unhighlightShape();
            dataManager.nothingClicked();
            dataManager.nothingSelected();
        }
    }
    
    private Color loadColor(JsonObject json, String colorToGet) {
	JsonObject jsonColor = json.getJsonObject(colorToGet);
	double red = getDataAsDouble(jsonColor, JSON_RED);
	double green = getDataAsDouble(jsonColor, JSON_GREEN);
	double blue = getDataAsDouble(jsonColor, JSON_BLUE);
	double alpha = getDataAsDouble(jsonColor, JSON_ALPHA);
	Color loadedColor = new Color(red, green, blue, alpha);
	return loadedColor;
    }
    
    // HELPER METHOD FOR LOADING DATA FROM A JSON FORMAT
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
	InputStream is = new FileInputStream(jsonFilePath);
	JsonReader jsonReader = Json.createReader(is);
	JsonObject json = jsonReader.readObject();
	jsonReader.close();
	is.close();
	return json;
    }
    
    private double getDataAsDouble(JsonObject json, String dataName) {
	JsonValue value = json.get(dataName);
	JsonNumber number = (JsonNumber)value;
	return number.bigDecimalValue().doubleValue();	
    }
    
    private Node loadText(JsonObject jsonShape){
        try{
        // FIRST BUILD THE PROPER SHAPE TYPE
        Shape shape = new DraggableText(jsonShape.getString(JSON_TEXT));
        
        // THEN LOAD ITS FILL AND OUTLINE PROPERTIES
	Color fillColor = loadColor(jsonShape, JSON_FILL_COLOR);
	shape.setFill(fillColor);
	
	// AND THEN ITS DRAGGABLE PROPERTIES
	double x = getDataAsDouble(jsonShape, JSON_X);
	double y = getDataAsDouble(jsonShape, JSON_Y);
        
        // Make sure they are in bound
        if(x < 0)
            x = 10;
        if(y < 0)
            y = 10;
        
	Draggable draggableShape = (Draggable)shape;
	((DraggableText)draggableShape).setLocation(x, y);
        
        if(jsonShape.containsKey(JSON_FAMILY))
            ((DraggableText)draggableShape).setFontFamily(jsonShape.getString(JSON_FAMILY));
        if(jsonShape.containsKey(JSON_SIZE))
            ((DraggableText)draggableShape).setFontSize(jsonShape.getInt(JSON_SIZE));
        if(jsonShape.containsKey(JSON_BOLD))
            ((DraggableText)draggableShape).setTextBold(jsonShape.getBoolean(JSON_BOLD));
        if(jsonShape.containsKey(JSON_ITALICS))
            ((DraggableText)draggableShape).setTextItalics(jsonShape.getBoolean(JSON_ITALICS));
        
        ((DraggableText)draggableShape).setTextFont();
	// ALL DONE, RETURN IT
	return shape;
        } catch(Exception ex){
            return null;
        }
    }
    
    private Node loadImage(JsonObject jsonShape) throws IOException{
        // FIRST BUILD THE PROPER SHAPE TYPE
        Shape shape = new DraggableImage();

        String imagePath = jsonShape.getString(JSON_IMAGE);
        BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
        ((DraggableImage)shape).setImagePath(imagePath);
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);

        shape.setFill(new ImagePattern(image));
        ((DraggableImage)shape).setImagePath(imagePath);

        // AND THEN ITS DRAGGABLE PROPERTIES
        double x = getDataAsDouble(jsonShape, JSON_X);
        double y = getDataAsDouble(jsonShape, JSON_Y);
        
        if(x < 0)
            x = 10;
        if(y < 0)
            y = 10;
        
        double width = getDataAsDouble(jsonShape, JSON_WIDTH);
        double height = getDataAsDouble(jsonShape, JSON_HEIGHT);
        Draggable draggableShape = (Draggable)shape;
        draggableShape.setLocationAndSize(x, y, width, height);

        // ALL DONE, RETURN IT
        return shape;
    }
    
    private DraggableStation loadStation(JsonObject jsonShape) throws IOException{
        String name = jsonShape.getString(NAME);
        // FIRST BUILD THE PROPER SHAPE TYPE
        DraggableStation shape = new DraggableStation(name);
        
        double x = getDataAsDouble(jsonShape, JSON_X);
        double y = getDataAsDouble(jsonShape, JSON_Y);
        if(x < 0)
            x = 10;
        if(y < 0)
            y = 10;
        shape.setLocation(x,y);
        
        if(jsonShape.containsKey(LABEL_POSITION)){
            double position = getDataAsDouble(jsonShape, LABEL_POSITION);
            shape.setPosition((int)position);
        }
        
        if(jsonShape.containsKey(LABEL_ROTATE)){
            double rotate = getDataAsDouble(jsonShape, LABEL_ROTATE);
            shape.rotateText((int)rotate);
        }
        
        shape.moveTextPosition();
        
        if(jsonShape.containsKey(STATION_RADII)){
            double radius = getDataAsDouble(jsonShape, STATION_RADII);
            shape.setRadius(radius);
        }
        if(jsonShape.containsKey(JSON_FILL_COLOR)){
            Color fillColor = loadColor(jsonShape, JSON_FILL_COLOR);
            shape.setFill(fillColor);
        }
        if(jsonShape.containsKey(JSON_TEXT)){
            JsonObject text = jsonShape.getJsonObject(JSON_TEXT);
            DraggableText label = shape.getLabel();
            if(text.containsKey(JSON_FAMILY))
                label.setFontFamily(text.getString(JSON_FAMILY));
            if(text.containsKey(JSON_SIZE))
                label.setFontSize(text.getInt(JSON_SIZE));
            if(text.containsKey(JSON_BOLD))
                label.setTextBold(text.getBoolean(JSON_BOLD));
            if(text.containsKey(JSON_ITALICS))
                label.setTextItalics(text.getBoolean(JSON_ITALICS));
            if(text.containsKey(JSON_FILL_COLOR)){
                Color fillColor = loadColor(text, JSON_FILL_COLOR);
                    label.setFill(fillColor);
            }
            label.setTextFont();
        }

        // ALL DONE, RETURN IT
        return shape;   
    }
        
    private void loadLine(AppDataComponent data, JsonObject jsonShape) throws IOException{
        MapData dataManager = (MapData)data;
        
        String name = jsonShape.getString(NAME);
        boolean circular = jsonShape.getBoolean(CIRCULAR);
        Color fillColor = loadColor(jsonShape, COLOR);
        
        // FIRST BUILD THE PROPER SHAPE TYPE
        MetroLine line = new MetroLine(name, fillColor);
        line.setCircular(circular);
        
        if(jsonShape.containsKey(LINE_THICKENSS)){
            double lineThickness = getDataAsDouble(jsonShape, LINE_THICKENSS);
            line.setLineThickness(lineThickness);
        }
        
        
        JsonArray json = jsonShape.getJsonArray(STATION_NAMES);
        for(int i=0; i<json.size(); i++) {
            line.addStationFile(dataManager.findMetroStation(json.getString(i)));
        }
        
        // Set the default end point
        if(!json.isEmpty()){
            line.setName1Location(dataManager.findMetroStation(json.getString(0)).getCenterX(), dataManager.findMetroStation(json.getString(0)).getCenterY());
            line.setName2Location(dataManager.findMetroStation(json.getString(json.size()-1)).getCenterX(), dataManager.findMetroStation(json.getString(json.size()-1)).getCenterY());
        }
            
        // The text
        if(!circular && jsonShape.containsKey(NAME1X)){
            double onex = getDataAsDouble(jsonShape, NAME1X);
            double oney = getDataAsDouble(jsonShape, NAME1Y);
            double twox = getDataAsDouble(jsonShape, NAME2X);
            double twoy = getDataAsDouble(jsonShape, NAME2Y);
            
            if(onex < 0)
                onex = 10;
            if(oney < 0)
                oney = 10;
            if(twox < 0)
                twox = 10;
            if(twoy < 0)
                twoy = 10;
            
            line.setName1Location(onex, oney);
            line.setName2Location(twox, twoy);
        }
        
        if(jsonShape.containsKey(JSON_TEXT)){
            JsonObject text = jsonShape.getJsonObject(JSON_TEXT);
            DraggableText label1 = line.getName1();
            DraggableText label2 = line.getName2();
            
            if(text.containsKey(JSON_FAMILY)){
                label1.setFontFamily(text.getString(JSON_FAMILY));
                label2.setFontFamily(text.getString(JSON_FAMILY));
            }
            if(text.containsKey(JSON_SIZE)){
                label1.setFontSize(text.getInt(JSON_SIZE));
                label2.setFontSize(text.getInt(JSON_SIZE));
            }
            if(text.containsKey(JSON_BOLD)){
                label1.setTextBold(text.getBoolean(JSON_BOLD));
                label2.setTextBold(text.getBoolean(JSON_BOLD));
            }
            if(text.containsKey(JSON_ITALICS)){
                label1.setTextItalics(text.getBoolean(JSON_ITALICS));
                label2.setTextItalics(text.getBoolean(JSON_ITALICS));
            }
            if(text.containsKey(JSON_FILL_COLOR)){
                Color fColor = loadColor(text, JSON_FILL_COLOR);
                label1.setFill(fColor);
                label2.setFill(fColor);
            }
            
            label1.setTextFont();
            label2.setTextFont();
        }
        
        // Connect All station...
        dataManager.addLineToCanvas(line);
        line.connectAll();
    }
    
    /**
     * This method is provided to satisfy the compiler, but it
     * is not used by this application.
     */
    @Override
    public void exportData(AppDataComponent data, String filePath) throws IOException {
        // WE ARE NOT USING THIS, THOUGH PERHAPS WE COULD FOR EXPORTING
        // IMAGES TO VARIOUS FORMATS, SOMETHING OUT OF THE SCOPE
        // OF THIS ASSIGNMENT
    }
    
    /**
     * This method is provided to satisfy the compiler, but it
     * is not used by this application.
     */
    @Override
    public void importData(AppDataComponent data, String filePath) throws IOException {
	// AGAIN, WE ARE NOT USING THIS IN THIS ASSIGNMENT
    }
}
