package org.khee.kheepardygl;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.FXGLForKtKt;
import com.almasb.fxgl.ui.UIController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainUIController implements UIController {

  @FXML private List<Label> playerNames;
  @FXML private List<Label> playerScores;
  @FXML private List<ImageView> playerMoods;

  @FXML private List<GridPane> playerBoxes;
  @FXML private Clock clock;

  private ArrayList<Animation<?>> animations = new ArrayList<>();

  @Override
  public void init() {

    playerNames.forEach(
        label -> {
          label.setStyle("-fx-font-size: 24 ; -fx-font-family: 'Kdam Thmor Pro'");
        });

    for (Label playerScore : playerScores) {
      playerScore.setStyle("-fx-font-size: 48 ; -fx-font-family: 'VT323'");
    }

    for (int i = 0; i < Kheepardy.MAX_PLAYERS; i++) {
      int index = i;
      Player player = Player.getPlayer(i);
      player
          .scoreProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                this.updatePlayerMoods();
              });
      player
          .nameProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                player.setMood(newValue.equals("") ? Player.MOOD_GHOST : Player.MOOD_NORMAL);
              });
      player
          .buzzerLockedProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                if (newValue.equals(true)) {
                  this.playerNames.get(index).setStyle("-fx-font-size: 24 ; -fx-font-family: 'Kdam Thmor Pro' ; -fx-text-fill: gray");
                } else {
                  this.playerNames.get(index).setStyle("-fx-font-size: 24 ; -fx-font-family: 'Kdam Thmor Pro' ; -fx-text-fill: white");
                }
              });
    }

    Player.addPlayerBuzzListener(
        (player, index) -> {
          this.animatePlayerBox(index);
        });

    Player.addPlayerUnbuzzListener(
        (player, index) -> {
          this.deanimatePlayerBox(index);
        });

    this.initializeStates();
    this.updatePlayerMoods();
  }

  public void animatePlayerBox(int index) {
    this.playerBoxes.get(index).setStyle("-fx-background-color: #772222");
  }

  public void deanimatePlayerBox(int index) {
    this.playerBoxes
        .get(index)
        .setStyle((index % 2) == 0 ? "-fx-background-color: #111" : "-fx-background-color: #222");
  }

  private void updatePlayerMoods() {

    if (Player.getLeading() == null && Player.getLast() == null) {
      Player.getPlayers()
          .forEach(
              player -> {
                player.setMood(
                    player.getName().equals("") ? Player.MOOD_GHOST : Player.MOOD_NORMAL);
              });
    }
    if (Player.getLeading() != null) {
      Player.getLeading().setMood(Player.MOOD_COCKY);
    }
    if (Player.getLast() != null) {
      Player.getLast().setMood(Player.MOOD_CRYING);
    }
  }

  private void initializeStates() {

    FXGL.getWorldProperties()
        .addListener(
            "state",
            (prev, now) -> {
              if (now == Kheepardy.State.CLOCK_TICKING_ANSWER) {
                clock.startTimer(
                    20,
                    () -> {
                      FXGL.set("state", Kheepardy.State.CLOCK_OUT_OF_TIME_ANSWER);
                    });
              }
              if (now == Kheepardy.State.PLAYER_BUZZERED) {
                clock.stopTimer();
              }
            });
  }

  public void setMood(int index, Image mood) {
    this.playerMoods.get(index).setImage(mood);
  }

  public void update(double tpf) {
    this.animations.forEach(a -> a.onUpdate(tpf));
  }

  public List<Label> getPlayerNames() {
    return playerNames;
  }

  public List<Label> getPlayerScores() {
    return playerScores;
  }

  public List<ImageView> getPlayerMoods() {
    return playerMoods;
  }
}
