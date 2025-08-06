package org.example.gomoku;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientController {

    @FXML
    private TextField portnumber;

    @FXML
    private Label note;

    private Scene scene;
    Stage stage;
    private Parent root;


    public void join(ActionEvent event) throws IOException {
        if(portnumber.getText() != null) {
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GameRoom.fxml"));
                root = fxmlLoader.load();
                GameController controller = fxmlLoader.getController();
                scene = new Scene(root);

                controller.setGameScene(scene);
                controller.setPlayernum(2);
                int port = Integer.parseInt(portnumber.getText().trim());
                Client client = new Client(port);
                controller.setClient(client);

                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                controller.setStage(stage);
                stage.setScene(scene);
                stage.show();
                controller.startGame();
            } catch (Exception e) {
                note.setText("Please enter a valid port.");
            }
        } else {
            note.setText("Please enter a port");
        }

    }

    public void backmenu(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Gomoku.fxml"));
        scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
