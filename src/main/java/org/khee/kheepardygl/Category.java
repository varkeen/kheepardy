package org.khee.kheepardygl;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.animation.Interpolator;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class Category {

  public static final Category NONE = new Category("");

  private String name;
  private ArrayList<Answer> answers;
  @JsonIgnore private Entity headerEntity;
  @JsonIgnore private ArrayList<Entity> answerEntities;
  @JsonIgnore private ArrayList<Entity> valueEntities;

  @JsonIgnore private int activatedAnswer;
  private Point2D posActivatedAnswer;

  @JsonIgnore private String path;

  private ArrayList<Animation<?>> animations;

  public Category() {
    this.answers = new ArrayList<>();
    this.answerEntities = new ArrayList<>();
    this.valueEntities = new ArrayList<>();
    this.animations = new ArrayList<>();
  }

  public Category(String name) {
    this.name = name;
    this.answers = new ArrayList<>();
    this.answerEntities = new ArrayList<>();
    this.valueEntities = new ArrayList<>();
    this.animations = new ArrayList<>();
  }

  public void init(double x) {

    this.headerEntity =
        FXGL.entityBuilder()
            .at(x, 0)
            .view(
                new Cell(
                    this.name, Color.web("444422"), Color.web("777722"), 26, "'Kdam Thmor Pro'"))
            .buildAndAttach();

    for (int row = 1; row < 6; row++) {

      Entity valueEntity =
          FXGL.entityBuilder()
              .at(x, row * 105)
              .view(
                  new Cell(
                      Integer.toString(row * 100),
                      Color.web("002c8d"),
                      Color.web("1960ff"),
                      28,
                      "Roboto"))
              .buildAndAttach();
      this.valueEntities.add(valueEntity);

      String answer = this.answers.get(row - 1).getAnswer();

      Entity answerEntity = null;

      if (answer.contains(".jpg") || answer.contains(".png")) {

        try {
          String imageUrl = new File(this.path + "/" + answer).toURI().toURL().toString();
          System.out.println(imageUrl);
          answerEntity =
              FXGL.entityBuilder()
                  .at(x, row * 105)
                  .view(new Cell(new Image(imageUrl), Color.web("002c8d"), Color.web("1960ff")))
                  .buildAndAttach();
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }
      } else {
        answerEntity =
            FXGL.entityBuilder()
                .at(x, row * 105)
                .view(new Cell(answer, Color.web("002c8d"), Color.web("1960ff"), null, null))
                .buildAndAttach();
      }
      answerEntity.setVisible(false);
      this.answerEntities.add(answerEntity);
    }
  }

  public void deactivate() {
    if (this.headerEntity != null) {
      this.headerEntity.removeFromWorld();
      this.headerEntity = null;
    }

    for (Entity entity : this.valueEntities) {
      entity.removeFromWorld();
    }
    this.valueEntities.clear();

    for (Entity entity : this.answerEntities) {
      entity.removeFromWorld();
    }
    this.answerEntities.clear();
  }

  private void animateValueFadeout() {

    Entity e = this.valueEntities.get(this.activatedAnswer);

    FXGL.getGameTimer()
        .runOnceAfter(
            () -> {
              FXGL.play("collect-point-01.wav");
              var animation =
                  FXGL.animationBuilder()
                      .onFinished(this::animateAnswerGrow)
                      .duration(Duration.seconds(0.3))
                      .repeat(2)
                      .fadeOut(e)
                      .to(0.3)
                      .build();

              this.animations.add(animation);
              animation.start();
            },
            Duration.seconds(0));
  }

  private void animateValueFadein() {

    this.answerEntities.get(this.activatedAnswer).setVisible(false);

    Entity e = this.valueEntities.get(this.activatedAnswer);
    e.setVisible(true);

    FXGL.getGameTimer()
        .runOnceAfter(
            () -> {
              var animation =
                  FXGL.animationBuilder()
                      .onFinished(
                          () -> {
                            CategoryControl.getInstance().setBusy(false);
                          })
                      .duration(Duration.seconds(0.3))
                      .fadeIn(e)
                      .build();

              this.animations.add(animation);
              animation.start();
            },
            Duration.seconds(0));
  }

  private void animateAnswerGrow() {

    //    this.valueEntities.get(this.activatedAnswer).setVisible(false);

    Entity e = this.answerEntities.get(this.activatedAnswer);
    this.posActivatedAnswer = e.getPosition();

    e.setVisible(true);
    e.setZIndex(Integer.MAX_VALUE - 1);

    final double targetScaleX = 0.7 * FXGL.getGameScene().getAppWidth() / 205;
    final double targetScaleY = 0.7 * FXGL.getGameScene().getAppHeight() / 105;
    final double targetX = FXGL.getGameScene().getAppWidth() / 2.0 - (targetScaleX * 205) / 2;
    final double targetY = FXGL.getGameScene().getAppHeight() / 2.0 - (targetScaleY * 105) / 2 - 48;

    FXGL.getGameTimer()
        .runOnceAfter(
            () -> {
              Animation<?> animation =
                  FXGL.animationBuilder()
                      .duration(Duration.seconds(0.5))
                      .scale(e)
                      .from(new Point2D(1, 1))
                      .to(new Point2D(targetScaleX, targetScaleY))
                      .build();

              this.animations.add(animation);
              animation.start();

              animation =
                  FXGL.animationBuilder()
                      .duration(Duration.seconds(0.5))
                      .translate(e)
                      .from(e.getPosition())
                      .to(new Point2D(targetX, targetY))
                      .build();

              this.animations.add(animation);
              animation.start();
            },
            Duration.seconds(0));
  }

  private void animateAnswerCancel() {

    Entity e = this.answerEntities.get(this.activatedAnswer);

    FXGL.getGameTimer()
        .runOnceAfter(
            () -> {
              Animation<?> animation =
                  FXGL.animationBuilder()
                      .duration(Duration.seconds(0.5))
                      .onFinished(this::animateValueFadein)
                      .scale(e)
                      .from(new Point2D(e.getScaleX(), e.getScaleY()))
                      .to(new Point2D(1, 1))
                      .build();

              this.animations.add(animation);
              animation.start();

              animation =
                  FXGL.animationBuilder()
                      .duration(Duration.seconds(0.5))
                      .translate(e)
                      .from(e.getPosition())
                      .to(this.posActivatedAnswer)
                      .build();

              this.animations.add(animation);
              animation.start();
            },
            Duration.seconds(0));
  }

  private void animateAnswerDismiss() {

    Entity e = this.answerEntities.get(this.activatedAnswer);

    FXGL.getGameTimer()
        .runOnceAfter(
            () -> {
              FXGL.play("pickup-03.wav");
              Animation<?> animation =
                  FXGL.animationBuilder()
                      .interpolator(Interpolator.EASE_IN)
                      .duration(Duration.seconds(0.5))
                      .onFinished(this::finalizeAnswer)
                      .translate(e)
                      .to(new Point2D(-FXGL.getGameScene().getAppWidth(), e.getY()))
                      .build();

              this.animations.add(animation);
              animation.start();
            },
            Duration.seconds(0));
  }

  private void finalizeAnswer() {

    Entity e = this.answerEntities.get(this.activatedAnswer);
    e.removeFromWorld();
    FXGL.getGameTimer()
        .runOnceAfter(
            () -> {
              this.animations.clear();
            },
            Duration.seconds(0));

    CategoryControl.INSTANCE.setBusy(false);
  }

  public void showAnswer(int index) {

    if (CategoryControl.getInstance().isBusy()) {
      return;
    }

    CategoryControl.getInstance().setBusy(true);

    this.activatedAnswer = index;
    this.animateValueFadeout();
  }

  public void cancelAnswer() {

    this.animateAnswerCancel();
  }

  public void dismissAnswer() {

    this.animateAnswerDismiss();
  }

  public void update(double tpf) {

    this.animations.forEach(a -> a.onUpdate(tpf));
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<Answer> getAnswers() {
    return answers;
  }

  public void setAnswers(ArrayList<Answer> answers) {
    this.answers = answers;
  }

  @Override
  public String toString() {
    return this.name;
  }

  public Entity getHeaderEntity() {
    return headerEntity;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
