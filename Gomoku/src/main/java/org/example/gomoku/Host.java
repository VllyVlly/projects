package org.example.gomoku;

import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Host implements Runnable {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private volatile boolean stop = true;
    private Game game;

    private GameController gameController;

    public Host(int port, Game game) {
        this.game = game;
        try{
            socket = new Socket("localhost",port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Receive game info
    @Override
    public void run() {
        try{
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            GameSettings settings = new GameSettings(gameController.getMoves(), gameController.getMaxlength(), gameController.getStartTurn(), gameController.getTimeLimit(), gameController.getViewChances());
            send(settings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while(stop) {
            try{
                GameInfo gameInfo = (GameInfo) in.readObject();
                if (gameInfo.hasColor()) {
                    int row = gameInfo.getRow();
                    int col = gameInfo.getCol();
                    Tile tile = game.getTile(col, row);
                    Platform.runLater(() -> {
                        game.addPiece(row, col, Color.WHITE);
                        gameController.moves2--;
                        gameController.updateLabelsAfterReceiving(2, tile);
                        gameController.listenForDraw();
                    });
                } else if(gameInfo.hasWinner()) {
                    if(gameInfo.getWinner().equals("2")){
                        Platform.runLater(() -> {
                            gameController.resultMenu("Player 2 wins.");
                            gameController.endGameHost();
                        });
                    } else if(gameInfo.getWinner().equals("4")){
                        Platform.runLater(() -> {
                            gameController.resultMenu("Player 1 wins.");
                            gameController.endGameHost();
                        });
                    }
                    return;
                } else if(gameInfo.startTimer()) {
                    gameController.startTimer();
                    gameController.listenForDraw();
                } else if(gameInfo.changeTurn()){
                    Platform.runLater(() -> {
                        gameController.changeCurrentTurn();
                        gameController.reduceMoves2();
                        gameController.updateMoveLabels();
                        gameController.updateCurrentTurnLabels();
                        gameController.listenForDraw();
                    });
                }
            } catch (IOException | ClassNotFoundException e) {
                break;
            }
        }
    }


    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public void send(GameInfo piece) {
        try{
            out.writeObject(piece);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(GameSettings settings) {
        try{
            out.writeObject(settings);
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

}
