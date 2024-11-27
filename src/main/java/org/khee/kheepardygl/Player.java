package org.khee.kheepardygl;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.UIController;
import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Player {

  public static final Image MOOD_COCKY = FXGL.getAssetLoader().loadImage("player-cocky.png");
  public static final Image MOOD_CRYING = FXGL.getAssetLoader().loadImage("player-cry.png");
  public static final Image MOOD_MAD = FXGL.getAssetLoader().loadImage("player-mad.png");
  public static final Image MOOD_NORMAL = FXGL.getAssetLoader().loadImage("player-normal.png");
  public static final Image MOOD_OHNO = FXGL.getAssetLoader().loadImage("player-ohno.png");
  public static final Image MOOD_SMIRKY = FXGL.getAssetLoader().loadImage("player-smirky.png");
  public static final Image MOOD_GHOST = FXGL.getAssetLoader().loadImage("player-ghost.png");
  private static final ArrayList<Player> players = new ArrayList<>();

  private static final ArrayList<PlayerBuzzListener> playerBuzzListeners = new ArrayList<>();
  private static final ArrayList<PlayerUnbuzzListener> playerUnbuzzListeners = new ArrayList<>();
  private static Player active;

  private static int lastId = 0;
  private final SimpleStringProperty name = new SimpleStringProperty();
  private final SimpleIntegerProperty score = new SimpleIntegerProperty();
  private final ObjectProperty<Image> mood = new SimpleObjectProperty<>();
  private final SimpleBooleanProperty buzzerLocked = new SimpleBooleanProperty();

  private final int id;

  public Player() {
    this.name.set("");
    this.score.set(0);
    this.mood.set(MOOD_GHOST);
    this.id = Player.lastId++;
    Player.players.add(this);
  }

  public static Player getPlayer(int index) {
    return players.get(index);
  }

  public static List<Player> getPlayers() {
    return players;
  }

  public static void setActive(Player player) {
    active = player;
  }

  public static void buzz(int index) {

    if ( Player.getPlayers().get(index).isBuzzerLocked()) {
      return;
    }

    if (Player.getActive() != null) {
      return;
    }

    FXGL.play("pickup-01.wav");
    Player.setActive(index);
    Player.getActive().setBuzzerLocked(true);

    System.out.println("Buzz: " + Player.getActiveIndex());

    for (PlayerBuzzListener playerBuzzListener : playerBuzzListeners) {
      playerBuzzListener.playerBuzzed(Player.getActive(), index);
    }

    if (FXGL.geto("state") == Kheepardy.State.CLOCK_TICKING_ANSWER) {
      FXGL.set("state", Kheepardy.State.PLAYER_BUZZERED);
    }
  }

  public static void unbuzz() {

    if (Player.getActive() == null) {
      return;
    }

    FXGL.play("craft-00.wav");

    for (PlayerUnbuzzListener playerUnbuzzListener : playerUnbuzzListeners) {
      playerUnbuzzListener.playerUnbuzzed(Player.getActive(), Player.getActiveIndex());
    }
    Player.setActive(null);
  }

  public static void lockBuzzers() {
    System.out.println("Buzzers locked");
    for (Player player : Player.players) {
      player.setBuzzerLocked(true);
    }
  }

  public static void unlockBuzzers() {
    System.out.println("Buzzers unlocked");
    for (Player player : Player.players) {
      player.setBuzzerLocked(false);
    }
  }

  public static void addPlayerBuzzListener(PlayerBuzzListener playerBuzzListener) {
    playerBuzzListeners.add(playerBuzzListener);
  }

  public static void addPlayerUnbuzzListener(PlayerUnbuzzListener playerUnbuzzListener) {
    playerUnbuzzListeners.add(playerUnbuzzListener);
  }

  private static List<Player> getSortedByScore() {
    ArrayList<Player> sorted = new ArrayList<>(players);
    sorted.sort((o1, o2) -> o1.scoreProperty().getValue().compareTo(o2.getScore()));
    return sorted;
  }

  public static Player getLast() {
    return getSortedByScore().get(0).getScore() == getSortedByScore().get(1).getScore()
        ? null
        : getSortedByScore().get(0);
  }

  public static Player getLeading() {
    final int first = getSortedByScore().size() - 1;
    final int second = getSortedByScore().size() - 2;
    return getSortedByScore().get(first).getScore() == getSortedByScore().get(second).getScore()
        ? null
        : getSortedByScore().get(first);
  }

  public static void setActive(int index) {
    active = players.get(index);
  }

  public static int getActiveIndex() {
    return players.indexOf(active);
  }

  public static void resetScore() {
    for (Player player : Player.players) {
      player.setScore(0);
    }
  }

  public static Player getActive() {
    return active;
  }

  public String getName() {
    return name.get();
  }

  public SimpleStringProperty nameProperty() {
    return name;
  }

  public void setName(String name) {
    this.name.set(name);
  }

  public int getScore() {
    return score.get();
  }

  public SimpleIntegerProperty scoreProperty() {
    return score;
  }

  public void setScore(int score) {
    this.score.set(score);
  }

  public void incScore(int score) {
    this.score.set(this.getScore() + score);
  }

  public Image getMood() {
    return mood.get();
  }

  public ObjectProperty<Image> moodProperty() {
    return mood;
  }

  public void setMood(Image mood) {
    this.mood.set(mood);
  }

  public boolean isBuzzerLocked() {
    return buzzerLocked.get();
  }

  public SimpleBooleanProperty buzzerLockedProperty() {
    return buzzerLocked;
  }

  public void setBuzzerLocked(boolean buzzerLocked) {
    this.buzzerLocked.set(buzzerLocked);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Player player = (Player) o;
    return id == ((Player) o).id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
