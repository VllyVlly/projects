package org.example.gomoku;

import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Tile extends Region {

    private Group root;
    private Piece piece;
    public static final int TILE_SIZE = 20;
    private double crossThickness = 1.75;
    private double crossPadding = 0.0;

    private int layoutX, layoutY;
    private int row;
    private int col;
    private int x;
    private int y;

    private RadioButton radioButton;
    private Rectangle tile;
    private Circle center;
    private Circle ghostpiece;
    private GameController controller;
    private Line verticalLine;
    private Line horizontalLine;

    private Timeline fadeTimeline;

    public void highlight() {
        tile.setFill(Color.YELLOW);
    }

    public void unhighlight() {
        tile.setFill(Color.BURLYWOOD);
    }

    public void attachPiece(Piece piece){
        piece.getRoot().setScaleX(0.5);
        piece.getRoot().setScaleY(0.5);
        root.getChildren().add(piece.getRoot());

        ScaleTransition grow = new ScaleTransition(Duration.millis(200), piece.getRoot());
        grow.setFromX(0.5);
        grow.setFromY(0.5);
        grow.setToX(1);
        grow.setToY(1);
        grow.setInterpolator(Interpolator.EASE_OUT);
        grow.play();
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void setListener(){
        radioButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                controller.inform.setText(" ");
                highlight();
                controller.addPieceOnClick();
            } else {
                unhighlight();
            }
        });

        radioButton.setOnMouseClicked(event -> radioButton.setSelected(true));

        tile.setOnMouseEntered(event -> {
            highlight();
            if(controller.playernum == 1){
                ghostpiece.setFill(Color.BLACK);
                fadeTimeline.play();
            } else if (controller.playernum == 2){
                ghostpiece.setFill(Color.WHITE);
                fadeTimeline.play();
            }
        });

        tile.setOnMouseExited(event -> {
            unhighlight();
            fadeTimeline.stop();
            ghostpiece.setOpacity(0.0);
        });

        radioButton.setOnMouseEntered(event -> {
            highlight();
            if(controller.playernum == 1){
                ghostpiece.setFill(Color.BLACK);
                fadeTimeline.play();
            } else if (controller.playernum == 2){
                ghostpiece.setFill(Color.WHITE);
                fadeTimeline.play();
            }
        });

        radioButton.setOnMouseExited(event -> {
            unhighlight();
            fadeTimeline.stop();
            ghostpiece.setOpacity(0.0);
        });

        verticalLine.setOnMouseEntered(event -> {
            highlight();
            if(controller.playernum == 1){
                ghostpiece.setFill(Color.BLACK);
                fadeTimeline.play();
            } else if (controller.playernum == 2){
                ghostpiece.setFill(Color.WHITE);
                fadeTimeline.play();
            }
        });

        verticalLine.setOnMouseExited(event -> {
            unhighlight();
            fadeTimeline.stop();
            ghostpiece.setOpacity(0.0);
        });

        horizontalLine.setOnMouseEntered(event -> {
            highlight();
            if(controller.playernum == 1){
                ghostpiece.setFill(Color.BLACK);
                fadeTimeline.play();
            } else if (controller.playernum == 2){
                ghostpiece.setFill(Color.WHITE);
                fadeTimeline.play();
            }
        });

        horizontalLine.setOnMouseExited(event -> {
            unhighlight();
            fadeTimeline.stop();
            ghostpiece.setOpacity(0.0);
        });

        radioButton.toFront();
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public Group getRoot() {
        return root;
    }

    public int getX() {
        return layoutX;
    }

    public int getY() {
        return layoutY;
    }

    public RadioButton getRadio() {
        return radioButton;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    // Generate functions for each case of tile
    public void generateGeneralCross(){
        verticalLine = new Line();
        verticalLine.setStartX(x + (double) TILE_SIZE /2);
        verticalLine.setStartY(y + crossPadding);
        verticalLine.setEndX(x + (double) TILE_SIZE /2);
        verticalLine.setEndY(y + TILE_SIZE - crossPadding);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(crossThickness);

        horizontalLine = new Line();
        horizontalLine.setStartX(x + crossPadding);
        horizontalLine.setStartY(y + (double) TILE_SIZE /2);
        horizontalLine.setEndX(x + TILE_SIZE - crossPadding);
        horizontalLine.setEndY(y + (double) TILE_SIZE /2);
        horizontalLine.setStroke(Color.BLACK);
        horizontalLine.setStrokeWidth(crossThickness);

        root.getChildren().addAll(verticalLine, horizontalLine);
    }

    public void generateBottomRightCross() {
        verticalLine = new Line();
        verticalLine.setStartX(x + TILE_SIZE/2);
        verticalLine.setStartY(y + TILE_SIZE/2);
        verticalLine.setEndX(x + TILE_SIZE/2);
        verticalLine.setEndY(y + TILE_SIZE - crossPadding);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(crossThickness);

        horizontalLine = new Line();
        horizontalLine.setStartX(x + TILE_SIZE/2);
        horizontalLine.setStartY(y + TILE_SIZE/2);
        horizontalLine.setEndX(x + TILE_SIZE - crossPadding);
        horizontalLine.setEndY(y + TILE_SIZE/2);
        horizontalLine.setStroke(Color.BLACK);
        horizontalLine.setStrokeWidth(crossThickness);

        root.getChildren().addAll(verticalLine, horizontalLine);
    }

    public void generateTopRightCross() {
        verticalLine = new Line();
        verticalLine.setStartX(x + TILE_SIZE/2);
        verticalLine.setStartY(y + crossPadding);
        verticalLine.setEndX(x + TILE_SIZE/2);
        verticalLine.setEndY(y + TILE_SIZE/2);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(crossThickness);

        horizontalLine = new Line();
        horizontalLine.setStartX(x + TILE_SIZE/2);
        horizontalLine.setStartY(y + TILE_SIZE/2);
        horizontalLine.setEndX(x + TILE_SIZE - crossPadding);
        horizontalLine.setEndY(y + TILE_SIZE/2);
        horizontalLine.setStroke(Color.BLACK);
        horizontalLine.setStrokeWidth(crossThickness);

        root.getChildren().addAll(verticalLine, horizontalLine);
    }

    public void generateBottomLeftCross() {
        verticalLine = new Line();
        verticalLine.setStartX(x + TILE_SIZE/2);
        verticalLine.setStartY(y + TILE_SIZE/2);
        verticalLine.setEndX(x + TILE_SIZE/2);
        verticalLine.setEndY(y + TILE_SIZE - crossPadding);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(crossThickness);

        horizontalLine = new Line();
        horizontalLine.setStartX(x + crossPadding);
        horizontalLine.setStartY(y + TILE_SIZE/2);
        horizontalLine.setEndX(x + TILE_SIZE/2);
        horizontalLine.setEndY(y + TILE_SIZE/2);
        horizontalLine.setStroke(Color.BLACK);
        horizontalLine.setStrokeWidth(crossThickness);

        root.getChildren().addAll(verticalLine, horizontalLine);
    }

    public void generateTopLeftCross() {
        verticalLine = new Line();
        verticalLine.setStartX(x + TILE_SIZE/2);
        verticalLine.setStartY(y + crossPadding);
        verticalLine.setEndX(x + TILE_SIZE/2);
        verticalLine.setEndY(y + TILE_SIZE/2);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(crossThickness);

        horizontalLine = new Line();
        horizontalLine.setStartX(x + crossPadding);
        horizontalLine.setStartY(y + TILE_SIZE/2);
        horizontalLine.setEndX(x + TILE_SIZE/2);
        horizontalLine.setEndY(y + TILE_SIZE/2);
        horizontalLine.setStroke(Color.BLACK);
        horizontalLine.setStrokeWidth(crossThickness);

        root.getChildren().addAll(verticalLine, horizontalLine);
    }

    public void generateTopHorizontal() {
        horizontalLine = new Line();
        horizontalLine.setStartX(x + crossPadding);
        horizontalLine.setStartY(y + TILE_SIZE/2);
        horizontalLine.setEndX(x + TILE_SIZE - crossPadding);
        horizontalLine.setEndY(y + TILE_SIZE/2);
        horizontalLine.setStroke(Color.BLACK);
        horizontalLine.setStrokeWidth(crossThickness);

        verticalLine = new Line();
        verticalLine.setStartX(x + TILE_SIZE/2);
        verticalLine.setStartY(y + TILE_SIZE/2);
        verticalLine.setEndX(x + TILE_SIZE/2);
        verticalLine.setEndY(y + TILE_SIZE - crossPadding);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(crossThickness);

        root.getChildren().addAll(horizontalLine,verticalLine);
    }

    public void generateBottomHorizontal() {
        horizontalLine = new Line();
        horizontalLine.setStartX(x + crossPadding);
        horizontalLine.setStartY(y + TILE_SIZE/2);
        horizontalLine.setEndX(x + TILE_SIZE - crossPadding);
        horizontalLine.setEndY(y + TILE_SIZE/2);
        horizontalLine.setStroke(Color.BLACK);
        horizontalLine.setStrokeWidth(crossThickness);

        verticalLine = new Line();
        verticalLine.setStartX(x + TILE_SIZE/2);
        verticalLine.setStartY(y + crossPadding);
        verticalLine.setEndX(x + TILE_SIZE/2);
        verticalLine.setEndY(y + TILE_SIZE/2);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(crossThickness);

        root.getChildren().addAll(horizontalLine, verticalLine);
    }

    public void generateLeftVertical() {
        verticalLine = new Line();
        verticalLine.setStartX(x + TILE_SIZE/2);
        verticalLine.setStartY(y + crossPadding);
        verticalLine.setEndX(x + TILE_SIZE/2);
        verticalLine.setEndY(y + TILE_SIZE - crossPadding);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(crossThickness);

        horizontalLine = new Line();
        horizontalLine.setStartX(x + TILE_SIZE/2);
        horizontalLine.setStartY(y + TILE_SIZE/2);
        horizontalLine.setEndX(x + TILE_SIZE - crossPadding);
        horizontalLine.setEndY(y + TILE_SIZE/2);
        horizontalLine.setStroke(Color.BLACK);
        horizontalLine.setStrokeWidth(crossThickness);

        root.getChildren().addAll(verticalLine, horizontalLine);
    }

    public void generateRightVertical(){
        verticalLine = new Line();
        verticalLine.setStartX(x + TILE_SIZE/2);
        verticalLine.setStartY(y + crossPadding);
        verticalLine.setEndX(x + TILE_SIZE/2);
        verticalLine.setEndY(y + TILE_SIZE - crossPadding);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(crossThickness);

        horizontalLine = new Line();
        horizontalLine.setStartX(x + crossPadding);
        horizontalLine.setStartY(y + TILE_SIZE/2);
        horizontalLine.setEndX(x + TILE_SIZE/2);
        horizontalLine.setEndY(y + TILE_SIZE/2);
        horizontalLine.setStroke(Color.BLACK);
        horizontalLine.setStrokeWidth(crossThickness);

        root.getChildren().addAll(verticalLine, horizontalLine);
    }

    public Tile (int x, int y, int row, int col, GameController controller, Game game) {
        this.controller = controller;
        this.x = x;
        this.y = y;
        root = new Group();
        tile = new Rectangle(TILE_SIZE, TILE_SIZE);
        tile.setWidth(TILE_SIZE);
        tile.setHeight(TILE_SIZE);
        tile.setX(x);
        tile.setY(y);
        tile.setFill(Color.BURLYWOOD);
        tile.setStroke(Color.BURLYWOOD);

        ghostpiece = new Circle(Piece.PIECE_SIZE);
        ghostpiece.setCenterX(x+10.0);
        ghostpiece.setCenterY(y+10.0);
        ghostpiece.setStroke(Color.BLACK);
        ghostpiece.setOpacity(0.0);

        center = new Circle(1.0);
        center.setCenterX(x+10.0);
        center.setCenterY(y+10.0);
        center.setFill(Color.BLACK);
        center.setStroke(Color.BLACK);

        layoutX = x+10;
        layoutY = y+10;

        root.getChildren().addAll(tile, center, ghostpiece);

        this.row = row;
        this.col = col;

        radioButton = new RadioButton();
        radioButton.setOpacity(0.0);
        radioButton.setLayoutX(x+2.0);
        radioButton.setLayoutY(y+2.0);

        fadeTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(ghostpiece.opacityProperty(), 0.8)),
                new KeyFrame(Duration.seconds(1), new KeyValue(ghostpiece.opacityProperty(), 0.3))
        );
        fadeTimeline.setAutoReverse(true);
        fadeTimeline.setCycleCount(Timeline.INDEFINITE);

        root.getChildren().add(radioButton);
    }


}
