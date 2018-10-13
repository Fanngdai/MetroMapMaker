package djf.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import djf.controller.AppFileController;
import djf.AppTemplate;
import static djf.settings.AppPropertyType.*;
import static djf.settings.AppStartupConstants.FILE_PROTOCOL;
import static djf.settings.AppStartupConstants.PATH_IMAGES;
import static djf.settings.AppStartupConstants.PATH_WORK;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * This class provides the basic user interface for this application,
 * including all the file controls, but not including the workspace,
 * which would be customly provided for each app.
 * 
 * @author Richard McKenna
 * @version 1.0
 */
public class AppGUI {
    // THIS HANDLES INTERACTIONS WITH FILE-RELATED CONTROLS
    protected AppFileController fileController;

    // THIS IS THE APPLICATION WINDOW
    protected Stage primaryStage;

    // THIS IS THE STAGE'S SCENE GRAPH
    protected Scene primaryScene;

    // THIS PANE ORGANIZES THE BIG PICTURE CONTAINERS FOR THE
    // APPLICATION AppGUI. NOTE THAT THE WORKSPACE WILL GO
    // IN THE CENTER REGION OF THE appPane
    protected BorderPane appPane;
    
    // THIS IS THE TOP PANE WHERE WE CAN PUT TOOLBAR
    protected HBox topToolbarPane;
    
    // THIS IS THE FILE TOOLBAR AND ITS CONTROLS
    protected HBox leftFileToolbar;
    protected HBox rightFileToolbar;

    // FILE TOOLBAR BUTTONS
    protected Button newButton;
    protected Button loadButton;
    protected Button saveButton;
    protected Button saveAsButton;
    protected Button exportButton;
    protected Button undoButton;
    protected Button redoButton;
    protected Button aboutButton;
    
    // THIS DIALOG IS USED FOR GIVING FEEDBACK TO THE USER
    protected AppConfirmDialogSingleton yesNoCancelDialog;
    
    // THIS TITLE WILL GO IN THE TITLE BAR
    protected String appTitle;
    
    private final AppTemplate app;
    
    // FOR WELCOME DIALOG
    protected VBox recentWork = new VBox();
    private Label recentWorkLabel;
    // Right side of the welcome dialog
    protected VBox rightSideV = new VBox();
    protected Button createNewMetroMap;
    
    protected ArrayList<File> files = new ArrayList<>();
    
    /**
     * This constructor initializes the file toolbar for use.
     * 
     * @param initPrimaryStage The window for this application.
     * 
     * @param initAppTitle The title of this application, which
     * will appear in the window bar.
     * 
     * @param app The app within this gui is used.
     */
    public AppGUI(  Stage initPrimaryStage, 
		    String initAppTitle, 
		    AppTemplate app){
	// SAVE THESE FOR LATER
	primaryStage = initPrimaryStage;
	appTitle = initAppTitle;
        this.app = app;
        
        // Set up the files
        loadRecentFiles();
        
        // INIT THE TOOLBAR
        initWelcomeDialog();
        initWelcomeController();
        // Set up the toolbar but will not show it
        initTopToolbar(app);
        		
        // AND FINALLY START UP THE WINDOW (WITHOUT THE WORKSPACE)
        initWindow();
        
        // INIT THE STYLESHEET AND THE STYLE FOR THE FILE TOOLBAR
        initStylesheet(app);
        initStyle();
    }
    
    /**
     * Accessor method for getting the file toolbar controller.
     */
    public AppFileController getFileController() { return fileController; }
    
    /**
     * Accessor method for getting the application pane, within which all
     * user interface controls are ultimately placed.
     * 
     * @return This application GUI's app pane.
     */
    public BorderPane getAppPane() { return appPane; }
    
    /**
     * Accessor method for getting the toolbar pane in the top, within which
     * other toolbars are placed.
     * 
     * @return This application GUI's app pane.
     */    
    public HBox getTopToolbarPane() {
        return topToolbarPane;
    }
    
    /**
     * Accessor method for getting the file toolbar pane, within which all
     * file controls are ultimately placed.
     * 
     * @return This application GUI's app pane.
     */    
    public HBox getLeftFileToolbar() {
        return leftFileToolbar;
    }
    
    public HBox getRightFileToolbar() {
        return rightFileToolbar;
    }
    
    public Button getExportButton(){
        return exportButton;
    }
    
    public Button getUndoButton(){
        return undoButton;
    }

    public Button getRedoButton(){
        return redoButton;
    }
    
    public Button getSaveButton(){
        return saveButton;
    }
    /**
     * Accessor method for getting this application's primary stage's,
     * scene.
     * 
     * @return This application's window's scene.
     */
    public Scene getPrimaryScene() { return primaryScene; }
    
    /**
     * Accessor method for getting this application's window,
     * which is the primary stage within which the full GUI will be placed.
     * 
     * @return This application's primary stage (i.e. window).
     */    
    public Stage getWindow() { return primaryStage; }
    
    public ArrayList<File> getFiles(){ return files; }

    /**
     * This method is used to activate/deactivate toolbar buttons when
     * they can and cannot be used so as to provide foolproof design.
     * 
     * @param saved Describes whether the loaded Page has been saved or not.
     */
    public void updateToolbarControls(boolean saved) {
        // THIS TOGGLES WITH WHETHER THE CURRENT COURSE
        // HAS BEEN SAVED OR NOT
        saveButton.setDisable(saved);
        if(!saved)
            fileController.markFileAsNotSaved();
        
        saveAsButton.setDisable(false);
        exportButton.setDisable(false);
    }

    /****************************************************************************/
    /* BELOW ARE ALL THE PRIVATE HELPER METHODS WE USE FOR INITIALIZING OUR AppGUI */
    /****************************************************************************/
    
    private void initWelcomeDialog(){
        // LEFT SIDE
        recentWorkLabel = new Label("Recent Work");
        recentWork.getChildren().add(recentWorkLabel);
        
        // RIGHT SIDE
        createNewMetroMap = new Button("Create New Metro Map");
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String appIcon = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(APP_LOGO);
        Image image = new Image(appIcon);
        ImageView imageView = new ImageView(image);
        rightSideV.getChildren().addAll(imageView,createNewMetroMap);
        rightSideV.setSpacing(100);
        rightSideV.setAlignment(Pos.CENTER);
        
        // Put everything together
        appPane = new BorderPane();
        ((BorderPane)appPane).setLeft(recentWork);
        ((BorderPane)appPane).setCenter(rightSideV);
        primaryStage.setResizable(false);   
    }
    
    private void loadRecentFiles(){
        File workFile = new File(PATH_WORK);
        File[] allFiles = workFile.listFiles();
        boolean flags;
        
        for(File theFile: allFiles){
            flags = true;
            
            if(theFile.getName().equals(".DS_Store"))      // fucking ds store don't fucking need
                flags = false;
            else if(files.size()==0){
                 files.add(theFile);
                 flags=false;
            }
            
            // last modified 
            for(int i = 0; i<files.size(); i++){
                if(theFile.lastModified() > files.get(i).lastModified() && flags){
                    files.add(i, theFile);
                    flags=false;
                }
                else if(i == files.size()-1 && flags){
                    files.add(theFile);
                    flags=false;
                }
            }
        }
    }
    
    private void initWelcomeController(){
        fileController = new AppFileController(app);
        
        createNewMetroMap.setOnMouseClicked(e ->{
            // Ask user for the file name
            if(fileController.askForFileName()){
                closeWelcomeDialog();
                fileController.handleNewRequest();
            }
        });
        
        int min = Math.min(files.size(), 10);
        for(int i = 0; i < min; i++){
            File george = files.get(i);
            Hyperlink ben = new Hyperlink(george.getName() +"\n"+
                    new SimpleDateFormat("MM-dd-yyyy HH:mm").format(new Date(george.lastModified())));
            ben.setFont(Font.font(18));
            ben.setStyle("-fx-text-fill: black;");
            recentWork.getChildren().add(ben);
            
            ben.setOnAction((ActionEvent e) ->{
                closeWelcomeDialog();
                fileController.handleRecentFile(george);
            });
        }
    }
    
    public void closeWelcomeDialog(){
        appPane.getChildren().removeAll(recentWork, rightSideV);
            
        // Change the window header name
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        primaryStage.setTitle(props.getProperty(APP_TITLE));
        
        appPane.setTop(topToolbarPane);
        
        // INIT THE STYLESHEET AND THE STYLE FOR THE FILE TOOLBAR
        initStylesheet(app);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
    }
    
    
    /**
     * This function initializes all the buttons in the toolbar at the top of
     * the application window. These are related to file management.
     */
    private void initTopToolbar(AppTemplate app) {
        leftFileToolbar = new HBox();
        leftFileToolbar.setAlignment(Pos.CENTER_LEFT);
        leftFileToolbar.setSpacing(10);
        rightFileToolbar = new HBox();
        rightFileToolbar.setAlignment(Pos.CENTER_RIGHT);
        rightFileToolbar.setSpacing(10);

        // HERE ARE OUR FILE TOOLBAR BUTTONS, NOTE THAT SOME WILL
        // START AS ENABLED (false), WHILE OTHERS DISABLED (true)
        newButton = initChildButton(leftFileToolbar,	NEW_ICON.toString(),	    NEW_TOOLTIP.toString(),	false);
        loadButton = initChildButton(leftFileToolbar,	LOAD_ICON.toString(),	    LOAD_TOOLTIP.toString(),	false);
        saveButton = initChildButton(leftFileToolbar,	SAVE_ICON.toString(),	    SAVE_TOOLTIP.toString(),	true);
        saveAsButton = initChildButton(leftFileToolbar,	SAVEAS_ICON.toString(),	    SAVEAS_TOOLTIP.toString(),	true);
        exportButton = initChildButton(leftFileToolbar,	EXPORT_ICON.toString(),	    EXPORT_TOOLTIP.toString(),	true);
        
        undoButton = initChildButton(rightFileToolbar,	UNDO_ICON.toString(),	    UNDO_TOOLTIP.toString(),	true);
        redoButton = initChildButton(rightFileToolbar,	REDO_ICON.toString(),	    REDO_TOOLTIP.toString(),	true);
        
        // Always enabled
        aboutButton = initChildButton(rightFileToolbar,	ABOUT_ICON.toString(),	    ABOUT_TOOLTIP.toString(),	false);

	// AND NOW SETUP THEIR EVENT HANDLERS
        fileController = new AppFileController(app);
        newButton.setOnAction(e -> {
            if(fileController.askForFileName())
                fileController.handleNewRequest();
        });
        loadButton.setOnAction(e -> {
            fileController.handleLoadRequest();
        });
        saveButton.setOnAction(e -> {
            fileController.handleSaveRequest();
        });
        saveAsButton.setOnAction(e -> {
            fileController.handleSaveAsRequest();
        });
        aboutButton.setOnAction(e -> {
           fileController.handleAboutRequest(); 
        });
        
        // NOW PUT THE FILE TOOLBAR IN THE TOP TOOLBAR, WHICH COULD
        // ALSO STORE OTHER TOOLBARS
        topToolbarPane = new HBox();
        topToolbarPane.setAlignment(Pos.CENTER);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topToolbarPane.getChildren().addAll(leftFileToolbar, spacer, rightFileToolbar);
    }

    // INITIALIZE THE WINDOW (i.e. STAGE) PUTTING ALL THE CONTROLS
    // THERE EXCEPT THE WORKSPACE, WHICH WILL BE ADDED THE FIRST
    // TIME A NEW Page IS CREATED OR LOADED
    private void initWindow() {
	PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        // SET THE WINDOW TITLE
        primaryStage.setTitle(appTitle);

        // START FULL-SCREEN OR NOT, ACCORDING TO PREFERENCES
        primaryStage.setMaximized(false);
//        primaryStage.initStyle(StageStyle.UNDECORATED);

        // ADD THE TOOLBAR ONLY, NOTE THAT THE WORKSPACE
        // HAS BEEN CONSTRUCTED, BUT WON'T BE ADDED UNTIL
        // THE USER STARTS EDITING A COURSE
        primaryScene = new Scene(appPane);

        // SET THE APP PANE PREFERRED SIZE ACCORDING TO THE PREFERENCES
        double prefWidth = Double.parseDouble(props.getProperty(PREF_WIDTH));
        double prefHeight = Double.parseDouble(props.getProperty(PREF_HEIGHT));
        appPane.setPrefWidth(prefWidth);
        appPane.setPrefHeight(prefHeight);

        // SET THE APP ICON
        String appIcon = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(APP_LOGO_MINI);
        primaryStage.getIcons().add(new Image(appIcon));

        // NOW TIE THE SCENE TO THE WINDOW
        primaryStage.setScene(primaryScene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(600);
    }
    
    /**
     * This is a public helper method for initializing a simple button with
     * an icon and tooltip and placing it into a toolbar.
     * 
     * @param toolbar Toolbar pane into which to place this button.
     * 
     * @param icon Icon image file name for the button.
     * 
     * @param tooltip Tooltip to appear when the user mouses over the button.
     * 
     * @param disabled true if the button is to start off disabled, false otherwise.
     * 
     * @return A constructed, fully initialized button placed into its appropriate
     * pane container.
     */
    public Button initChildButton(Pane toolbar, String icon, String tooltip, boolean disabled) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
	
	// LOAD THE ICON FROM THE PROVIDED FILE
        String imagePath = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(icon);
        Image buttonImage = new Image(imagePath);
	
	// NOW MAKE THE BUTTON
        Button button = new Button();
        button.setDisable(disabled);
        button.setGraphic(new ImageView(buttonImage));
        Tooltip buttonTooltip = new Tooltip(props.getProperty(tooltip));
        button.setTooltip(buttonTooltip);
	
	// PUT THE BUTTON IN THE TOOLBAR
        toolbar.getChildren().add(button);
	
	// AND RETURN THE COMPLETED BUTTON
        return button;
    }
    
    /**
     * This function sets up the stylesheet to be used for specifying all
     * style for this application. Note that it does not attach CSS style
     * classes to controls, that must be done separately.
     */
    private void initStylesheet(AppTemplate app) {
	// SELECT THE STYLESHEET
	PropertiesManager props = PropertiesManager.getPropertiesManager();
	String stylesheet = props.getProperty(APP_PATH_CSS);
	stylesheet += props.getProperty(APP_CSS);
        Class appClass = app.getClass();
	URL stylesheetURL = appClass.getResource(stylesheet);
	String stylesheetPath = stylesheetURL.toExternalForm();
	primaryScene.getStylesheets().add(stylesheetPath);	
    }
    
    /**
     *  Note that this is the default style class for the top file toolbar
     * and that style characteristics for this type of component should be 
     * put inside app_properties.xml.
     */
    public static final String CLASS_BORDERED_PANE = "bordered_pane";
    public static final String CLASS_RECENT_WORK_LABEL = "recent_work_label";
    public static final String CLASS_RECENT_WORK_VBOX = "recent_work_vbox";
    public static final String CLASS_RIGHT_SIDE_V = "rightsizeV";
    public static final String CLASS_BUTTON_WELCOME = "button_Welcome";
    public static final String CLASS_BUTTON = "button_MainUI";
    
    /**
     * This function specifies the CSS style classes for the controls managed
     * by this framework.
     */
    private void initStyle() {
	topToolbarPane.getStyleClass().add(CLASS_BORDERED_PANE);
        leftFileToolbar.getStyleClass().add(CLASS_BORDERED_PANE);
        rightFileToolbar.getStyleClass().add(CLASS_BORDERED_PANE);
        
	newButton.getStyleClass().add(CLASS_BUTTON);
	loadButton.getStyleClass().add(CLASS_BUTTON);
	saveButton.getStyleClass().add(CLASS_BUTTON);
        saveAsButton.getStyleClass().add(CLASS_BUTTON);
        exportButton.getStyleClass().add(CLASS_BUTTON);
        undoButton.getStyleClass().add(CLASS_BUTTON);
        redoButton.getStyleClass().add(CLASS_BUTTON);
        aboutButton.getStyleClass().add(CLASS_BUTTON);
        
        recentWorkLabel.getStyleClass().add(CLASS_RECENT_WORK_LABEL);
        recentWork.getStyleClass().add(CLASS_RECENT_WORK_VBOX);
        recentWork.setPrefWidth(300);
        
        rightSideV.getStyleClass().add(CLASS_RIGHT_SIDE_V);
        createNewMetroMap.getStyleClass().add(CLASS_BUTTON);
    }
}