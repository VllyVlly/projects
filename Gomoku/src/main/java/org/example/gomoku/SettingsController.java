package org.example.gomoku;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {

    @FXML
    private Spinner<Integer> spinner1;

    @FXML
    private Spinner<Integer> spinner2;

    @FXML
    private TextField portnumber;

    @FXML
    private ChoiceBox<String> timeLimit;

    @FXML
    private ChoiceBox<String> invisibleMode;

    @FXML
    private Label note;

    int movements = 20;
    int maxlength = 5;

    private String[] times = {"30 Seconds", "1 minute", "2 minutes", "5 minutes"};
    private String[] chances = {"0","3","6"};
    // 0 means invisible mode is disabled.

    private Scene scene;
    Stage stage;
    private Parent root;

    public void initialize() {
        // Initialize the choice boxes and spinners for settings
        timeLimit.getItems().addAll(times);
        timeLimit.setValue("30 Seconds");

        invisibleMode.getItems().addAll(chances);
        invisibleMode.setValue("0");

        SpinnerValueFactory<Integer> valueFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(movements, 400);
        valueFactory1.setValue(movements);
        spinner1.setValueFactory(valueFactory1);

        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(maxlength, 10);
        valueFactory1.setValue(maxlength);
        spinner2.setValueFactory(valueFactory2);

        spinner1.valueProperty().addListener((observable, oldValue, newValue) -> {
            movements = spinner1.getValue();
        });

        spinner2.valueProperty().addListener((observable, oldValue, newValue) -> {
            maxlength = spinner2.getValue();
        });
    }

    public void play (ActionEvent event) {
        if(portnumber.getText() != null) {
            try{
                int port = Integer.parseInt(portnumber.getText());
                if(port >= 0 && port <= 65535) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("GameRoom.fxml"));
                    root = loader.load();
                    GameController controller = loader.getController();
                    scene = new Scene(root);

                    controller.setGameScene(scene);
                    controller.setMaxLength(maxlength);
                    controller.setMoves(movements);
                    controller.setTimeLimit(timeLimit.getValue());
                    controller.setViewChances(Integer.parseInt(invisibleMode.getValue()));

                    Server server = new Server(port);
                    controller.setServer(server);

                    controller.setPort(port);
                    controller.setPlayernum(1);

                    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    controller.setStage(stage);
                    stage.setScene(scene);
                    stage.show();
                    controller.startGame();
                } else {
                    note.setText("Invalid port number");
                }
            } catch (Exception e) {
                note.setText("Please enter a valid or unused port");
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
