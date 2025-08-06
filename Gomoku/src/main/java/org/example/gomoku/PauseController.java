package org.example.gomoku;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PauseController {

    // Controller for pause menu

    Stage stage;
    private Parent root;
    private Scene scene;

    private Scene gameScene;
    private GameController gameController;

    public void backmenu(ActionEvent event) throws IOException {
        gameController.endOnExit();
        root = FXMLLoader.load(getClass().getResource("Gomoku.fxml"));
        scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void continuegame(ActionEvent event){
        if (gameScene != null) {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(gameScene);
            stage.show();
        }
    }

    public void setGameScene(Scene scene){
        this.gameScene = scene;
    }

    public void setGameController(GameController gameController){
        this.gameController = gameController;
    }
}
