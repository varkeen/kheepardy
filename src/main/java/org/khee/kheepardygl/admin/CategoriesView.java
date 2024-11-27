package org.khee.kheepardygl.admin;

import com.almasb.fxgl.dsl.FXGL;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.khee.kheepardygl.Category;
import org.khee.kheepardygl.CategoryControl;
import org.khee.kheepardygl.Kheepardy;
import org.khee.kheepardygl.Player;
import org.kordamp.ikonli.javafx.FontIcon;

public class CategoriesView extends StackPane {

  private final HBox categoriesPane = new HBox();
  private final VBox answerPane = new VBox();

  private final Label labelAnswer = new Label();
  private final Label correctAnswer = new Label();
  private final Button buttonStartTimer = new Button("Start Timer");
  private final Button buttonCancel = new Button("Cancel");
  private final Button buttonCorrect = new Button("Correct");
  private final Button buttonWrong = new Button("Wrong");
  private final Button buttonDismiss = new Button("Dismiss");
  private final HBox buttons = new HBox();

  private Button activeButton = null;

  public CategoriesView() {

    this.categoriesPane.setPrefWidth(Integer.MAX_VALUE);
    this.categoriesPane.setPrefHeight(Integer.MAX_VALUE);
    this.categoriesPane.setAlignment(Pos.CENTER);
    this.answerPane.setPrefWidth(Integer.MAX_VALUE);
    this.answerPane.setPrefHeight(Integer.MAX_VALUE);

    this.getChildren().add(this.categoriesPane);
    this.getChildren().add(this.answerPane);

    this.answerPane.setVisible(false);

    this.initAnswerPane();
    this.initializeStates();
  }

  private void initializeStates() {

    System.out.println("initStates (categroyView)");

    FXGL.getWorldProperties()
        .addListener(
            "state",
            (prev, now) -> {
              this.buttons.getChildren().clear();

              if (now == Kheepardy.State.ANSWER_CHOOSING) {
                Player.lockBuzzers();
                this.activateCategories();
              }
              if (now == Kheepardy.State.ANSWER_SELECTED) {
                this.activateAnswer();
                Category category = FXGL.getWorldProperties().getObject("activeCategory");
                int index = FXGL.getWorldProperties().getInt("activeCategoryIndex");
                this.labelAnswer.setText(category.getAnswers().get(index).getAnswer());
                this.correctAnswer.setText(category.getAnswers().get(index).getQuestion());
                this.buttons.getChildren().add(buttonStartTimer);
                this.buttons.getChildren().add(buttonCancel);
              }
              if (now == Kheepardy.State.PLAYER_BUZZERED) {
                this.buttons.getChildren().add(buttonCorrect);
                this.buttons.getChildren().add(buttonWrong);
              }
              if (now == Kheepardy.State.CLOCK_OUT_OF_TIME_ANSWER) {
                Player.lockBuzzers();
                this.buttons.getChildren().add(this.buttonDismiss);
              }
            });
  }

  private void initAnswerPane() {

    this.labelAnswer.setMinHeight(150);
    this.labelAnswer.setStyle("-fx-font-size: 32px ; -fx-text-fill: lightblue");
    this.labelAnswer.setPrefWidth(Integer.MAX_VALUE);
    this.labelAnswer.setWrapText(true);
    this.labelAnswer.setAlignment(Pos.CENTER);
    this.labelAnswer.setTextAlignment(TextAlignment.CENTER);
    this.correctAnswer.setMinHeight(150);
    this.correctAnswer.setStyle("-fx-font-size: 32px ; -fx-text-fill: white");
    this.correctAnswer.setPrefWidth(Integer.MAX_VALUE);
    this.correctAnswer.setWrapText(true);
    this.correctAnswer.setAlignment(Pos.CENTER);
    this.correctAnswer.setTextAlignment(TextAlignment.CENTER);

    this.answerPane.getChildren().add(this.labelAnswer);
    this.answerPane.getChildren().add(this.correctAnswer);

    this.buttons.setAlignment(Pos.CENTER);
    this.buttons.setSpacing(10);

    this.buttonStartTimer.setPrefWidth(128);
    this.buttonStartTimer.setPrefHeight(64);
    this.buttonStartTimer.setGraphic(new FontIcon("far-clock"));
    this.buttonStartTimer.setOnMouseClicked(
        event -> {
          FXGL.getWorldProperties().setValue("state", Kheepardy.State.CLOCK_TICKING_ANSWER);
          Player.unlockBuzzers();
        });

    this.buttonCancel.setPrefWidth(128);
    this.buttonCancel.setPrefHeight(64);
    this.buttonCancel.setGraphic(new FontIcon("far-times-circle"));
    this.buttonCancel.setStyle("-fx-background-color: linear-gradient(#ff5400, #be1d00);");
    this.buttonCancel.setOnMouseClicked(
        event -> {
          if (CategoryControl.getInstance().isBusy()) {
            FXGL.getWorldProperties().setValue("state", Kheepardy.State.ANSWER_CHOOSING);
            CategoryControl.getInstance().getActiveCategory().cancelAnswer();
          }
          System.out.println("Active Button = " + this.activeButton.getText());
          this.activeButton.setDisable(false);
          this.activateCategories();
        });

    this.buttonCorrect.setPrefWidth(128);
    this.buttonCorrect.setPrefHeight(64);
    this.buttonCorrect.setGraphic(new FontIcon("far-check-circle"));
    this.buttonCorrect.setStyle("-fx-background-color: linear-gradient(#7bff00, #30be00);");
    this.buttonCorrect.setOnMouseClicked(
        event -> {
          FXGL.inc("answersLeft", -1);
          System.out.println("Answers left: " + FXGL.geti("answersLeft"));
          FXGL.play("pickup-02.wav");
          Player.getActive().incScore((FXGL.geti("activeCategoryIndex") + 1) * 100);
          Player.unbuzz();
          Player.unlockBuzzers();
          CategoryControl.getInstance().getActiveCategory().dismissAnswer();
          FXGL.set("state", Kheepardy.State.ANSWER_CHOOSING);
        });

    this.buttonWrong.setPrefWidth(128);
    this.buttonWrong.setPrefHeight(64);
    this.buttonWrong.setGraphic(new FontIcon("far-times-circle"));
    this.buttonWrong.setStyle("-fx-background-color: linear-gradient(#ff5400, #be1d00);");
    this.buttonWrong.setOnMouseClicked(
        event -> {
          Player.getActive().incScore((FXGL.geti("activeCategoryIndex") + 1) * -100);
          Player.unbuzz();
          FXGL.set("state", Kheepardy.State.CLOCK_TICKING_ANSWER);
        });

    this.buttonDismiss.setPrefWidth(128);
    this.buttonDismiss.setPrefHeight(64);
    this.buttonDismiss.setGraphic(new FontIcon("fas-hand-point-right"));
    this.buttonDismiss.setOnMouseClicked(
        event -> {
          FXGL.inc("answersLeft", -1);
          Player.unlockBuzzers();
          CategoryControl.getInstance().getActiveCategory().dismissAnswer();
          FXGL.getWorldProperties().setValue("state", Kheepardy.State.ANSWER_CHOOSING);
        });

    this.answerPane.getChildren().add(buttons);
  }

  public void activateCategories() {
    this.answerPane.setVisible(false);
    this.categoriesPane.setVisible(true);
  }

  public void activateAnswer() {
    this.categoriesPane.setVisible(false);
    this.answerPane.setVisible(true);
  }

  public void updateCategories(ObservableList<Category> categories) {

    this.categoriesPane.getChildren().clear();
    this.categoriesPane.setVisible(true);
    this.answerPane.setVisible(false);

    for (Category category : categories) {

      VBox vbox = new VBox();

      Button button = new Button(category.getName());
      button.setPrefSize(100, 50);
      vbox.getChildren().add(button);

      for (int value = 1; value <= 5; value++) {

        Button buttonValue = new Button(Integer.toString(value * 100));
        buttonValue.setPrefSize(100, 50);

        vbox.getChildren().add(buttonValue);

        final int index = value - 1;
        buttonValue.setOnMouseClicked(
            event -> {
              if (CategoryControl.getInstance().isBusy()) {
                return;
              }
              CategoryControl.getInstance().showAnswer(category, index);
              System.out.println(FXGL.getWorldProperties().getObject("state").toString());
              FXGL.getWorldProperties().setValue("activeCategory", category);
              FXGL.getWorldProperties().setValue("activeCategoryIndex", index);
              FXGL.getWorldProperties().setValue("state", Kheepardy.State.ANSWER_SELECTED);
              buttonValue.setDisable(true);
              this.activeButton = buttonValue;
            });
      }
      this.categoriesPane.getChildren().add(vbox);
    }
  }
}
