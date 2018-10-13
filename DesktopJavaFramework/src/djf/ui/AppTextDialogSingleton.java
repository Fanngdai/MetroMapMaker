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
 * This class serves to present a dialog with three options to
 * the user: Yes, No, or Cancel and lets one access which was
 * selected.
 * 
 * @author Richard McKenna
 * @author Fanng Dai
 * @version 1.0
 */
public class AppTextDialogSingleton extends Stage {
    // HERE'S THE SINGLETON
    static AppTextDialogSingleton singleton;
    
    // GUI CONTROLS FOR OUR DIALOG
        Scene msgScene;
    VBox msgPane;
    Label msgLabel;
    TextField input;
    Button yesButton;
    Button cancelButton;
    String selection;
    
    // CONSTANT CHOICES

    public static final String YES = "OK";
    public static final String CANCEL = "Cancel";
    
    /**
     * Note that the constructor is private since it follows
     * the singleton design pattern.
     * 
     * @param primaryStage The owner of this modal dialog.
     */
    private AppTextDialogSingleton() {}
    
    /**
     * The static accessor method for this singleton.
     * 
     * @return The singleton object for this type.
     */
    public static AppTextDialogSingleton getSingleton() {
	if (singleton == null)
	    singleton = new AppTextDialogSingleton();
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

        msgLabel = new Label();
        msgLabel.setWrapText(true);
        input = new TextField();
        
        // YES AND CANCEL BUTTONS
        yesButton = new Button(YES);
        cancelButton = new Button(CANCEL);
	
	// MAKE THE EVENT HANDLER FOR THESE BUTTONS
        EventHandler<ActionEvent> yesNoCancelHandler = (ActionEvent ae) -> {
            Button sourceButton = (Button)ae.getSource();
            AppTextDialogSingleton.this.selection = sourceButton.getText();
            AppTextDialogSingleton.this.hide();
        };
        
	// AND THEN REGISTER THEM TO RESPOND TO INTERACTIONS
        yesButton.setOnAction(yesNoCancelHandler);
        cancelButton.setOnAction(yesNoCancelHandler);

        // NOW ORGANIZE OUR BUTTONS
        HBox buttonBox = new HBox();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        buttonBox.getChildren().addAll(yesButton, spacer, cancelButton);
        
        // WE'LL PUT EVERYTHING HERE
        msgPane = new VBox();
        msgPane.setAlignment(Pos.CENTER);
        msgPane.getChildren().addAll(msgLabel, input, buttonBox);
        
        // MAKE IT LOOK NICE
        msgPane.setPadding(new Insets(10, 20, 20, 20));
        msgPane.setSpacing(30);
        
        // AND PUT IT IN THE WINDOW
        msgScene = new Scene(msgPane, 350, 200);
        this.setScene(msgScene);
        this.setResizable(false);
        this.initStyle(StageStyle.UNDECORATED);
        
        msgPane.setBackground(new Background(new BackgroundFill(Color.web("0xE1E2E1"), CornerRadii.EMPTY, Insets.EMPTY)));
        msgLabel.setFont(new Font("Arial", 20));
        yesButton.setBackground(new Background(new BackgroundFill(Color.web("0xC0C0C0"), CornerRadii.EMPTY, Insets.EMPTY)));
        yesButton.setBorder(new Border(new BorderStroke(Color.web("0x525252"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        yesButton.setOnMouseEntered(e -> yesButton.setStyle("-fx-background-color: #969696;"));
        yesButton.setOnMouseExited(e -> yesButton.setStyle("-fx-background-color: #C0C0C0;"));
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
 
    /**
     * This method loads a custom message into the label
     * then pops open the dialog.
     * 
     * @param title
     *  The title to set window
     * @param messageLabel
     *  The message. Tell user what to do
     * @param prompt
     *  What to type in to text field.
     * @return 
     *  The String the user typed.
     */
    public String showAdd(String title, String messageLabel, String prompt) {
	// SET THE DIALOG TITLE BAR TITLE
        setTitle(TITLE_STARTER + title);
        msgLabel.setText(messageLabel);
        // Empty out the text
        input.setText("");
        input.setPromptText(prompt);
	
	// AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
	// WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
	// DO MORE WORK.?
        showAndWait();
        
        return input.getText();
    }
    
    /**
     * This method loads a custom message into the label and sets the textfield value to
     * the value of the text being edited.
     * then pops open the dialog.
     * 
     * @param title
     *  The title of the window.
     * @param messageLabel
     *  The message for the user. What you doing
     * @param inputField
     *  The value of the text which we will be editing
     * @param prompt
     *  What to promt the user. In the text field
     * @return 
     *  The new string value of the text
     */
    public String showEdit(String title, String messageLabel, String inputField, String prompt) {
	// SET THE DIALOG TITLE BAR TITLE
        setTitle(TITLE_STARTER + title);
        msgLabel.setText(messageLabel);
        input.setText(inputField);
        input.setPromptText(prompt);
	
	// AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
	// WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
	// DO MORE WORK.?
        showAndWait();
        
        return input.getText();
    }
}