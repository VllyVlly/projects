package org.example.gomoku;

import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client implements Runnable {

    private Socket socket;
    private GameSettings settings;
    private Game game;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private GameController gameController;

    private volatile boolean stop = true;

    public Client(int port) {
        try {
            socket = new Socket("localhost", port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            settings = (GameSettings) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException();
        }
    }

    // Receive game info
    @Override
    public void run() {
        while(stop) {
            try{
                GameInfo gameInfo = (GameInfo) in.readObject();
                if (gameInfo.hasColor()) {
                    int row = gameInfo.getRow();
                    int col = gameInfo.getCol();
                    Tile tile = game.getTile(col, row);
                    Platform.runLater(() -> {
                        game.addPiece(row, col, Color.BLACK);
                        gameController.moves1--;
                        gameController.updateLabelsAfterReceiving(1, tile);
                        gameController.listenForDraw();
                    });

                } else if(gameInfo.hasWinner()) {
                    if(gameInfo.getWinner().equals("1")){
                        Platform.runLater(() -> {
                            gameController.resultMenu("Player 1 wins.");
                            gameController.endGameClient();
                        });
                    } else if(gameInfo.getWinner().equals("3")){
                        Platform.runLater(() -> {
                            gameController.resultMenu("Player 2 wins.");
                            gameController.endGameClient();
                        });
                    }
                    return;
                } else if(gameInfo.startTimer()) {
                    gameController.startTimer();
                    gameController.listenForDraw();
                } else if(gameInfo.changeTurn()){
                    Platform.runLater(() -> {
                        gameController.changeCurrentTurn();
                        gameController.reduceMoves1();
                        gameController.updateMoveLabels();
                        gameController.updateCurrentTurnLabels();
                        gameController.listenForDraw();
                    });
                }
            } catch (ClassNotFoundException | IOException e) {
                break;
            }
        }

    }


    public int getStartTurn() {
        return settings.getStartTurn();
    }

    public int getMax(){
        System.out.println(settings.getMax());
        return settings.getMax();
    }

    public int getMoves(){
        return settings.getMoves();
    }

    public int getTimeLimit(){
        return settings.getTimeLimit();
    }

    public int getViewChances(){
        return settings.getViewChances();
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void send(GameInfo piece) {
        try{
            out.writeObject(piece);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        stop = false;
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setController(GameController gameController) {
        this.gameController = gameController;
    }
}
