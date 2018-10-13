package metroMapMaker.gui;

import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import metroMapMaker.data.MapData;
import static metroMapMaker.data.MapData.WHITE_HEX;
import djf.ui.AppGUI;
import djf.AppTemplate;
import djf.components.AppDataComponent;
import djf.components.AppWorkspaceComponent;
import static djf.settings.AppPropertyType.WELCOME_TITLE;
import djf.ui.AppAlertDialogSingleton;
import java.awt.AWTException;
import java.awt.Robot;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import javafx.stage.Stage;
import jtps.jTPS;
import static metroMapMaker.MapLanguageProperty.*;
import static metroMapMaker.css.MapStyle.*;
import metroMapMaker.data.DraggableCircle;
import metroMapMaker.data.DraggableImage;
import metroMapMaker.data.DraggableStation;
import metroMapMaker.data.DraggableText;
import metroMapMaker.data.MapState;
import metroMapMaker.data.MetroLine;
import metroMapMaker.transactions.RemoveStationT;
import metroMapMaker.transactions.StationColorT;
import properties_manager.PropertiesManager;

/**
 * This class serves as the workspace component for this application, providing
 * the user interface controls for editing work.
 *
 * @author Fanng Dai
 * @version 1.0
 */
public final class MapWorkspace extends AppWorkspaceComponent {
    // HERE'S THE APP
    private final AppTemplate app;

    // IT KNOWS THE GUI IT IS PLACED INSIDE
    private final AppGUI gui;

    // HAS ALL THE CONTROLS FOR EDITING
    private final VBox editToolbar = new VBox();
    private ScrollPane editToolbarScroll = new ScrollPane();
    
    // FIRST ROW
    private final VBox row1 = new VBox();
    private final HBox metroLine1 = new HBox();
    private Label metroLineLabel;
    public final ObservableList<String> metroLineObserList = FXCollections.observableArrayList();
    private final ComboBox<String> metroLineComboBox = new ComboBox<>(metroLineObserList);
    private Button metroLineEditLine;
    private final HBox metroLine2 = new HBox();
    private Button addMetroLine;
    private Button removeMetroLine;
    private Button addStationToLine;
    private Button removeStationFromLine;
    private Button showStationList;
    private Slider metroLineThickness;
    
    // SECOND ROW
    private final VBox row2 = new VBox();
    private final HBox metroStation1 = new HBox();
    private Label metroStationLabel;
    public ObservableList<String> metroStationObserList = FXCollections.observableArrayList();
    private final ComboBox<String> metroStationComboBox = new ComboBox<>(metroStationObserList);
    private ColorPicker metroStationColorPicker;
    private final HBox metroStation2 = new HBox();
    private Button addStation;
    private Button removeStation;
    private Button snapCircle;
    private Button moveLabelStation;
    private Button rotateLabelStation;
    private Slider stationCircleRadius;

    // THIRD ROW
    private final VBox routerFromToV = new VBox();
    private final ComboBox<String> from = new ComboBox<>(metroStationObserList);
    private final ComboBox<String> to = new ComboBox<>(metroStationObserList);
    private final HBox routerFromToH = new HBox();
    private Button findRoute;

    // FORTH ROW
    private final VBox row4 = new VBox();
    private final HBox decor1;
    private Label decorLabel;
    private ColorPicker backgroundColorPicker;
    private final HBox decor2;
    private Button setImageBackground;
    private Button addImage;
    private Button addLabel;
    private Button removeElement;
    
    // FIFTH ROW
    private final VBox row5  = new VBox();
    private final HBox font1;
    private Label fontLabel;
    private ColorPicker fontColorPicker;
    private final HBox font2;
    private Button boldText;
    private Button italicsText;
    private ComboBox<String> fontFamily;
    private ComboBox<Integer> fontSize;
        
    // SIXTH ROW
    private final VBox row6  = new VBox();
    private final HBox navigation1 = new HBox();
    private Label navigationLabel;
    private final CheckBox showGridCheck = new CheckBox();
    private final HBox navigation2;
    private Button zoomIn;
    private Button zoomOut;
    private Button increaseMapSize;
    private Button decreaseMapSize;
    
    // Variables I need for transaction
    int prevThickness;
    int prevRadius;
    
    // THIS IS WHERE WE'LL RENDER OUR DRAWING, NOTE THAT WE
    // CALL THIS A CANVAS, BUT IT'S REALLY JUST A Pane
    ScrolllPane scrollPane;
    Pane canvas; 
    MapData dataManager;
    
    // FOR DISPLAYING DEBUG STUFF
    Text debugText;
    
    private static jTPS jtps;

    /**
     * Constructor for initializing the workspace, note that this constructor
     * will fully setup the workspace user interface for use.
     *
     * @param initApp The application this workspace is part of.
     */
    public MapWorkspace(AppTemplate initApp) {
        this.navigation2 = new HBox();
        this.font2 = new HBox();
        this.font1 = new HBox();
        this.decor2 = new HBox();
        this.decor1 = new HBox();
	// KEEP THIS FOR LATER
	app = initApp;
        
	// KEEP THE GUI FOR LATER
	gui = app.getGUI();

        dataManager = (MapData)app.getDataComponent();
            
        // LAYOUT THE APP
        initLayout();
        
        // HOOK UP THE CONTROLLERS
        initControllers();
        
        // AND INIT THE STYLE FOR THE WORKSPACE
        initStyle();
    }
    
    /**
     * Note that this is for displaying text during development.
     * @param text
     */
    public void setDebugText(String text) {
	debugText.setText(text);
    }
    
    public ScrolllPane getScrollPane(){
        return this.scrollPane;
    }
    
    public ColorPicker getBackGroundColorPicker(){
        return backgroundColorPicker;
    }
    
    // ACCESSOR METHODS FOR COMPONENTS THAT EVENT HANDLERS
    // MAY NEED TO UPDATE OR ACCESS DATA FROM
    public Color getBackgroundColor() {
	return backgroundColorPicker.getValue();
    }
    
    public Color getMetroStationColor(){
        return metroStationColorPicker.getValue();
    }
    
    public Color getFontColor(){
        return fontColorPicker.getValue();
    }
    
    public Slider getLineThicknessSlider() {
	return metroLineThickness;
    }
    
    public Slider getCircleRadiuSlider(){
        return stationCircleRadius;
    }

    public Pane getCanvas() {
	return canvas;
    }
    
    public void snapButtonOn(){
        snapCircle.setDisable(false);
    }
    
    public String getMetroNameSelected(){
        return metroLineComboBox.getValue();
    }
    
    public MetroLine selectedLine(){
        if(metroLineComboBox.getValue() != null)
            return dataManager.findMetroLine(metroLineComboBox.getValue());
        return null;
    }
    
    public DraggableStation selectedStation(){
        if(metroStationComboBox.getValue() != null)
            return dataManager.findMetroStation(metroStationComboBox.getValue());
        return null;
    }
        
    // HELPER SETUP METHOD
    private void initLayout() {
	// ROW 1
        metroLineLabel = new Label("Metro Lines");
        metroLineComboBox.setDisable(true);
        metroLineComboBox.setPromptText("Lines");
        metroLineComboBox.setPrefSize(160,20);
        Region spacera = new Region();
        HBox.setHgrow(spacera, Priority.ALWAYS);
        metroLine1.getChildren().addAll(metroLineLabel, metroLineComboBox, spacera);
        metroLine1.setSpacing(10);
        metroLineEditLine = gui.initChildButton(metroLine1, EDIT_ICON.toString(), EDIT_METROLINE_TOOLTIP.toString(), true);
        
        addMetroLine = gui.initChildButton(metroLine2, PLUS_ICON.toString(), ADD_METROLINE_TOOLTIP.toString(), false);
        removeMetroLine = gui.initChildButton(metroLine2, MINUS_ICON.toString(), REMOVE_METROLINE_TOOLTIP.toString(), true);
        addStationToLine = gui.initChildButton(metroLine2, ADD_STATION_ICON.toString(), ADD_STATIONTOLINE_TOOLTIP.toString(), true);
        removeStationFromLine = gui.initChildButton(metroLine2, REMOVE_STATION_ICON.toString(), REMOVE_STATIONFROMLINE_TOOLTIP.toString(), true);
        showStationList = gui.initChildButton(metroLine2, SHOW_STATION_ICON.toString(), SHOW_STATION_TOOLTIP.toString(), true);
        metroLine2.setSpacing(9);
        metroLineThickness = new Slider(2, 15, 1);
        metroLineThickness.setDisable(true);
       
        row1.getChildren().addAll(metroLine1, metroLine2, metroLineThickness);
        
	// ROW 2
	metroStationLabel = new Label("Metro Stations");
        metroStationComboBox.setDisable(true);
        metroStationComboBox.setPromptText("Stations");
        metroStationComboBox.setPrefSize(160,20);
        metroStationColorPicker = new ColorPicker(Color.valueOf(WHITE_HEX));
        metroStationColorPicker.setDisable(true);
        Region spacerb = new Region();
        HBox.setHgrow(spacerb, Priority.ALWAYS);
        metroStation1.getChildren().addAll(metroStationLabel, metroStationComboBox, spacerb, metroStationColorPicker);
        metroStation1.setSpacing(10);
                
        addStation = gui.initChildButton(metroStation2, PLUS_ICON.toString(), ADD_STATION_TOOLTIP.toString(), false);
        removeStation = gui.initChildButton(metroStation2, MINUS_ICON.toString(), REMOVE_STATION_TOOLTIP.toString(), true);
        snapCircle = gui.initChildButton(metroStation2, SNAP_ICON.toString(), SNAP_STATION_TOOLTIP.toString(), true);
        moveLabelStation = gui.initChildButton(metroStation2, MOVE_ICON.toString(), MOVE_LABEL_TOOLTIP.toString(), true);
        rotateLabelStation = gui.initChildButton(metroStation2,ROTATE_ICON.toString(), ROTATE_STATION_TOOLTIP.toString(), true);
        metroStation2.setSpacing(9);
        
        stationCircleRadius = new Slider(2, 15, 1);
        stationCircleRadius.setDisable(true);
        
        row2.getChildren().addAll(metroStation1, metroStation2, stationCircleRadius);

        // ROW 3
        from.setPromptText("FROM");
        from.setDisable(true);
        from.setPrefSize(215,20);
        to.setPromptText("TO");
        to.setDisable(true);
        to.setPrefSize(215,20);
	routerFromToV.getChildren().addAll(from, to);
        routerFromToV.setSpacing(21);
        routerFromToH.getChildren().add(routerFromToV);
        findRoute = gui.initChildButton(routerFromToH, FIND_ROUTE_ICON.toString(), FIND_ROUTE_TOOLTIP.toString(), true);

	// ROW 4
	decorLabel = new Label("Decor");
        backgroundColorPicker = new ColorPicker(Color.valueOf(WHITE_HEX));
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        decor1.getChildren().addAll(decorLabel, spacer1, backgroundColorPicker);
        
        setImageBackground = gui.initChildButton(decor2, IMAGE_BACKGROUND_ICON.toString(), SET_IMAGE_BACKGROUND_TOOLTIP.toString(), false);
	addImage = gui.initChildButton(decor2, ADD_IMAGE_ICON.toString(), ADD_IMAGE_TOOLTIP.toString(), false);
        addLabel = gui.initChildButton(decor2, ADD_LABEL_ICON.toString(), ADD_LABEL_TOOLTIP.toString(), false);
        removeElement = gui.initChildButton(decor2, REMOVE_ELEMENT_ICON.toString(), REMOVE_ELEMENT_TOOLTIP.toString(), true);
        decor2.setSpacing(30);
        
        row4.getChildren().addAll(decor1, decor2);
        
	// ROW 5
	fontLabel = new Label("Font");
        fontColorPicker = new ColorPicker(Color.valueOf(WHITE_HEX));
        fontColorPicker.setDisable(true);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        font1.getChildren().addAll(fontLabel, spacer2, fontColorPicker);
        
        boldText = gui.initChildButton(font2, BOLD_ICON.toString(), BOLD_TOOLTIP.toString(), true);
        italicsText = gui.initChildButton(font2, ITALICS_ICON.toString(), ITALICS_TOOLTIP.toString(), true);
	
        ObservableList<Integer> fontSizee = FXCollections.observableArrayList(5,8,9,10,11,12,14,16,18,20,22,24,26,28,36,48,72,108);
        fontSize = new ComboBox<>(fontSizee);
        fontSize.setPromptText("Size");
        fontSize.setDisable(true);
        fontSize.setPrefSize(73,20);
      
        ObservableList<String> font = FXCollections.observableArrayList("Courier", "Dekko",
                "Helvetica", "Monospaced", "Sansserif", "Tahoma", "Times New Roman");
        fontFamily = new ComboBox<>(font);
        fontFamily.setPromptText("Font Family");
        fontFamily.setPrefSize(128,20);
        fontFamily.setDisable(true);
        
        font2.getChildren().addAll(fontSize, fontFamily);
        font2.setSpacing(5);
        
        row5.getChildren().addAll(font1, font2);
        
	// ROW 6
	navigationLabel = new Label("Navigation");
        showGridCheck.setText("Show Grid");
        Region spacer3 = new Region();
        HBox.setHgrow(spacer3, Priority.ALWAYS);
        navigation1.getChildren().addAll(navigationLabel, spacer3, showGridCheck);
        
        zoomIn = gui.initChildButton(navigation2, ZOOMIN_ICON.toString(), ZOOMIN_TOOLTIP.toString(), false);
	zoomOut = gui.initChildButton(navigation2, ZOOMOUT_ICON.toString(), ZOOMOUT_TOOLTIP.toString(), false);
        decreaseMapSize = gui.initChildButton(navigation2, DECREASE_MAP_SIZE_ICON.toString(), DECREASE_MAP_SIZE_TOOLTIP.toString(), false);
        increaseMapSize = gui.initChildButton(navigation2, INCREASE_MAP_SIZE_ICON.toString(), INCREASE_MAP_SIZE_TOOLTIP.toString(), false);
        navigation2.setSpacing(30);
        
        row6.getChildren().addAll(navigation1, navigation2);
        
	// NOW ORGANIZE THE EDIT TOOLBAR
	editToolbar.getChildren().addAll(row1, row2, routerFromToH, row4, row5, row6);
        editToolbarScroll = new ScrollPane();
        editToolbarScroll.setContent(editToolbar);
        editToolbarScroll.setMinWidth(410);
	
	// WE'LL RENDER OUR STUFF HERE IN THE CANVAS
	canvas = new Pane();
        // DELETE THIS SHIT LATER
//        canvas.setPrefSize(800, 800);
	debugText = new Text();
	canvas.getChildren().add(debugText);
        canvas.setStyle("-fx-border-color:#777777;-fx-border-width: 1px;");
        canvas.setEffect(new InnerShadow(BlurType.THREE_PASS_BOX, new Color(0,0,0,.4),20,0,0,0));

        BackgroundFill fill = new BackgroundFill(Color.WHITE, null, null);
        Background background = new Background(fill);
        canvas.setBackground(background);
        
	debugText.setX(100);
	debugText.setY(100);
	
	// AND MAKE SURE THE DATA MANAGER IS IN SYNC WITH THE PANE
	MapData data = (MapData)app.getDataComponent();
	data.setShapes(canvas.getChildren());

        // To make sure no shapes go over the toolbars
        final Rectangle outputClip = new Rectangle();
        canvas.setClip(outputClip);

        canvas.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            outputClip.setWidth(newValue.getWidth());
            outputClip.setHeight(newValue.getHeight());
        });
        
        scrollPane = new ScrolllPane(app, canvas);
//        BorderPane.setAlignment(scrollPane, Pos.CENTER);
        
	// AND NOW SETUP THE WORKSPACE
	workspace = new BorderPane();
	((BorderPane)workspace).setLeft(editToolbarScroll);
	((BorderPane)workspace).setCenter(scrollPane);
    }
    
    
    // HELPER SETUP METHOD
    private void initControllers() {
	// MAKE THE EDIT CONTROLLER
	MapController controller = new MapController(app);
        jtps = dataManager.getJTPS();
        
        Button undoButton = gui.getUndoButton();
        Button redoButton = gui.getRedoButton();
        Button saveButton = gui.getSaveButton();
	
	// MAKE THE CANVAS CONTROLLER	
	CanvasController canvasController = new CanvasController(app);
	canvas.setOnMousePressed(e->{
            Node shape = (Node)dataManager.getTopShape((int)e.getX(), (int)e.getY());
            if(shape == null && (!dataManager.isInState(MapState.ADD_IMAGE) || shape instanceof Pane || shape instanceof Canvas)){
                nothingSelected();
            }
	    canvasController.processCanvasMousePress((int)e.getX(), (int)e.getY());
	});
	canvas.setOnMouseReleased(e->{
	    canvasController.processCanvasMouseRelease((int)e.getX(), (int)e.getY());
	});
	canvas.setOnMouseDragged(e->{
	    canvasController.processCanvasMouseDragged((int)e.getX(), (int)e.getY());
	});

        // WASD
        scrollPane.setOnKeyPressed( e -> {
            if (null != e.getCode()) switch (e.getCode()) {
                case W:         // Move up
                    try {
                        Robot r = new Robot();
                        r.keyPress(java.awt.event.KeyEvent.VK_UP);
                    } catch (AWTException ex) {
                        Logger.getLogger(MapWorkspace.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case A:         // Move left
                    try {
                        Robot r = new Robot();
                        r.keyPress(java.awt.event.KeyEvent.VK_LEFT);
                    } catch (AWTException ex) {
                        Logger.getLogger(MapWorkspace.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case S:         // Move down
                    try {
                        Robot r = new Robot();
                        r.keyPress(java.awt.event.KeyEvent.VK_DOWN);
                    } catch (AWTException ex) {
                        Logger.getLogger(MapWorkspace.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case D:         // Move right
                    try {
                        Robot r = new Robot();
                        r.keyPress(java.awt.event.KeyEvent.VK_RIGHT);
                    } catch (AWTException ex) {
                        Logger.getLogger(MapWorkspace.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
            } 
        });

        editToolbarScroll.setOnMousePressed(e ->{
            dataManager.unhighlightShape();
            nothingSelected();
        });
        gui.getTopToolbarPane().setOnMousePressed(e ->{
            dataManager.unhighlightShape();
            nothingSelected();
        });

        Button exportButton = gui.getExportButton();
        exportButton.setOnAction(e ->{
            try {
                controller.processExport();
            } catch (IOException ex) {
                Logger.getLogger(MapWorkspace.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        undoButton.setOnAction(e->{
            dataManager.unhighlightShape();
            nothingSelected();
	    controller.handleUndoTransaction();
            nothingSelected();
	});
        redoButton.setOnAction(e->{
            dataManager.unhighlightShape();
            nothingSelected();
            controller.handleDoTransaction();
            nothingSelected();
	});
        
        jtps.getMostRecentProperty().addListener( e ->{
            // At the beginning of the transaction?
            if(jtps.getMostRecentTransaction() == -1){
                undoButton.setDisable(true);
            } else{
               undoButton.setDisable(false);
            }
            
            if(jtps.getMostRecentTransaction() == jtps.getSizeOfJTPS()-1){
                redoButton.setDisable(true);
            } else{
                redoButton.setDisable(false);
            }
            app.getGUI().updateToolbarControls(false);
        });
        
        // Close/ exit program.
        Stage primaryStage = gui.getWindow();
        primaryStage.setOnCloseRequest(e ->{
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            // During welcome screen
            if(primaryStage.getTitle().equals(props.getProperty(WELCOME_TITLE))){
                e.consume();
                gui.closeWelcomeDialog();
            }
            else try {
                if(!gui.getFileController().isSaved()
                        && !gui.getFileController().promptToSave()){
                    e.consume();
                    // Request to save. ############NEED TO WORK ON THIS AFTER MY SAVING STARTS TO WORK. LALALLALLAALLALALALLA
                    primaryStage.close();
                    System.exit(0);
                }
                else{
                    // If file is saved and user clicks the x button.
                    // And user is not in the welcome dialog, exit program.
                    primaryStage.close();
                    System.exit(0);
                }
            } catch (IOException ex) {
                Logger.getLogger(MapWorkspace.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        // ROW 1 CONTROLLER METRO LINES
        metroLineController();
        // ROW 2 CONTROLLER METRO STATIONS
        metroStationController();
        // ROW 3 CONTROLLER ROUTE
        routeController();
        // ROW 4 CONTROLLER DECOR
        decorController();
        // Row 5 CONTROLLER FONT
        fontController();
        // Row 6 CONTROLLER NAVIGATION
        navigationController();
    }
    
    public void handleUndo(){
        MapController controller = new MapController(app);
        controller.handleUndoTransaction();
    }
    
    /**
     * Controller for row 1.
     */
    private void metroLineController(){
        MetroLineController lineController = new MetroLineController(app);
        metroLineComboBox.setOnAction(e -> {
            MetroLine line = dataManager.findMetroLine(metroLineComboBox.getValue());

            if(line == null)
                return;
            
            fontSize.setValue(line.getName1().getFontSize());
            fontFamily.setValue(line.getName1().getFontFamily());
        });
        metroLineEditLine.setOnAction(e -> {
            String prev = null;
            String now = null;
           
            prev = metroLineComboBox.getValue();
            now = lineController.processEditLine();
        });
        addMetroLine.setOnAction(e -> {
            dataManager.unhighlightShape();
            lineController.processAddLine();
        });
        removeMetroLine.setOnAction(e ->{
            // Confirm to delete
            if(lineController.processRemoveLine()){
                dataManager.removeLine(metroLineComboBox.getValue());
                // Remove from combo box
                metroLineObserList.remove(metroLineComboBox.getValue());
                metroLineComboBox.setValue(null);
                 if(metroLineObserList.isEmpty()){
                     metroLineComboBox.setDisable(true);
                 }
                 lineNotSelected();
            }
        });
        addStationToLine.setOnAction(e ->{
            lineController.processAddStation();
        });
        removeStationFromLine.setOnAction(e ->{
            lineController.processRemoveStation();
        });
        showStationList.setOnAction(e ->{
            lineController.processListStation();
        });
                
        // Get the previous value
        metroLineThickness.addEventHandler(MouseEvent.MOUSE_PRESSED, e ->{
            MetroLine selectedLine = dataManager.selectedMetroLine;
            if(selectedLine != null){
                prevThickness = (int)selectedLine.getLineThickness();
            }
        });
        
        // Change the value
        metroLineThickness.valueProperty().addListener(e-> {
            MetroLine selectedLine = dataManager.selectedMetroLine;
            if(selectedLine != null){
                lineController.processLineThickness();
            }
	});
        
        // Put the value to transaction
        metroLineThickness.addEventHandler(MouseEvent.MOUSE_RELEASED, e ->{
            MetroLine selectedLine = dataManager.selectedMetroLine;
            if(selectedLine != null && selectedLine.getLineThickness() != prevThickness){
                lineController.processLineThicknessTrans(prevThickness);
            }
        });
    }
    
    /**
     * Checks if line name already exist.
     * @param name
     *  Name of line to be checked
     * @return 
     *  True if line name already exist. False o.w.
     */
    public boolean hasLineName(String name){
        AppAlertDialogSingleton alert = AppAlertDialogSingleton.getSingleton();
        if(name == null || name.trim().equals("")){
            alert.show("Line name not valid", "Line name not valid", "Have you considered about typing something in the text field?");
            return true;
        }
        for(int i=0; i<metroLineObserList.size(); i++){
            // Check if name already exist
            if(metroLineObserList.get(i).equalsIgnoreCase(name)){
                alert.show("Line name already exist", 
                        name.substring(0,1).toUpperCase() + name.substring(1, name.length()).toLowerCase() + " already exist!",
                        "Line already exist!");
                return true;
            }
        }
        
        // When adding in a line
//        metroLineObserList.add(name);
//        metroLineComboBox.setDisable(false);
//        metroLineComboBox.setValue(name);
        return false;
    }
    
    public void addLineToCombo(String name){
        if(!metroLineObserList.contains(name))
            metroLineObserList.add(name);
        if(metroLineObserList.contains(name))
            metroLineComboBox.setValue(name);
        
        if(!metroLineObserList.isEmpty())
            metroLineComboBox.setDisable(false);
    }
    
    public void removeLineFromCombo(String name){
        if(metroLineObserList.contains(name))
            metroLineObserList.remove(name);
        metroLineComboBox.setValue(null);
        
        if(metroLineObserList.isEmpty())
            metroLineComboBox.setDisable(true);
    }
    
    public void addStationToCombo(String s){
        metroStationObserList.add(s);
        if(metroStationObserList.size() > 1){
            to.setDisable(false);
            from.setDisable(false);
        }
        metroStationComboBox.setDisable(false);
    }
    
    public void removeStationFromCombo(String s){
        if(metroStationObserList.contains(s))
            metroStationObserList.remove(s);
        
        if(metroStationObserList.isEmpty())
            metroStationComboBox.setDisable(true);

        metroStationComboBox.setValue(null);
    }
    
    public void setLineColor(Color color){
        metroLineEditLine.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
    }
    
    /**
     * Controller for row 2.
     */
    private void metroStationController(){
        MetroStationController stationController = new MetroStationController(app);
        metroStationComboBox.setOnAction(e -> {
            if(metroStationComboBox.getValue() != null){
                DraggableStation station = dataManager.findMetroStation(metroStationComboBox.getValue());
                
                if(station == null)
                    return;
                
                fontSize.setValue(station.getLabel().getFontSize());
                fontFamily.setValue(station.getLabel().getFontFamily());
            }
        });
        metroStationColorPicker.setOnAction(e ->{
            if(dataManager.selectedStation != null){
                if(!dataManager.selectedStation.getFill().equals(metroStationColorPicker)){
                    StationColorT trans = new StationColorT(dataManager.selectedStation, metroStationColorPicker.getValue());
                    jtps.addTransaction(trans);
                }
            }
        });
        addStation.setOnAction(e ->{
            dataManager.unhighlightShape();
           stationController.processAddStation();
        });
        removeStation.setOnAction(e ->{
            if(metroStationComboBox.getValue() != null){
                RemoveStationT trans = new RemoveStationT(app, dataManager.findMetroStation(metroStationComboBox.getValue()));
                jtps.addTransaction(trans);
//                dataManager.removeStationForever();
//                removeStationFromCombo(metroStationComboBox.getValue());
                nothingSelected();
            }
        });
        snapCircle.setOnAction(e ->{
            stationController.processSnap();
        });
        moveLabelStation.setOnAction(e ->{
            stationController.processMoveLabel();
        });
        rotateLabelStation.setOnAction(e ->{
            stationController.processRotateLabel();
        });
        
        // get previous value
        stationCircleRadius.addEventHandler(MouseEvent.MOUSE_PRESSED, e ->{
            DraggableStation station = dataManager.selectedStation;
            if(station != null){
                prevRadius = (int)station.getRadius();
            }
	});
        
        // Change the vaue as the slider changes
        stationCircleRadius.valueProperty().addListener(e-> {
            DraggableStation station = dataManager.selectedStation;
            if(station != null){
                stationController.processCircleRadius();
            }
	});
        
        // Change the radius overall
        stationCircleRadius.addEventHandler(MouseEvent.MOUSE_RELEASED, e ->{
            DraggableStation station = dataManager.selectedStation;
            if(station != null){
                stationController.processCircleRadiusTrans(prevRadius);
            }
	});
    }

    public void setRouteComboBox(){
        if(metroStationObserList.size() > 1){
            from.setDisable(false);
            to.setDisable(false);
        } else if(metroStationObserList.isEmpty() || metroStationObserList.size() < 2){
            from.setDisable(true);
            to.setDisable(true);
        }
    }
    /**
     * Checks if line name already exist.
     * @param name
     *  Name of line to be checked
     * @return 
     *  True if line name already exist. False o.w.
     */
    public boolean hasStationName(String name){
        AppAlertDialogSingleton alert = AppAlertDialogSingleton.getSingleton();
        if(name == null || name.trim().equals("")){
            alert.show("Station name not valid", "Station name not valid", "Have you considered about typing something in the text field?");
            return true;
        }
        for(int i=0; i<metroStationObserList.size(); i++){
            // Check if name already exist
            if(metroStationObserList.get(i).equalsIgnoreCase(name)){
                alert.show("Station name already exist", 
                        name.substring(0,1).toUpperCase() + name.substring(1, name.length()).toLowerCase() + " already exist!",
                        "Station already exist!");
                return true;
            }
        }
        
        // Add to combo box
//        metroStationObserList.add(name);
//        metroStationComboBox.setDisable(false);
//        metroStationComboBox.setValue(name);
        return false;
    }
    
    /**
     * Controller for row 3.
     */
    private void routeController(){
        from.setOnAction(e -> {
            // Make sure something is selected in both comboBox and they are not the same station
            if(to.getValue() != null && from.getValue() != null &&
                    !to.getValue().equals(from.getValue()))
                findRoute.setDisable(false);
            else
                findRoute.setDisable(true);
        });
        to.setOnAction(e -> {
            // Make sure something is selected in both comboBox and they are not the same station
            if(to.getValue() != null && from.getValue() != null &&
                    !to.getValue().equals(from.getValue()))
                findRoute.setDisable(false);
            else
                findRoute.setDisable(true);
        });
        
        findRoute.setOnAction(e -> {
            if(to.getValue() == null || from.getValue() == null){
                findRoute.setDisable(true);
            } else {
                findRoute route = new findRoute(app);
                route.showDialog(from.getValue(), to.getValue());
                dataManager.unhighlightShape();
                nothingSelected();
                
//                routeController.findRoute(from.getValue(), to.getValue());
            }

//        Iterator<RouteGraph> it = new Iterator<RouteGraph>() {
//            @Override
//            public boolean hasNext() {
//                return scanner.hasNext();
//            }
//
//            @Override
//            public Word next() {
//                return new DraggableStation(scanner.next());
//            }
//        };
//        RouteGraph wg = new RouteGraph(it);
//        List<DraggableStation> path = wg.minimumWeightPath(start, goal);
        });
    }
    /**
     * Controller for row 4.
     */
    private void decorController(){
        DecorController decorController = new DecorController(app);
        backgroundColorPicker.setOnAction(e->{
	    decorController.processBackgroundColor();
	});
        setImageBackground.setOnAction(e ->{
            decorController.processImageBackground();
        });
        addImage.setOnAction(e ->{
            decorController.processAddImage();
        });
        addLabel.setOnAction(e ->{
            nothingSelected();
           decorController.processAddLabel(); 
        });
        removeElement.setOnAction(e ->{
            decorController.processRemoveElement();
        });
    }

    /**
     * Controller for row 5.
     */
    private void fontController(){
        FontController fontController = new FontController(app);
        fontColorPicker.setOnAction(e ->{
            Color color = fontColorPicker.getValue();
            fontController.processColorPicker(color);
        });
        boldText.setOnAction(e ->{
            fontController.processbold();
        });
        italicsText.setOnAction(e ->{
            fontController.processItalics();
        });
        fontSize.setOnAction(e ->{
            fontController.processFontSize(fontSize.getSelectionModel().getSelectedItem());
        });
        fontFamily.setOnAction(e ->{
            if(!dataManager.isInState(MapState.FUCKING_SHIT))
                fontController.processFontFamily(fontFamily.getSelectionModel().getSelectedItem());
        });
    }
    
    /**
     * Controller for row 6.
     */
    private void navigationController(){
        MapData dataManager = (MapData) app.getDataComponent();
        
        showGridCheck.setOnAction(e ->{
            if(showGridCheck.isSelected())
                scrollPane.enableGrid();
            else // Remove the grid lines
                scrollPane.disableGrid();
        });
        
        zoomIn.setOnAction(e ->{
            scrollPane.increaseScale();
        });
        zoomOut.setOnAction(e ->{
            scrollPane.decreaseScale();
        });
        
        increaseMapSize.setOnAction(e ->{
            scrollPane.increasePane();
        });
        decreaseMapSize.setOnAction(e ->{
            scrollPane.decreasePane();
        });
    }
    
    // HELPER METHOD
    public void loadSelectedShapeSettings(Node shape) {
	if(shape == null){
            return;
        }
        
        // MetroLine selected
	if(shape instanceof DraggableCircle || shape instanceof Line || 
                shape instanceof DraggableText && ((DraggableText)shape).getMetroLine() != null){    
            
            MetroLine metroLine = null;
            
            if(dataManager.selectedMetroLine != null){
                setLineColor(dataManager.selectedMetroLine.getColor());
                metroLine = dataManager.selectedMetroLine;
            } else if(shape instanceof DraggableCircle){
                setLineColor((Color)((DraggableCircle)shape).getFill());
                metroLine = ((DraggableCircle)shape).getMetroLine();
            } else if(shape instanceof Line){
                setLineColor((Color)((Line)shape).getStroke());
                metroLine = dataManager.findMetroLine(((Line)shape));
            } else{
                setLineColor((Color)((DraggableText)shape).getFill());
                metroLine = ((DraggableText)shape).getMetroLine();
            }
            
            if(dataManager.selectedMetroLine != null)
                metroLineComboBox.setValue(dataManager.selectedMetroLine.getName());
            showStationList.setDisable(false);
            metroLineEditLine.setDisable(false);
            removeMetroLine.setDisable(false);
            if(!metroStationObserList.isEmpty())
                addStationToLine.setDisable(false);
            if(metroLine != null)
                removeStationFromLine.setDisable(false);
            metroLineThickness.setDisable(false);
            metroLineThickness.setValue(dataManager.selectedMetroLine.getLineThickness());
            
            // Change the font
            DraggableText text = (DraggableText) metroLine.getName1();
            Color color = (Color)text.getFill();
            fontColorPicker.setValue(color);

            fontColorPicker.setDisable(false);
            boldText.setDisable(false);
            italicsText.setDisable(false);

            if(fontSize.getSelectionModel().getSelectedItem() == null)
                fontSize.getSelectionModel().select(text.getFontSize());
            fontSize.setDisable(false);

            if(fontFamily.getSelectionModel().getSelectedItem() == null)
                fontFamily.setValue(text.getFontFamily());
            fontFamily.setDisable(false);
            
            stationNotSelected();
            decorNotSelected();
//            labelNotSelected();
            
//            snapCircle.setDisable(false);
        } else if(shape instanceof DraggableStation){
            Color colorOfStation = (Color)(dataManager.selectedStation).getFill();
            metroStationColorPicker.setValue(colorOfStation);
            metroStationComboBox.setValue(((DraggableStation)shape).getName());
            metroStationColorPicker.setDisable(false);
            moveLabelStation.setDisable(false);
            rotateLabelStation.setDisable(false);
            stationCircleRadius.setDisable(false);
            stationCircleRadius.setValue(dataManager.selectedStation.getRadius());
            removeStation.setDisable(false);
            
            // Change the font
            DraggableText text = (DraggableText) ((DraggableStation)shape).getLabel();
            Color color = (Color)text.getFill();
            fontColorPicker.setValue(color);

            fontColorPicker.setDisable(false);
            boldText.setDisable(false);
            italicsText.setDisable(false);

            if(fontSize.getSelectionModel().getSelectedItem() == null)
                fontSize.getSelectionModel().select(text.getFontSize());
            fontSize.setDisable(false);

            if(fontFamily.getSelectionModel().getSelectedItem() == null)
                fontFamily.setValue(text.getFontFamily());
            fontFamily.setDisable(false);
            
            lineNotSelected();
            decorNotSelected();
//            labelNotSelected();
            
            snapCircle.setDisable(false);
        } else if(shape instanceof DraggableImage){
            removeElement.setDisable(false);
            
            lineNotSelected();
            stationNotSelected();
            labelNotSelected();
        } else if(shape instanceof DraggableText){
            DraggableText text = (DraggableText) dataManager.getSelectedShape();
            Color color = (Color)text.getFill();
            fontColorPicker.setValue(color);
            
            fontColorPicker.setDisable(false);
            boldText.setDisable(false);
            italicsText.setDisable(false);
            
            fontSize.setValue(text.getFontSize());
            fontSize.setDisable(false);
            
            fontFamily.setValue(text.getFontFamily());
            fontFamily.setDisable(false);
            
            removeElement.setDisable(false);
            
            lineNotSelected();
            stationNotSelected();
        }
    }
    /**
     * This function reloads all the controls
     * the workspace.
     * @param data
     */
    @Override
    public void reloadWorkspace(AppDataComponent data) {        
	if(dataManager.isInState(MapState.ADD_LINE)){
            Node shape = dataManager.getSelectedShape();
            if(shape == null){
                return;
            }
            
            Color colorOfLine = Color.TRANSPARENT;
            
            MetroLine metroLine = null;
            
            if(shape instanceof DraggableCircle){
                setLineColor((Color)((DraggableCircle)shape).getFill());
                metroLine = ((DraggableCircle)shape).getMetroLine();
            } else if(shape instanceof Line){
                setLineColor((Color)((Line)shape).getStroke());
                metroLine = dataManager.findMetroLine(((Line)shape));
            } else{
                setLineColor((Color)((DraggableText)shape).getFill());
                metroLine = ((DraggableText)shape).getMetroLine();
            }
            
            metroLineEditLine.setBackground(new Background(new BackgroundFill(colorOfLine, CornerRadii.EMPTY, Insets.EMPTY)));
            
            showStationList.setDisable(false);
            metroLineEditLine.setDisable(false);
            removeMetroLine.setDisable(false);
            if(!metroStationObserList.isEmpty())
                addStationToLine.setDisable(false);
            if(metroLine != null)
                removeStationFromLine.setDisable(false);
            // Needs to be updated....##############
            metroLineThickness.setDisable(false);
            metroLineThickness.setValue(5);
            
            stationNotSelected();
            decorNotSelected();
//            labelNotSelected();
        }
        else if(dataManager.isInState(MapState.ADD_STATION)){
            nothingSelected();
            // Change state
            dataManager.setState(MapState.ADD_STATION);
            stationCircleRadius.setValue(10);
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.CROSSHAIR);
        } else if(dataManager.isInState(MapState.REMOVE_STATION)){
            nothingSelected();
            // Change state
            dataManager.setState(MapState.REMOVE_STATION);
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.CROSSHAIR);
        } else if(dataManager.isInState(MapState.ADD_IMAGE)){
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.CROSSHAIR);
            
            removeElement.setDisable(false);
                        
            lineNotSelected();
            stationNotSelected();
            labelNotSelected();
        } else if(dataManager.isInState(MapState.ADD_TEXT)){
            DraggableText text = (DraggableText) dataManager.getSelectedShape();
            
            if(text == null)
                return;
            
            Color color = (Color)text.getFill();
            fontColorPicker.setValue(color);
            
            fontColorPicker.setDisable(false);
            boldText.setDisable(false);
            italicsText.setDisable(false);
            
            fontSize.setValue(text.getFontSize());
            fontSize.setDisable(false);
            
            fontFamily.setValue(text.getFontFamily());
            fontFamily.setDisable(false);
            
            removeElement.setDisable(false);
            
            lineNotSelected();
            stationNotSelected();
        }
    }
    
    /**
     * When a line is not selected, these buttons will be disabled
     */
    private void lineNotSelected(){
        metroLineComboBox.setValue(null);
        showStationList.setDisable(true);
        metroLineEditLine.setDisable(true);
        removeMetroLine.setDisable(true);
        addStationToLine.setDisable(true);
        removeStationFromLine.setDisable(true);
        metroLineThickness.setDisable(true);
        Scene scene = app.getGUI().getPrimaryScene();
        scene.setCursor(Cursor.DEFAULT);
    }
    
    private void stationNotSelected(){
        metroStationComboBox.setValue(null);
        metroStationColorPicker.setDisable(true);
        removeStation.setDisable(true);
        snapCircle.setDisable(true);
        moveLabelStation.setDisable(true);
        rotateLabelStation.setDisable(true);
        stationCircleRadius.setDisable(true);
        Scene scene = app.getGUI().getPrimaryScene();
        scene.setCursor(Cursor.DEFAULT);
    }
    
    private void decorNotSelected(){
        removeElement.setDisable(true);
        Scene scene = app.getGUI().getPrimaryScene();
        scene.setCursor(Cursor.DEFAULT);
    }
    
    private void labelNotSelected(){
        if(dataManager.getSelectedShape() instanceof DraggableText)
            dataManager.unhighlightShape();
        
        fontColorPicker.setDisable(true);
        boldText.setDisable(true);
        italicsText.setDisable(true);
        fontSize.setValue(null);
        fontSize.setDisable(true);
        fontFamily.setValue(null);
        fontFamily.setDisable(true);
        Scene scene = app.getGUI().getPrimaryScene();
        scene.setCursor(Cursor.DEFAULT);
    }
    
    public void nothingSelected(){
        lineNotSelected();
        stationNotSelected();
        decorNotSelected();
        labelNotSelected();
        dataManager.unhighlightShape();
        
        // Make sure cursor goes back to normal
        Scene scene = app.getGUI().getPrimaryScene();
        scene.setCursor(Cursor.DEFAULT);
        
        dataManager.setState(MapState.SELECTING_SHAPE);
    }
    
    /**
     * This function specifies the CSS style classes for all the UI components
     * known at the time the workspace is initially constructed. Note that the
     * tag editor controls are added and removed dynamicaly as the application
     * runs so they will have their style setup separately.
     */
    public void initStyle() {
	// NOTE THAT EACH CLASS SHOULD CORRESPOND TO
	// A STYLE CLASS SPECIFIED IN THIS APPLICATION'S
	// CSS FILE
	
	// COLOR PICKER STYLE
	metroStationColorPicker.getStyleClass().add(CLASS_COLORPICKER);
        metroStationColorPicker.getStyleClass().add("button");
	backgroundColorPicker.getStyleClass().add(CLASS_COLORPICKER);
        backgroundColorPicker.getStyleClass().add("button");
        fontColorPicker.getStyleClass().add(CLASS_COLORPICKER);
        fontColorPicker.getStyleClass().add("button");
	
        editToolbar.getChildren().forEach((rows) -> {
            rows.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        });
        editToolbar.getStyleClass().add(CLASS_EDIT_TOOLBAR);
        editToolbarScroll.getStyleClass().add(CLASS_SCROLLPANE_MAINUI);
        
        metroLineLabel.getStyleClass().add(CLASS_LABEL);
        metroStationLabel.getStyleClass().add(CLASS_LABEL);
        decorLabel.getStyleClass().add(CLASS_LABEL);
        fontLabel.getStyleClass().add(CLASS_LABEL);
        navigationLabel.getStyleClass().add(CLASS_LABEL);
        showGridCheck.getStyleClass().add(CLASS_LABEL);
        
        addMetroLine.getStyleClass().add(CLASS_BUTTON);
        removeMetroLine.getStyleClass().add(CLASS_BUTTON);
        addStationToLine.getStyleClass().add(CLASS_BUTTON);
        removeStationFromLine.getStyleClass().add(CLASS_BUTTON);
        showStationList.getStyleClass().add(CLASS_BUTTON);
        addStation.getStyleClass().add(CLASS_BUTTON);
        removeStation.getStyleClass().add(CLASS_BUTTON);
        snapCircle.getStyleClass().add(CLASS_BUTTON);
        moveLabelStation.getStyleClass().add(CLASS_BUTTON);
        rotateLabelStation.getStyleClass().add(CLASS_BUTTON);
        findRoute.getStyleClass().add(CLASS_BUTTON);
        setImageBackground.getStyleClass().add(CLASS_BUTTON);
        addImage.getStyleClass().add(CLASS_BUTTON);
        addLabel.getStyleClass().add(CLASS_BUTTON);
        removeElement.getStyleClass().add(CLASS_BUTTON);
        boldText.getStyleClass().add(CLASS_BUTTON);
        italicsText.getStyleClass().add(CLASS_BUTTON);
        zoomIn.getStyleClass().add(CLASS_BUTTON);
        zoomOut.getStyleClass().add(CLASS_BUTTON);
        increaseMapSize.getStyleClass().add(CLASS_BUTTON);
        decreaseMapSize.getStyleClass().add(CLASS_BUTTON);
    }
    
    @Override
    public void resetWorkspace() {
        nothingSelected();
        canvas.setPrefSize(1100, 700);
        if(scrollPane.gridEnabled()){
            scrollPane.disableGrid();
            showGridCheck.setSelected(false);
        }
    }
    
    public void clearEditToolbar(){
        metroLineObserList.clear();
        metroStationObserList.clear();
        metroLineComboBox.setDisable(true);
        metroStationComboBox.setDisable(true);
        
        nothingSelected();
        
        metroLineEditLine.setBackground(Background.EMPTY);
        metroStationColorPicker.setValue(null);
        backgroundColorPicker.setValue(null);
        fontColorPicker.setValue(null);
        
        to.setDisable(true);
        from.setDisable(true);
    }
}