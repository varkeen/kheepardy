package org.khee.kheepardygl;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.UI;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import org.khee.kheepardygl.scenes.TitleScene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Kheepardy extends GameApplication {

  private MainUIController mainUIController;

  public enum State {
    TITLE,
    START,
    ANSWER_CHOOSING,
    ANSWER_SELECTED,
    CLOCK_TICKING_ANSWER,
    CLOCK_OUT_OF_TIME_ANSWER,
    PLAYER_BUZZERED,
    END
  };

  public static final int MAX_PLAYERS = 4;

  private ArrayList<Category> activeCategories;

  private Controller joystickController;

  @Override
  protected void initSettings(GameSettings settings) {
    settings.setTitle("Kheepardy");
    settings.setVersion("0.1337");
    settings.setIntroEnabled(false);
    settings.setWidth(1280);
    settings.setHeight(811);
    settings.setScaleAffectedOnResize(true);
    settings.setManualResizeEnabled(true);
    settings.setPreserveResizeRatio(true);
    settings.setDeveloperMenuEnabled(false);
    settings.setProfilingEnabled(false);
    settings.setIntroEnabled(false);
    settings.set3D(false);
    settings.setExperimentalTiledLargeMap(true);
    settings.setGameMenuEnabled(false);
  }

  @Override
  protected void initGame() {

    // ye ye... will do this cleaner later
    //
    Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
    if (controllers.length == 0) {
      this.joystickController = null;
    } else {
      this.joystickController = controllers[0];
    }

    FXGL.play("drop.wav");

    Font.loadFont(
        Objects.requireNonNull(
                Kheepardy.class.getResource("/assets/ui/fonts/KdamThmorPro-Regular.ttf"))
            .toExternalForm(),
        10);
    Font.loadFont(
        Objects.requireNonNull(Kheepardy.class.getResource("/assets/ui/fonts/Roboto-Regular.ttf"))
            .toExternalForm(),
        10);

    Font.loadFont(
        Objects.requireNonNull(Kheepardy.class.getResource("/assets/ui/fonts/VT323-Regular.ttf"))
            .toExternalForm(),
        10);

    FXGL.getGameScene().setBackgroundColor(Color.BLACK);

    this.activeCategories = new ArrayList<>();

    Platform.runLater(
        () -> {
          try {

            FXMLLoader fxmlLoader =
                new FXMLLoader(Kheepardy.class.getResource("/assets/ui/admin.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
            int i = 0;
            for (KeyCode kc :
                new KeyCode[] {KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4}) {
              final int player = i;
              scene
                  .getAccelerators()
                  .put(
                      new KeyCodeCombination(kc),
                      () -> {
                        System.out.println("PLAYER " + (player+1));
                        Player.buzz(player);
                      });
              i++;
            }
            stage.setScene(scene);
            stage.show();
          } catch (IOException eIO) {
            eIO.printStackTrace();
          }
        });

    FXGL.getGameTimer()
        .runAtInterval(
            () -> {
              Event event = new Event();

              if (this.joystickController != null) {
                this.joystickController.poll();
                EventQueue queue = this.joystickController.getEventQueue();
                while (queue.getNextEvent(event)) {
                  if (event.getComponent().getName().equals("Base") && event.getValue() == 1.0f) {
                    Player.buzz(0);
                  }
                  if (event.getComponent().getName().equals("Base 2") && event.getValue() == 1.0f) {
                    Player.buzz(1);
                  }
                  if (event.getComponent().getName().equals("Base 3") && event.getValue() == 1.0f) {
                    Player.buzz(2);
                  }
                  if (event.getComponent().getName().equals("Base 4") && event.getValue() == 1.0f) {
                    Player.buzz(3);
                  }
                  if (event.getComponent().getName().equals("Base 5") && event.getValue() == 1.0f) {
                    Player.buzz(4);
                  }
                }
              }
            },
            Duration.millis(20));

    FXGL.getWorldProperties()
        .addListener(
            "state",
            (prev, now) -> {
              if (now == State.TITLE) {
                FXGL.getSceneService().pushSubScene(new TitleScene());
              }
              if (prev == State.TITLE) {
                FXGL.getSceneService().popSubScene();
              }
            });

    FXGL.getWorldProperties()
        .addListener(
            "answersLeft",
            (prev, now) -> {
              if (FXGL.geti("answersLeft") == 0) {
                System.out.println("No answers left, playing GAME OVER");
                FXGL.set("state", State.END);
              }
            });
  }

  @Override
  protected void initGameVars(Map<String, Object> vars) {
    vars.put("state", State.TITLE);
    vars.put("activeCategory", Category.NONE);
    vars.put("activeCategoryIndex", -1);
    vars.put("answersLeft", 0);
    vars.put("answersMax", 0);
    for (int i = 0; i < MAX_PLAYERS; i++) {
      new Player();
    }
    Player.lockBuzzers();
  }

  @Override
  protected void initUI() {

    this.mainUIController = new MainUIController();

    UI ui = FXGL.getAssetLoader().loadUI("main.fxml", mainUIController);

    FXGL.getGameScene().addUI(ui);

    int index;

    index = 0;
    for (Label label : this.mainUIController.getPlayerScores()) {
      label.textProperty().bind(Player.getPlayer(index++).scoreProperty().asString());
    }

    index = 0;
    for (Label label : this.mainUIController.getPlayerNames()) {
      label.textProperty().bind(Player.getPlayer(index++).nameProperty());
    }

    index = 0;
    for (ImageView imageView : this.mainUIController.getPlayerMoods()) {
      imageView.imageProperty().bind(Player.getPlayer(index++).moodProperty());
    }

    FXGL.runOnce(
        () -> {
          FXGL.getSceneService().pushSubScene(new TitleScene());
        },
        Duration.seconds(0.5));
  }

  @Override
  protected void onUpdate(double tpf) {

    for (Category category : this.activeCategories) {
      category.update(tpf);
    }

    //    this.mainUIController.update(tpf);

    //    if (updateScore) {
    //      int count = 0;
    //      for (int i = 0; i < this.playerScores.size(); i++) {
    //        if (this.playerScores.get(i).get() == this.playerScoreNew.get(i)) {
    //          count++;
    //          continue;
    //        }
    //        this.playerScores.get(i).set(this.playerScores.get(i).get() + 5);
    //      }
    //      if (count == this.playerScoreNew.size()) {
    //        this.updateScore = false;
    //      }
    //    }
  }

  public void updateCategories(ObservableList<Category> categories) {

    Player.resetScore();

    this.activeCategories.clear();
    CategoryControl.getInstance().deactivateAll();

    double x = FXGL.getGameScene().getAppWidth() / 2.0 - (205 * categories.size()) / 2.0;

    int answersLeft = 0;
    for (Category category : categories) {
      answersLeft += 5;
      category.init(x);
      x += 205;
      this.activeCategories.add(category);
    }

    FXGL.set("answersLeft", answersLeft);
  }

  public static void main(String[] args) throws IOException {
    CategoryControl.getInstance().loadCategories();
    launch(args);
  }
}
