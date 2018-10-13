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
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 * This class serves to present a dialog with three options to
 * the user: Yes, No, or Cancel and lets one access which was
 * selected.
 * 
 * @author Richard McKenna
 * @version 1.0
 */
public class AppConfirmDialogSingleton extends Stage {
    // HERE'S THE SINGLETON
    static AppConfirmDialogSingleton singleton;
    
    // GUI CONTROLS FOR OUR DIALOG
    Scene msgScene;
    VBox msgPane;
    Label msgLabel;
    Button yesButton;
    Button noButton;
    String selection;
    
    // CONSTANT CHOICES

    public static final String YES = "Yes";
    public static final String NO = "No";
    
    /**
     * Note that the constructor is private since it follows
     * the singleton design pattern.
     * 
     * @param primaryStage The owner of this modal dialog.
     */
    private AppConfirmDialogSingleton() {}
    
    /**
     * The static accessor method for this singleton.
     * 
     * @return The singleton object for this type.
     */
    public static AppConfirmDialogSingleton getSingleton() {
	if (singleton == null)
	    singleton = new AppConfirmDialogSingleton();
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
        msgLabel = new Label();
        msgLabel.setWrapText(true);

        // YES, NO, AND CANCEL BUTTONS
        yesButton = new Button(YES);
        noButton = new Button(NO);
	
	// MAKE THE EVENT HANDLER FOR THESE BUTTONS
        EventHandler<ActionEvent> yesNoHandler = (ActionEvent ae) -> {
            Button sourceButton = (Button)ae.getSource();
            AppConfirmDialogSingleton.this.selection = sourceButton.getText();
            AppConfirmDialogSingleton.this.hide();
        };
        
	// AND THEN REGISTER THEM TO RESPOND TO INTERACTIONS
        yesButton.setOnAction(yesNoHandler);
        noButton.setOnAction(yesNoHandler);

        // NOW ORGANIZE OUR BUTTONS
        HBox buttonBox = new HBox();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        buttonBox.getChildren().addAll(yesButton,spacer, noButton);
        
        // WE'LL PUT EVERYTHING HERE
        msgPane = new VBox();
        msgPane.setAlignment(Pos.CENTER);
        msgPane.getChildren().addAll(msgLabel, buttonBox);
        
        // MAKE IT LOOK NICE
        msgPane.setPadding(new Insets(10, 20, 20, 20));
        msgPane.setSpacing(10);

        // AND PUT IT IN THE WINDOW
        msgScene = new Scene(msgPane);
        this.setScene(msgScene);
        this.setMaxHeight(300);
        this.setMaxWidth(500);
        this.setResizable(false);
        this.initStyle(StageStyle.UNDECORATED);
        
        msgPane.setBackground(new Background(new BackgroundFill(Color.web("0xE1E2E1"), CornerRadii.EMPTY, Insets.EMPTY)));
        yesButton.setBackground(new Background(new BackgroundFill(Color.web("0xC0C0C0"), CornerRadii.EMPTY, Insets.EMPTY)));
        yesButton.setBorder(new Border(new BorderStroke(Color.web("0x525252"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        yesButton.setOnMouseEntered(e -> yesButton.setStyle("-fx-background-color: #969696;"));
        yesButton.setOnMouseExited(e -> yesButton.setStyle("-fx-background-color: #C0C0C0;"));
        noButton.setBackground(new Background(new BackgroundFill(Color.web("0xC0C0C0"), CornerRadii.EMPTY, Insets.EMPTY)));
        noButton.setBorder(new Border(new BorderStroke(Color.web("0x525252"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        noButton.setOnMouseEntered(e -> noButton.setStyle("-fx-background-color: #969696;"));
        noButton.setOnMouseExited(e -> noButton.setStyle("-fx-background-color: #C0C0C0;"));
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
     * @param title The title to appear in the dialog window bar.
     * 
     * @param message Message to appear inside the dialog.
     */
    public void show(String title, String message) {
	// SET THE DIALOG TITLE BAR TITLE
	setTitle(TITLE_STARTER + title);
	
	// SET THE MESSAGE TO DISPLAY TO THE USER
        msgLabel.setText(message);
	
	// AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
	// WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
	// DO MORE WORK.
        showAndWait();
    }
}