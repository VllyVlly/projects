package org.example.gomoku;


import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.Serializable;


public class Piece implements Serializable {

    public static final int PIECE_SIZE = 8;
    private Color color;
    private Group root;
    private int row;
    private int col;
    private FillTransition flashAnimation;
    private Circle c;

    private GameController gameController;

    public Piece (Color color, int x, int y) {
        root = new Group();
        c = new Circle();
        c.setRadius(PIECE_SIZE);
        c.setCenterX(x);
        c.setCenterY(y);
        this.color = color;
        if(color == Color.BLACK) {
            c.setFill(Color.BLACK);
        }
        if(color == Color.WHITE) {
            c.setFill(Color.WHITE);
        }
        c.setStroke(Color.BLACK);
        root.getChildren().add(c);

        makeFlashAnimation();

        c.setOnMouseClicked(event -> {
            flash();
            if(gameController.isInvisibleModeOn() && gameController.getCurrentTurn() == gameController.getPlayerNum()){
                gameController.increaseStrikes();
                gameController.inform.setText("A piece has already been placed in that tile.");
            }
        });
    }

    public void flash(){
        flashAnimation.stop();
        c.setFill(Color.RED);
        flashAnimation.playFromStart();
    }

    private void makeFlashAnimation() {
        flashAnimation = new FillTransition(Duration.millis(750), c);
        flashAnimation.setFromValue(Color.RED);
        flashAnimation.setToValue(color);
        flashAnimation.setCycleCount(2);
        flashAnimation.setAutoReverse(false);
    }

    public void placementAnimation() {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), c);
        scaleUp.setFromX(1);
        scaleUp.setFromY(1);
        scaleUp.setToX(1.3);
        scaleUp.setToY(1.3);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), c);
        scaleDown.setFromX(1.3);
        scaleDown.setFromY(1.3);
        scaleDown.setToX(1);
        scaleDown.setToY(1);
        scaleDown.setInterpolator(Interpolator.EASE_OUT);

        SequentialTransition pulse = new SequentialTransition(scaleUp, scaleDown);
        pulse.play();
    }

    public void setCoor(int x, int y) {
        this.col = x;
        this.row = y;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public Color getColor() {
        return color;
    }

    public Group getRoot() {
        return root;
    }
}
