package djf.ui;

import static djf.settings.AppStartupConstants.TITLE_STARTER;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 * This class serves to present a dialog for adding and editing the name and color of metro line.
 * 
 * @author Richard McKenna
 * @author Fanng Dai
 * @version 1.0
 */
public class AppMetroLineDialogSingleton extends Stage {
    // HERE'S THE SINGLETON
    static AppMetroLineDialogSingleton singleton;
    
    // GUI CONTROLS FOR OUR DIALOG
    Scene msgScene;
    VBox msgPane;
    Label msgLabel;
    TextField input;
    HBox box;
    ColorPicker colorPicker;
    CheckBox circular;
    Button okButton;
    Button cancelButton;
    String selection;
    
    // CONSTANT CHOICES

    public static final String YES = "OK";
    public static final String CANCEL = "Cancel";
    
    boolean circularChecked = false;
    
    /**
     * Note that the constructor is private since it follows
     * the singleton design pattern.
     * 
     * @param primaryStage The owner of this modal dialog.
     */
    private AppMetroLineDialogSingleton() {}
    
    /**
     * The static accessor method for this singleton.
     * 
     * @return The singleton object for this type.
     */
    public static AppMetroLineDialogSingleton getSingleton() {
	if (singleton == null)
	    singleton = new AppMetroLineDialogSingleton();
	return singleton;
    }
    
    /**
     * This method initializes the singleton for use.
     * 
     * @param primaryStage The window above which this
     * dialog will be centered.
     */
    public void init(Stage primaryStage) {
        // MAKE THIS DIALOG MODAL, MEANING OTHERS WILL WAIT
        // FOR IT WHEN IT IS DISPLAYED
        initModality(Modality.WINDOW_MODAL);
        initOwner(primaryStage);
        
        // LABEL TO DISPLAY THE CUSTOM MESSAGE
        msgLabel = new Label("Metro Line Details");        

        input = new TextField();
        input.setPrefWidth(400);
        input.setPromptText("Enter Line Name");
        
        colorPicker = new ColorPicker(Color.WHITE);
        colorPicker.getStyleClass().add("button");
        
        circular = new CheckBox("Circular");
        
        Region spacerBox = new Region();
        HBox.setHgrow(spacerBox, Priority.ALWAYS);
        
        box = new HBox();
        box.getChildren().addAll(colorPicker, spacerBox, circular);
        
        
        // YES AND CANCEL BUTTONS
        okButton = new Button(YES);
        cancelButton = new Button(CANCEL);
	
	// MAKE THE EVENT HANDLER FOR THESE BUTTONS
        EventHandler<ActionEvent> yesNoCancelHandler = (ActionEvent ae) -> {
            Button sourceButton = (Button)ae.getSource();
            AppMetroLineDialogSingleton.this.selection = sourceButton.getText();
            AppMetroLineDialogSingleton.this.hide();
        };
        
	// AND THEN REGISTER THEM TO RESPOND TO INTERACTIONS
        okButton.setOnAction(yesNoCancelHandler);
        cancelButton.setOnAction(yesNoCancelHandler);

        // NOW ORGANIZE OUR BUTTONS
        HBox buttonBox = new HBox();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        buttonBox.getChildren().addAll(okButton, spacer, cancelButton);
        
        // WE'LL PUT EVERYTHING HERE
        msgPane = new VBox();
        msgPane.setAlignment(Pos.CENTER);
        msgPane.getChildren().addAll(msgLabel, input, box, buttonBox);
        
        // MAKE IT LOOK NICE
        msgPane.setPadding(new Insets(10, 20, 20, 20));
        msgPane.setSpacing(20);
        
        // AND PUT IT IN THE WINDOW
        msgScene = new Scene(msgPane);
        this.setScene(msgScene);
        this.setResizable(false);
        this.initStyle(StageStyle.UNDECORATED);
        
        msgPane.setBackground(new Background(new BackgroundFill(Color.web("0xE1E2E1"), CornerRadii.EMPTY, Insets.EMPTY)));
        msgLabel.setFont(new Font("Arial", 20));
        okButton.setBackground(new Background(new BackgroundFill(Color.web("0xC0C0C0"), CornerRadii.EMPTY, Insets.EMPTY)));
        okButton.setBorder(new Border(new BorderStroke(Color.web("0x525252"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        okButton.setOnMouseEntered(e -> okButton.setStyle("-fx-background-color: #969696;"));
        okButton.setOnMouseExited(e -> okButton.setStyle("-fx-background-color: #C0C0C0;"));
        cancelButton.setBackground(new Background(new BackgroundFill(Color.web("0xC0C0C0"), CornerRadii.EMPTY, Insets.EMPTY)));
        cancelButton.setBorder(new Border(new BorderStroke(Color.web("0x525252"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #969696;"));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: #C0C0C0;"));
    }

    /**
     * Accessor method for getting the selection the user made.
     * 
     * @return Either YES, NO, or CANCEL, depending on which
     * button the user selected when this dialog was presented.
     */
    public String getSelection() {
        return selection;
    }
 
    public String showAddLine() {
	// SET THE DIALOG TITLE BAR TITLE
	setTitle(TITLE_STARTER +  "Metro Line Add");
	
	// SET THE MESSAGE TO DISPLAY TO THE USER
        input.setText("");
        colorPicker.setValue(Color.WHITE);
        circular.setSelected(false);
	
	// AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
	// WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
	// DO MORE WORK.
        showAndWait();
        
        circularChecked = circular.isSelected();
        
        return input.getText();
    }
    
    /**
     * This method loads a custom message into the label
     * then pops open the dialog.
     * 
     * @param title The title to appear in the dialog window bar.
     * 
     * @param message Message to appear inside the dialog.
     * @return 
     */
    public String showEditLine(String metroName, Color color, boolean isCircular) {
	// SET THE DIALOG TITLE BAR TITLE
	setTitle(TITLE_STARTER + "Metro Line Edit");
	
	// SET THE MESSAGE TO DISPLAY TO THE USER
        input.setText(metroName);
        colorPicker.setValue(color);
        circular.setSelected(isCircular);
	
	// AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
	// WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
	// DO MORE WORK.
        showAndWait();
        
        circularChecked = circular.isSelected();
        
        return input.getText();
    }
    
    public Color getColor(){
        return colorPicker.getValue();
    }
    
    public boolean getCircularChecked(){
        return circularChecked;
    }
}