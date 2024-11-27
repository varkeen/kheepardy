package org.khee.kheepardygl.admin;

import com.almasb.fxgl.dsl.FXGL;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BackgroundFill;
import org.controlsfx.control.CheckComboBox;
import org.khee.kheepardygl.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

  @FXML private CheckComboBox<Category> categories;

  @FXML private CategoriesView categoriesView;

  @FXML private List<TextField> playerNames;

  @FXML private Button unbuzzPlayer;

  @FXML private Button controlTitle;
  @FXML private Button controlBuzzers;
  @FXML private Button toggleFullscreen;
  @FXML private Button exitGame;

  public AdminController() {}

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    Kheepardy kheepardy = (Kheepardy) FXGL.getApp();

    for (int i = 0; i < Kheepardy.MAX_PLAYERS; i++) {

      final int index = i;

      this.playerNames
          .get(i)
          .textProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                Player.getPlayer(index).setName(newValue);
              });
    }

    this.exitGame.setStyle("-fx-background-color: #f66; -fx-text-fill: white");

    this.exitGame.setOnMouseClicked((event) -> {
      Platform.exit();
      System.exit(0);
    });

    this.unbuzzPlayer.setOnMouseClicked((event) -> {
      Player.unbuzz();
    });

    this.controlTitle.setOnMouseClicked((event) -> {
      if ( this.controlTitle.getText().equals("Pause Game")) {
        FXGL.set("state", Kheepardy.State.TITLE);
        this.controlTitle.setText("Start Game");
      } else {
        FXGL.set("state", Kheepardy.State.START);
        this.controlTitle.setText("Pause Game");
      }
    });

    this.controlBuzzers.setOnMouseClicked((event) -> {
      if ( this.controlBuzzers.getText().equals("Lock Buzzers")) {
        Player.lockBuzzers();
        this.controlBuzzers.setText("Unlock Buzzers");
      } else {
        Player.unlockBuzzers();
        this.controlBuzzers.setText("Lock Buzzers");
      }
    });

    this.toggleFullscreen.setOnMouseClicked(event -> {
      FXGL.getPrimaryStage().setFullScreen( ! FXGL.getPrimaryStage().isFullScreen() );
    });

    this.categories.getItems().addAll(CategoryControl.getInstance().getCategoriesByName().values());
    this.categories
        .getCheckModel()
        .getCheckedIndices()
        .addListener(
            (ListChangeListener<? super Integer>)
                (c -> {
                  kheepardy.updateCategories(this.categories.getCheckModel().getCheckedItems());
                  this.categoriesView.updateCategories(
                      this.categories.getCheckModel().getCheckedItems());
                }));
  }
}
