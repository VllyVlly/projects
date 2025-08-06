package org.example.gomoku;

import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

public class GameTimer{

    // Timer for game

    private Timer timer;
    private TimerTask task;
    private int timelimit;
    private int seconds;
    private GameController gameController;

    public GameTimer(int timelimit, GameController gameController) {
        this.timelimit = timelimit;
        this.gameController = gameController;
    }

    public void startTimer(){
        cancelTimer();
        seconds = timelimit;
        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                seconds--;
                int currentTurn = gameController.getCurrentTurn();
                Platform.runLater(() -> gameController.updateTimer(seconds));
                if(seconds == 0){
                    Platform.runLater(() -> {
                        gameController.changeCurrentTurn();
                        if(currentTurn == 1){
                            gameController.reduceMoves1();
                            gameController.updateMoveLabels();
                            gameController.updateCurrentTurnLabels();
                        } else {
                            gameController.reduceMoves2();
                            gameController.updateMoveLabels();
                            gameController.updateCurrentTurnLabels();
                        }
                        cancelTimer();
                        startTimer();
                    });
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void cancelTimer() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

}
