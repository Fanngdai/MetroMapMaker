 package djf.controller;

import djf.ui.AppConfirmDialogSingleton;
import djf.ui.AppGUI;
import java.io.File;
import java.io.IOException;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import properties_manager.PropertiesManager;
import djf.AppTemplate;
import static djf.settings.AppPropertyType.LOAD_ERROR_MESSAGE;
import static djf.settings.AppPropertyType.LOAD_ERROR_TITLE;
import static djf.settings.AppPropertyType.LOAD_WORK_TITLE;
import static djf.settings.AppPropertyType.WORK_FILE_EXT;
import static djf.settings.AppPropertyType.WORK_FILE_EXT_DESC;
import static djf.settings.AppPropertyType.NEW_COMPLETED_MESSAGE;
import static djf.settings.AppPropertyType.NEW_COMPLETED_TITLE;
import static djf.settings.AppPropertyType.NEW_ERROR_MESSAGE;
import static djf.settings.AppPropertyType.NEW_ERROR_TITLE;
import static djf.settings.AppPropertyType.SAVE_COMPLETED_MESSAGE;
import static djf.settings.AppPropertyType.SAVE_COMPLETED_TITLE;
import static djf.settings.AppPropertyType.SAVE_UNSAVED_WORK_MESSAGE;
import static djf.settings.AppPropertyType.SAVE_UNSAVED_WORK_TITLE;
import static djf.settings.AppPropertyType.SAVE_WORK_TITLE;
import static djf.settings.AppStartupConstants.*;
import djf.ui.AppAlertDialogSingleton;
import djf.ui.AppTextDialogSingleton;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import org.apache.commons.io.FileUtils;

/**
 * This class provides the event programmed responses for the file controls
 * that are provided by this framework.
 * 
 * @author Richard McKenna
 * @version 1.0
 */
public class AppFileController {
    // HERE'S THE APP
    AppTemplate app;
    
    // WE WANT TO KEEP TRACK OF WHEN SOMETHING HAS NOT BEEN SAVED
    boolean saved;
    
    // THIS IS THE FILE FOR THE WORK CURRENTLY BEING WORKED ON
    File currentWorkFile;

    static final String NAME = "name";
    static final String LINES = "lines";
    static final String STATIONS = "stations";
    
    /**
     * This constructor just keeps the app for later.
     * 
     * @param initApp The application within which this controller
     * will provide file toolbar responses.
     */
    public AppFileController(AppTemplate initApp) {
        // NOTHING YET
        saved = true;
        app = initApp;
    }
    
    /**
     * This method marks the appropriate variable such that we know
     * that the current Work has been edited since it's been saved.
     * The UI is then updated to reflect this.
     * 
     * @param gui The user interface editing the Work.
     */
    public void markAsEdited(AppGUI gui) {
        // THE WORK IS NOW DIRTY
        saved = false;
        
        // LET THE UI KNOW
        gui.updateToolbarControls(saved);
    }

    /**
     * This method starts the process of editing new Work. If work is
     * already being edited, it will prompt the user to save it first.
     * 
     */
    public void handleNewRequest() {
	AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
	PropertiesManager props = PropertiesManager.getPropertiesManager();
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToMakeNew = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToMakeNew = promptToSave();
            }

            // IF THE USER REALLY WANTS TO MAKE A NEW COURSE
            if (continueToMakeNew) {
                
                // RESET THE WORKSPACE
		app.getWorkspaceComponent().resetWorkspace();

                // RESET THE DATA
                app.getDataComponent().resetData();
                
                // NOW RELOAD THE WORKSPACE WITH THE RESET DATA
                app.getWorkspaceComponent().reloadWorkspace(app.getDataComponent());

		// MAKE SURE THE WORKSPACE IS ACTIVATED
		app.getWorkspaceComponent().activateWorkspace(app.getGUI().getAppPane());
		
		// WORK IS SAVED
                saved = true;

                // REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE
                // THE APPROPRIATE CONTROLS
                app.getGUI().updateToolbarControls(saved);
               
                app.getGUI().getWindow().setTitle(TITLE_STARTER + currentWorkFile.getName());
                
                // TELL THE USER NEW WORK IS UNDERWAY
		dialog.show(props.getProperty(NEW_COMPLETED_TITLE), props.getProperty(NEW_COMPLETED_TITLE),
                        props.getProperty(NEW_COMPLETED_MESSAGE));
            }
        } catch (IOException ioe) {
            // SOMETHING WENT WRONG, PROVIDE FEEDBACK
	    dialog.show(props.getProperty(NEW_ERROR_TITLE), props.getProperty(NEW_ERROR_TITLE),
                    props.getProperty(NEW_ERROR_MESSAGE));
        }
    }

    /** Ask user for the name of the new file. Once created, it will save the file.
     * If the name already exists, dialog will inform user. If the cancel button or
     * the exit button is pressed, nothing will happen.
     * 
     * @return
     * true if it was a success. False if user clicked cancel or exit. In which, the
     * program will not do anything
     * @throws IOException 
     */
    public boolean askForFileName(){
        try{
        AppTextDialogSingleton dialog = AppTextDialogSingleton.getSingleton() ;
        String result = dialog.showAdd("New Map Name", "Please Enter File Name \n '\\' are not allowed!", "Please enter file name here:").trim();
        
        String selection = dialog.getSelection();

        ArrayList<File> files = app.getGUI().getFiles();

        while(true){
            // Cancel
            if(selection.equals(AppTextDialogSingleton.CANCEL)){
                dialog.close();
                return false;
            }
            
            if(result.equals("") || result.contains("\\")){
                result = dialog.showAdd("New Map Name",
                    "That is not a valid name!",
                    "Please enter file name here:").trim();
                selection = dialog.getSelection();
            }
            else{
                boolean nameExist = false;
                for(int i=0; i<files.size(); i++){
                    if(!nameExist && files.get(i).getName().equalsIgnoreCase(result)){
                        result = dialog.showAdd("New Map Name",
                                result.toUpperCase() + " already exist!",
                                "Please enter file name here:").trim();
                        selection = dialog.getSelection();
                        nameExist = true;
                    }
                }
                if(!nameExist){
                    // Write empty json file
                    writeNewFile(result);
                    // Makes export file
                    createExport();
                    return true;
                }
            }
        }
    } catch(IOException ex){
        return false;
    }
    }
    
    // Write an empty JSON File
    public void writeNewFile(String fileName) throws FileNotFoundException{
        currentWorkFile = new File(PATH_WORK+fileName);
        app.getGUI().getFiles().add(currentWorkFile);
        
        JsonArrayBuilder lineBuilder = Json.createArrayBuilder();
        JsonArrayBuilder stationBuilder = Json.createArrayBuilder();
        JsonArray lineArray = lineBuilder.build();
        JsonArray stationArray = stationBuilder.build();
        
        // Builds the object
        JsonObject jsonFile = Json.createObjectBuilder().build();

        JsonObject dataManagerJSO = Json.createObjectBuilder()
            .add(NAME, currentWorkFile.getName())
            .add(LINES, lineArray)
            .add(STATIONS, stationArray)
            .build();
        
        	// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
	Map<String, Object> properties = new HashMap<>(1);
	properties.put(JsonGenerator.PRETTY_PRINTING, true);
	JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
	StringWriter sw = new StringWriter();
	JsonWriter jsonWriter = writerFactory.createWriter(sw);
	jsonWriter.writeObject(dataManagerJSO);
	jsonWriter.close();

	// INIT THE WRITER
	OutputStream os = new FileOutputStream(PATH_WORK+fileName);
	JsonWriter jsonFileWriter = Json.createWriter(os);
	jsonFileWriter.writeObject(dataManagerJSO);
	String prettyPrinted = sw.toString();
	PrintWriter pw = new PrintWriter(PATH_WORK+fileName);
	pw.write(prettyPrinted);
	pw.close();
    }
  
    /**
     * Makes the dir in export folder.
     * @throws IOException 
     */
    public void createExport() throws IOException{
        File exportFile = new File(PATH_EXPORT + currentWorkFile.getName() + "/");
        FileUtils.forceMkdir(exportFile);
    }
    
    public String processExportJSON() throws FileNotFoundException, IOException{
        String pathToDir = PATH_EXPORT + currentWorkFile.getName() + "/";
        String pathToInDir = pathToDir + currentWorkFile.getName();
        
        // The path to the folder
        if (currentWorkFile != null) {
            File jsonFile = new File(pathToInDir + " Metro.json");
            // SAVE IT TO A FILE
            app.getFileComponent().saveData(app.getDataComponent(), jsonFile.getPath());

            // AND REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE
            // THE APPROPRIATE CONTROLS
            app.getGUI().updateToolbarControls(saved);	
        }
        return pathToInDir;
    }
    /**
     * This method lets the user open a Course saved to a file. It will also
     * make sure data for the current Course is not lost.
     */
    public void handleLoadRequest() {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToOpen = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToOpen = promptToSave();
            }

            // IF THE USER REALLY WANTS TO OPEN A Course
            if (continueToOpen) {
                // GO AHEAD AND PROCEED LOADING A Course
                promptToOpen();
            }
        } catch (IOException ioe) {
            // SOMETHING WENT WRONG
	    AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
	    PropertiesManager props = PropertiesManager.getPropertiesManager();
	    dialog.show(props.getProperty(LOAD_ERROR_TITLE), props.getProperty(LOAD_ERROR_TITLE),
                    props.getProperty(LOAD_ERROR_MESSAGE));
        }
    }

    /**
     * This method will save the current course to a file. Note that we already
     * know the name of the file, so we won't need to prompt the user.
     */
    public void handleSaveRequest() {
	// WE'LL NEED THIS TO GET CUSTOM STUFF
	PropertiesManager props = PropertiesManager.getPropertiesManager();
        try {
	    // MAYBE WE ALREADY KNOW THE FILE
	    if (currentWorkFile != null) {
		saveWork(currentWorkFile);
	    }
	    // OTHERWISE WE NEED TO PROMPT THE USER
	    else {
		handleSaveAsRequest();
	    }
        } catch (IOException ioe) {
	    
        }
    }
    
    public void handleSaveAsRequest() {
	// WE'LL NEED THIS TO GET CUSTOM STUFF
	PropertiesManager props = PropertiesManager.getPropertiesManager();
        try {
            
		// PROMPT THE USER FOR A FILE NAME
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File(PATH_WORK));
		fc.setTitle(props.getProperty(SAVE_WORK_TITLE));
		fc.getExtensionFilters().addAll(
		new ExtensionFilter(props.getProperty(WORK_FILE_EXT_DESC), props.getProperty(WORK_FILE_EXT)));

		File selectedFile = fc.showSaveDialog(app.getGUI().getWindow());
		if (selectedFile != null) {
		    saveWork(selectedFile);
		}
        } catch (IOException ioe) {
	    AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
	    dialog.show(props.getProperty(LOAD_ERROR_TITLE), props.getProperty(LOAD_ERROR_TITLE),
                    props.getProperty(LOAD_ERROR_MESSAGE));
        }
    }
    
    // HELPER METHOD FOR SAVING WORK
    private void saveWork(File selectedFile) throws IOException {
	// SAVE IT TO A FILE
	app.getFileComponent().saveData(app.getDataComponent(), selectedFile.getPath());
	
	// MARK IT AS SAVED
	currentWorkFile = selectedFile;
	saved = true;
	
	// TELL THE USER THE FILE HAS BEEN SAVED
	AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
	PropertiesManager props = PropertiesManager.getPropertiesManager();
        dialog.show(props.getProperty(SAVE_COMPLETED_TITLE), props.getProperty(SAVE_COMPLETED_TITLE),
                props.getProperty(SAVE_COMPLETED_MESSAGE));
		    
	// AND REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE
	// THE APPROPRIATE CONTROLS
	app.getGUI().updateToolbarControls(saved);	
    }
    
    public void handleAboutRequest(){
//          CHANGE TO THIS AFTER SUBMISSION FOR HW4.
        AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
        dialog.show("Information", "Information", "Welcome to Metro Map Maker!\n\n"
                + "The frameworks used are DesktopJavaFramework, PropertiesManager, and jTPS.\n\n"
                + "Programmer: Fanng Dai \n\nMinion: Joel George \n\nDesigner: Daniel Niyazov \n\n"
                + "Year of Development: 2017");
    }

    /**
     * This helper method verifies that the user really wants to save their
     * unsaved work, which they might not want to do. Note that it could be used
     * in multiple contexts before doing other actions, like creating new
     * work, or opening another file. Note that the user will be
     * presented with 3 options: YES, NO, and CANCEL. YES means the user wants
     * to save their work and continue the other action (we return true to
     * denote this), NO means don't save the work but continue with the other
     * action (true is returned), CANCEL means don't save the work and don't
     * continue with the other action (false is returned).
     *
     * @return true if the user presses the YES option to save, true if the user
     * presses the NO option to not save, false if the user presses the CANCEL
     * option to not continue.
     * @throws java.io.IOException
     */
    public boolean promptToSave() throws IOException {
	PropertiesManager props = PropertiesManager.getPropertiesManager();
	
	// CHECK TO SEE IF THE CURRENT WORK HAS
	// BEEN SAVED AT LEAST ONCE
	
        // PROMPT THE USER TO SAVE UNSAVED WORK
	AppConfirmDialogSingleton confirmDialog = AppConfirmDialogSingleton.getSingleton();
        confirmDialog.show(props.getProperty(SAVE_UNSAVED_WORK_TITLE), props.getProperty(SAVE_UNSAVED_WORK_MESSAGE));
        
        // AND NOW GET THE USER'S SELECTION
        String selection = confirmDialog.getSelection();

        // IF THE USER SAID YES, THEN SAVE BEFORE MOVING ON
        if (selection.equals(AppConfirmDialogSingleton.YES)) {
	    if (currentWorkFile == null) {
		// PROMPT THE USER FOR A FILE NAME
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File(PATH_WORK));
		fc.setTitle(props.getProperty(SAVE_WORK_TITLE));
		fc.getExtensionFilters().addAll(
		new ExtensionFilter(props.getProperty(WORK_FILE_EXT_DESC), props.getProperty(WORK_FILE_EXT)));

		File selectedFile = fc.showSaveDialog(app.getGUI().getWindow());
		if (selectedFile != null) {
		    saveWork(selectedFile);
		    saved = true;
		}
	    }
	    else {
		saveWork(currentWorkFile);
		saved = true;
	    }
        }
        return true;
    }

    /**
     * This helper method asks the user for a file to open. The user-selected
     * file is then loaded and the GUI updated. Note that if the user cancels
     * the open process, nothing is done. If an error occurs loading the file, a
     * message is displayed, but nothing changes.
     */
    private void promptToOpen() {
	// WE'LL NEED TO GET CUSTOMIZED STUFF WITH THIS
	PropertiesManager props = PropertiesManager.getPropertiesManager();
	
        // AND NOW ASK THE USER FOR THE FILE TO OPEN
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(PATH_WORK));
	fc.setTitle(props.getProperty(LOAD_WORK_TITLE));
        File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());

        // ONLY OPEN A NEW FILE IF THE USER SAYS OK
        if (selectedFile != null) {
            try {
                // RESET THE WORKSPACE
		app.getWorkspaceComponent().resetWorkspace();

                // RESET THE DATA
                app.getDataComponent().resetData();
                
                // LOAD THE FILE INTO THE DATA
                app.getFileComponent().loadData(app.getDataComponent(), selectedFile.getAbsolutePath());
                
		// MAKE SURE THE WORKSPACE IS ACTIVATED
		app.getWorkspaceComponent().activateWorkspace(app.getGUI().getAppPane());
                
                // AND MAKE SURE THE FILE BUTTONS ARE PROPERLY ENABLED
                saved = true;
                currentWorkFile = selectedFile;
                
                if(currentWorkFile != null)
                    app.getGUI().getWindow().setTitle(TITLE_STARTER + currentWorkFile.getName());
                
                app.getGUI().updateToolbarControls(saved);
            } catch (IOException e) {
                AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
                dialog.show(props.getProperty(LOAD_ERROR_TITLE), props.getProperty(LOAD_ERROR_TITLE),
                        props.getProperty(LOAD_ERROR_MESSAGE));
            }
        }
    }

    // Open the recent file
    public void handleRecentFile(File joel){
        // WE'LL NEED TO GET CUSTOMIZED STUFF WITH THIS
	PropertiesManager props = PropertiesManager.getPropertiesManager();
	
        try {
                // RESET THE WORKSPACE
		app.getWorkspaceComponent().resetWorkspace();

                // RESET THE DATA
                app.getDataComponent().resetData();
                
                // LOAD THE FILE INTO THE DATA
                app.getFileComponent().loadData(app.getDataComponent(), joel.getPath());
                
		// MAKE SURE THE WORKSPACE IS ACTIVATED
		app.getWorkspaceComponent().activateWorkspace(app.getGUI().getAppPane());
                
                // AND MAKE SURE THE FILE BUTTONS ARE PROPERLY ENABLED
                saved = true;
                currentWorkFile = joel;
                
                // Set the window title
                app.getGUI().getWindow().setTitle(TITLE_STARTER + currentWorkFile.getName());
                
                app.getGUI().updateToolbarControls(saved);
            } catch (IOException e) {
                AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
                dialog.show(props.getProperty(LOAD_ERROR_TITLE), props.getProperty(LOAD_ERROR_TITLE),
                        props.getProperty(LOAD_ERROR_MESSAGE));
            }
    }
    /**
     * This mutator method marks the file as not saved, which means that when
     * the user wants to do a file-type operation, we should prompt the user to
     * save current work first. Note that this method should be called any time
     * the course is changed in some way.
     */
    public void markFileAsNotSaved() {
        saved = false;
    }

    /**
     * Accessor method for checking to see if the current work has been saved
     * since it was last edited.
     *
     * @return true if the current work is saved to the file, false otherwise.
     */
    public boolean isSaved() {
        return saved;
    }
}
