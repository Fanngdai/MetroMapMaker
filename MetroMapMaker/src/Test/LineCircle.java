/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
//from  w w  w. j av a2 s. c o  m
public class LineCircle extends Application {

  double orgSceneX, orgSceneY;

  private Circle createCircle(double x, double y, double r, Color color) {
    Circle circle = new Circle(x, y, r, color);

    circle.setCursor(Cursor.HAND);

    circle.setOnMousePressed((t) -> {
      orgSceneX = t.getSceneX();
      orgSceneY = t.getSceneY();

      Circle c = (Circle) (t.getSource());
      c.toFront();
    });
    circle.setOnMouseDragged((t) -> {
      double offsetX = t.getSceneX() - orgSceneX;
      double offsetY = t.getSceneY() - orgSceneY;

      Circle c = (Circle) (t.getSource());

      c.setCenterX(c.getCenterX() + offsetX);
      c.setCenterY(c.getCenterY() + offsetY);

      orgSceneX = t.getSceneX();
      orgSceneY = t.getSceneY();
    });
    return circle;
  }

  private Line connect(Circle c1, Circle c2) {
    Line line = new Line();

    line.startXProperty().bind(c1.centerXProperty());
    line.startYProperty().bind(c1.centerYProperty());

    line.endXProperty().bind(c2.centerXProperty());
    line.endYProperty().bind(c2.centerYProperty());

    line.setStrokeWidth(2);
    line.setStrokeLineCap(StrokeLineCap.ROUND);

    return line;
  }

  @Override
  public void start(Stage primaryStage) {
    Group root = new Group();
    Scene scene = new Scene(root, 500, 260);

    // circles
    Circle redCircle = createCircle(100, 50, 30, Color.TRANSPARENT);
    Circle blueCircle = createCircle(20, 150, 20, Color.TRANSPARENT);
    
    DropShadow dropShadowEffect = new DropShadow();
    dropShadowEffect.setOffsetX(0.0f);
    dropShadowEffect.setOffsetY(0.0f);
    dropShadowEffect.setSpread(1.0);
    dropShadowEffect.setColor(Color.YELLOW);
    dropShadowEffect.setBlurType(BlurType.GAUSSIAN);
    dropShadowEffect.setRadius(15);
    Effect highlightedEffect = dropShadowEffect;
    
    redCircle.setEffect(highlightedEffect);
    blueCircle.setEffect(highlightedEffect);

    Line line1 = connect(redCircle, blueCircle);

    // add the circles
    root.getChildren().add(redCircle);
    root.getChildren().add(blueCircle);
    root.getChildren().add(line1);
    
    // bring the circles to the front of the lines
    redCircle.toFront();
    blueCircle.toFront();

    primaryStage.setScene(scene);
    primaryStage.show();
  }
  public static void main(String[] args) {
    launch(args);
  }
}