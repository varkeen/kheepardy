package org.khee.kheepardygl;

import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Cell extends Region {

  public Cell(String content, Color gradientFrom, Color gradientTo, Integer textSize, String font ) {

    this.setMaxSize(200, 100);
    this.setWidth(200);
    this.setHeight(100);

    Label text = new Label(content);
    text.setTextAlignment(TextAlignment.CENTER);
    text.setPrefWidth(180);
    text.setPrefHeight(100);
    text.setLayoutX(10);
    text.setLayoutY(0);
    text.setAlignment(Pos.CENTER);
    text.setWrapText(true);

    if (textSize != null) {
      text.setStyle("-fx-font-family: " + font + " ; -fx-font-size: " + textSize + "; -fx-text-fill: white");
      text.applyCss();
    } else {
      long fontSize = 40;
      Text measureText = new Text(content);
      do {
        new Scene(new Group(measureText));
        measureText.setStyle("-fx-font-family: " + font + " ; -fx-font-size: " + fontSize + "; -fx-fill: white");
        measureText.setWrappingWidth(180);
        measureText.applyCss();
        fontSize--;
      } while (measureText.getLayoutBounds().getHeight() > 60);
      text.setStyle("-fx-font-family: " + font + " ; -fx-font-size: " + fontSize + "; -fx-text-fill: white");
    }

    Rectangle rect = new Rectangle();
    rect.setX(0);
    rect.setY(0);
    rect.setWidth(200);
    rect.setHeight(100);
    rect.setArcHeight(10);
    rect.setArcWidth(10);
    rect.setStroke(Color.WHITE);
    rect.setStrokeWidth(2.0);

    Stop[] stops = new Stop[] {new Stop(0, gradientFrom), new Stop(1, gradientTo)};
    LinearGradient linearGradient =
        new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

    rect.setFill(linearGradient);

    this.getChildren().add(rect);
    this.getChildren().add(text);
  }

  public Cell(Image image, Color gradientFrom, Color gradientTo) {


    this.setMaxSize(200, 100);
    this.setWidth(200);
    this.setHeight(100);

    Rectangle rect = new Rectangle();
    rect.setX(0);
    rect.setY(0);
    rect.setWidth(200);
    rect.setHeight(100);
    rect.setArcHeight(10);
    rect.setArcWidth(10);
    rect.setStroke(Color.WHITE);
    rect.setStrokeWidth(2.0);

    Stop[] stops = new Stop[] {new Stop(0, gradientFrom), new Stop(1, gradientTo)};
    LinearGradient linearGradient =
            new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

    rect.setFill(linearGradient);

    BorderPane borderPane = new BorderPane();
    borderPane.setPrefWidth(200);
    borderPane.setPrefHeight(100);

    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(180);
    imageView.setFitHeight(80);
    imageView.setPreserveRatio(true);

    borderPane.setCenter(imageView);

    this.getChildren().add(rect);
    this.getChildren().add(borderPane);
  }

}
