package Test;

import javafx.application.*;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.util.*;

public class ScrollInterceptor extends Application {

  @Override
  public void start(Stage stage) {
    ScrollPane scrollPane = new ScrollPane(
      createScrollableContent()
    );

    Scene scene = new Scene(
      scrollPane,
      300, 200
    );

    remapArrowKeys(scrollPane);

    stage.setScene(scene);
    stage.show();

    hackToScrollToTopLeftCorner(scrollPane);
  }

  private void remapArrowKeys(ScrollPane scrollPane) {
    List<KeyEvent> mappedEvents = new ArrayList<>();
    scrollPane.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if (mappedEvents.remove(event))
          return;

        switch (event.getCode()) {
          case UP:
          case DOWN:
          case LEFT:
          case RIGHT:
            KeyEvent newEvent = remap(event);
            mappedEvents.add(newEvent);
            event.consume();
            Event.fireEvent(event.getTarget(), newEvent);
        }
      }

      private KeyEvent remap(KeyEvent event) {
        KeyEvent newEvent = new KeyEvent(
            event.getEventType(),
            event.getCharacter(),
            event.getText(),
            event.getCode(),
            !event.isShiftDown(),
            event.isControlDown(),
            event.isAltDown(),
            event.isMetaDown()
        );

        return newEvent.copyFor(event.getSource(), event.getTarget());
      }
    });
  }

  /**
   * For the tiles. Do not need to care about this
   * @return 
   */
  private TilePane createScrollableContent() {
    TilePane tiles = new TilePane();
    tiles.setPrefColumns(10);
    tiles.setHgap(5);
    tiles.setVgap(5);
    for (int i = 0; i < 100; i++) {
      Button button = new Button(i + "");
      button.setMaxWidth(Double.MAX_VALUE);
      button.setMaxHeight(Double.MAX_VALUE);
      tiles.getChildren().add(button);
    }
    return tiles;
  }

  private void hackToScrollToTopLeftCorner(final ScrollPane scrollPane) {
//    Platform.runLater(new Runnable() {
//      @Override
//      public void run() {
//        scrollPane.setHvalue(scrollPane.getHmin());
//        scrollPane.setVvalue(0);
//      }
//    });
  }

  public static void main(String[] args) {
    launch(args);
  }
}