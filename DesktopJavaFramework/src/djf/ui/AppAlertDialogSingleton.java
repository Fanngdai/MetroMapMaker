package djf.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import static djf.settings.AppStartupConstants.OK_BUTTON_LABEL;
import static djf.settings.AppStartupConstants.TITLE_STARTER;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;

/**
 * This class serves to present custom text messages to the user when
 * events occur. Note that it always provides the same controls, a label
 * with a message, and a single Ok button. 
 * 
 * @author Richard McKenna
 * @author Fanng Dai
 * @version 1.0
 */
public class AppAlertDialogSingleton extends Stage {
    // HERE'S THE SINGLETON OBJECT
    static AppAlertDialogSingleton singleton = null;
    
    // HERE ARE THE DIALOG COMPONENTS
    Scene msgScene;
    VBox msgPane;
    Label msgLabel;
    ScrollPane scrollPane;
    Label msg;
    Button okButton;
    
    /**
     * Initializes this dialog so that it can be used repeatedly
     * for all kinds of messages. Note this is a singleton design
     * pattern so the constructor is private.
     * 
     * @param owner The owner stage of this modal dialoge.
     * 
     * @param closeButtonText Text to appear on the close button.
     */
    private AppAlertDialogSingleton() {}
    
    /**
     * A static accessor method for getting the singleton object.
     * 
     * @return The one singleton dialog of this object type.
     */
    public static AppAlertDialogSingleton getSingleton() {
	if (singleton == null)
	    singleton = new AppAlertDialogSingleton();
	return singleton;
    }
    
    /**
     * This function fully initializes the singleton dialog for use.
     * 
     * @param owner The window above which this dialog will be centered.
     */
    public void init(Stage owner) {
        // MAKE IT MODAL
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);
                
        // LABEL TO DISPLAY THE CUSTOM MESSAGE
        msgLabel = new Label();
        
        msg = new Label();
        msg.setWrapText(true);
        
        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxHeight(200);
        scrollPane.setContent(msg);

        // OK BUTTON
        okButton = new Button(OK_BUTTON_LABEL);
        okButton.setOnAction(e->{ AppAlertDialogSingleton.this.close(); });

        // WE'LL PUT EVERYTHING HERE
        msgPane = new VBox();
        msgPane.setAlignment(Pos.CENTER);
        msgPane.getChildren().addAll(msgLabel, scrollPane, okButton);
        
        // MAKE IT LOOK NICE
        msgPane.setPadding(new Insets(20, 10, 20, 10));
        msgPane.setSpacing(20);
                
        // AND PUT IT IN THE WINDOW
        msgScene = new Scene(msgPane);
        this.setMinHeight(200);
        this.setMaxHeight(300);
        this.setMinWidth(300);
        this.setMaxWidth(500);
        this.setScene(msgScene);
        this.setResizable(false);
        this.initStyle(StageStyle.UNDECORATED);
        
        msgPane.setBackground(new Background(new BackgroundFill(Color.web("0xE1E2E1"), CornerRadii.EMPTY, Insets.EMPTY)));
        msgLabel.setFont(new Font("Arial", 20));
        okButton.setBackground(new Background(new BackgroundFill(Color.web("0xC0C0C0"), CornerRadii.EMPTY, Insets.EMPTY)));
        okButton.setBorder(new Border(new BorderStroke(Color.web("0x525252"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        okButton.setMinWidth(100);
        okButton.setOnMouseEntered(e -> okButton.setStyle("-fx-background-color: #969696;"));
        okButton.setOnMouseExited(e -> okButton.setStyle("-fx-background-color: #C0C0C0;"));
    }
 
    /**
     * This method loads a custom message into the label and
     * then pops open the dialog.
     * 
     * @param title The title to appear in the dialog window.
     * @param messageLabel Message summary 
     * @param message Message to appear inside the dialog.
     */
    public void show(String title, String messageLabel, String message) {
	// SET THE DIALOG TITLE BAR TITLE
	setTitle(TITLE_STARTER + title);
        
        msgLabel.setText(messageLabel);
	
	// SET THE MESSAGE TO DISPLAY TO THE USER
        msg.setText(message);
	
	// AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
	// WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
	// DO MORE WORK.
        showAndWait();
    }
}