package org.example.gomoku;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class ResultController {

    @FXML
    private Label resultLabel;

    private Scene scene;
    Stage stage;
    private Parent root;

    public void setResult(String result) {
        resultLabel.setText(result);
    }

    public void backmenu(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Gomoku.fxml"));
        scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
