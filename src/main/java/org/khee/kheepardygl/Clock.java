package org.khee.kheepardygl;

import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.dsl.FXGL;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;

public class Clock extends Region {

  private static final Color COLOR_ON = Color.web("#cc0000");
  private static final Color COLOR_OFF = Color.BLACK;

  private final ArrayList<Circle> clockDotsLeft;
  private final ArrayList<Circle> clockDotsRight;

  private final SimpleBooleanProperty timerRunning = new SimpleBooleanProperty();

  private int currentSeconds = 0;
  private int maxSeconds = 0;

  public Clock() {

    this.clockDotsLeft = new ArrayList<>();
    this.clockDotsRight = new ArrayList<>();

    Rectangle rect = new Rectangle();
    rect.setX(-100);
    rect.setY(0);
    rect.setWidth(FXGL.getGameScene().getAppWidth() + 200);
    rect.setHeight(32);
    rect.setArcWidth(10);
    rect.setArcHeight(10);
    rect.setFill(Color.web("#004cad"));

    Text textClock = new Text("C-LOCK 1.0");
    textClock.setX(10);
    textClock.setY(22);
    textClock.setStyle(
        "-fx-fill: #008cfd ; -fx-font-size: 14 ; -fx-font-family: 'Kdam Thmor Pro'");

    this.getChildren().add(rect);
    this.getChildren().add(textClock);

    for (int i = 0; i < 5; i++) {
      for (int j = -1; j <= 1; j += 2) {

        Circle dot = new Circle();
        dot.setLayoutY(16);
        dot.setLayoutX(FXGL.getGameScene().getAppWidth() / 2.0 + j * i * 100);
        dot.setRadius(12);

        if (j == -1) {
          this.clockDotsLeft.add(dot);
        } else {
          this.clockDotsRight.add(dot);
        }
        this.getChildren().add(dot);
      }
    }
  }

  public void setTime(int actual, int max) {

    long numDots = 5 - Math.round((double) actual / (double) max * 5.0);

    for (int i = 0; i < 5; i++) {

      if (i < numDots) {
        this.clockDotsLeft.get(i).setFill(COLOR_ON);
        this.clockDotsRight.get(i).setFill(COLOR_ON);
      } else {
        this.clockDotsLeft.get(i).setFill(COLOR_OFF);
        this.clockDotsRight.get(i).setFill(COLOR_OFF);
      }
    }
  }

  public void startTimer(int seconds, Runnable onFinished) {

    this.stopTimer();

    Music theme = FXGL.getAssetLoader().loadMusic("theme.mp3");
    FXGL.getAudioPlayer().loopMusic(theme);

    this.maxSeconds = seconds;
    this.currentSeconds = 0;
    this.timerRunning.set(true);

    FXGL.getSettings().setGlobalSoundVolume(0.2);
    FXGL.getSettings().setGlobalMusicVolume(1.0);

    FXGL.getGameTimer()
        .runAtIntervalWhile(
            () -> {
              FXGL.play("menu-navigate-03.wav");

              this.setTime(this.currentSeconds, this.maxSeconds);
              if (this.currentSeconds == this.maxSeconds) {
                FXGL.play("fail.wav");
                this.stopTimer();
                onFinished.run();
              }
              this.currentSeconds++;
            },
            Duration.seconds(1),
            this.timerRunning);
  }

  public void stopTimer() {
    FXGL.getAudioPlayer().stopAllMusic();
    this.timerRunning.set(false);
    this.setTime(5, 5);
    FXGL.getSettings().setGlobalSoundVolume(0.5);
    FXGL.getSettings().setGlobalMusicVolume(0.5);
  }
}
