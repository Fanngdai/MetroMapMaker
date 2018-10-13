/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ScrollWhileButtonDown extends Application {

    @Override
    public void start(Stage primaryStage) {
        ScrollPane scroller = new ScrollPane();
        Pane pane = new Pane();
        pane.setMinHeight(1000);
        scroller.setContent(pane);

        Button upButton = new Button("Up");
        Button downButton = new Button("Down");

        HBox controls = new HBox(10, upButton, downButton);
        controls.setPadding(new Insets(10));
        controls.setAlignment(Pos.CENTER);

        Scene scene = new Scene(new BorderPane(scroller, null, null, controls, null), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        final double scrollSpeed = 0.5 ; // scrollpane units per second

        AnimationTimer timer = new AnimationTimer() {

            private long lastUpdate = 0 ;
            @Override
            public void handle(long time) {
                if (lastUpdate > 0) {
                    long elapsedNanos = time - lastUpdate ;
                    double elapsedSeconds = elapsedNanos / 1_000_000_000.0 ;
                    double delta = 0 ;
                    if (upButton.isArmed()) {
                        delta = -scrollSpeed * elapsedSeconds ;
                    }
                    if (downButton.isArmed()) {
                        delta = scrollSpeed * elapsedSeconds ;
                    }
                    double newValue = 
                            clamp(scroller.getVvalue() + delta, scroller.getVmin(), scroller.getVmax());
                    scroller.setVvalue(newValue);
                }
                lastUpdate = time ;
            }
        };

        timer.start();
    }

    private double clamp(double value, double min, double max) {
        return Math.min(max, Math.max(min, value));
    }

    public static void main(String[] args) {
        launch(args);
    }
}