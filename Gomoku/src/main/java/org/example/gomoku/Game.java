package org.example.gomoku;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class Game {

    private static GameController controller;

    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    private ToggleGroup togglegroup;

    public ArrayList<Tile> tiles = new ArrayList<>();
    public Tile[][] board = new Tile[WIDTH][HEIGHT];

    private int startturn = 0;
    private String winner;
    public int length = 5;

    public Parent boardParent = null;

    public Game (GameController controller) {
        this.controller = controller;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void createBoard() {
        Pane root = new Pane();
        root.setPrefSize(Tile.TILE_SIZE * WIDTH, Tile.TILE_SIZE * HEIGHT);
        root.getChildren().addAll(tileGroup, pieceGroup);

        togglegroup = new ToggleGroup();

        for(int y = 0; y < HEIGHT; y++) {
            for(int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile(x*Tile.TILE_SIZE, y*Tile.TILE_SIZE, y, x, controller, this);
                tiles.add(tile);
                board[y][x] = tile;

                // Determine edge or corner case
                if (isCorner(x, y, WIDTH, HEIGHT)) {
                    addCornerCross(tile, x, y, WIDTH, HEIGHT);
                }
                else if (isEdge(x, y, WIDTH, HEIGHT)) {
                    addEdgeCross(tile, x, y, WIDTH, HEIGHT);
                }
                else {
                    tile.generateGeneralCross();
                }

                tile.setListener();
                tileGroup.getChildren().add(tile.getRoot());
            }
        }

        for (Tile tile : tiles) {
            tile.getRadio().setToggleGroup(togglegroup);
        }
        boardParent = root;
    }

    // Checks for generating board
    private boolean isCorner(int x, int y, int width, int height) {
        return (x == 0 && y == 0) ||                   // Top-left
                (x == width-1 && y == 0) ||            // Top-right
                (x == 0 && y == height-1) ||           // Bottom-left
                (x == width-1 && y == height-1);       // Bottom-right
    }

    private boolean isEdge(int x, int y, int width, int height) {
        return x == 0 || x == width-1 || y == 0 || y == height-1;
    }

    // Generate board functions
    private void addCornerCross(Tile tile, int x, int y, int width, int height) {
        if (x == 0 && y == 0) {
            tile.generateBottomRightCross();       // Top-left corner
        }
        else if (x == width-1 && y == 0) {
            tile.generateBottomLeftCross();      // Top-right corner
        }
        else if (x == 0 && y == height-1) {
            tile.generateTopRightCross();    // Bottom-left corner
        }
        else {
            tile.generateTopLeftCross();    // Bottom-right corner
        }
    }

    private void addEdgeCross(Tile tile, int x, int y, int width, int height) {
        if (x == 0) {
            tile.generateLeftVertical();      // Left edge
        }
        else if (x == width-1) {
            tile.generateRightVertical();      // Right edge
        }
        else if (y == 0) {
            tile.generateTopHorizontal();        // Top edge
        }
        else {
            tile.generateBottomHorizontal();        // Bottom edge
        }
    }

    public boolean checkSelected() {
        boolean anyselected = false;
        for(Tile tile : tiles) {
            if(tile.getRadio().isSelected()){
                anyselected = true;
            }
        }
        return anyselected;
    }

    // Calls on click
    public Piece addPiece(Color color) {
        for(Tile tile : tiles) {
            if(tile.getRadio().isSelected()){
                if(tile.hasPiece()){
                    controller.inform.setText("A Piece has already been placed in the selected tile");
                    return null;
                } else {
                    Piece piece = new Piece(color, tile.getX(), tile.getY());
                    piece.setGameController(controller);
                    tile.setPiece(piece);
                    if(controller.isBoardInvis() && controller.isInvisibleModeOn()){
                        piece.getRoot().setOpacity(0.0);
                    }
                    tile.attachPiece(piece);
                    piece.setCoor(tile.getRow(), tile.getCol());

                    piece.placementAnimation();
                    return piece;
                }
            }
        }
        return null;
    }

    // Calls on receival
    public void addPiece(int row, int col, Color color) {
        Tile tile = board[col][row];
        Piece piece = new Piece(color, tile.getX(), tile.getY());
        piece.setGameController(controller);
        tile.setPiece(piece);
        if(controller.isBoardInvis() && controller.isInvisibleModeOn()){
            piece.getRoot().setOpacity(0.0);
        }
        tile.attachPiece(piece);
    }

    // Invisible mode exclusive functions
    public void revealPieces(){
        for(Tile tile : tiles) {
            if(tile.getPiece() != null){
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(tile.getPiece().getRoot().opacityProperty(), 0.0)),
                        new KeyFrame(Duration.seconds(1.5), new KeyValue(tile.getPiece().getRoot().opacityProperty(), 1.0))
                );
                timeline.play();
            }
        }
    }

    public void makePiecesDisappear(){
        for(Tile tile : tiles) {
            if(tile.getPiece() != null){
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(tile.getPiece().getRoot().opacityProperty(), 1.0)),
                        new KeyFrame(Duration.seconds(1.5), new KeyValue(tile.getPiece().getRoot().opacityProperty(), 0.0))
                );
                timeline.play();
            }
        }
    }

    // Setter and getter methods
    public void setStartTurn() {
        Random random = new Random();
        startturn = random.nextInt(2) + 1;
    }

    public int getStartTurn() {
        return startturn;
    }

    public Tile[][] getBoard() {
        return board;
    }

    public Tile getTile(int row, int col) {
        return board[row][col];
    }

    public Tile getSelectedTile() {
        for (Tile tile : tiles) {
            if (tile.getRadio().isSelected()) {
                return tile;
            }
        }
        return null;
    }

    // Check functions for max length of unbroken rows
    public int checkHorizontal(Tile tile, Color color) {
        int startrow = tile.getRow();
        int startcol = tile.getCol();
        int max = 1;
        int right = startcol + 1;
        int left = startcol - 1;
        boolean stopright = true;
        boolean stopleft = true;
        for(int i = 0; i < 20; i++) {
            if (right < 20) {
                if(board[startrow][right].getPiece() != null){
                    if(stopright && board[startrow][right].getPiece().getColor() == color){
                        max++;
                        right++;
                    } else {
                        stopright = false;
                    }
                }
            } else {
                stopright = false;
            }

            if (left >= 0) {
                if(board[startrow][left].getPiece() != null){
                    if(stopleft && board[startrow][left].getPiece().getColor() == color){
                        max++;
                        left--;
                    } else {
                        stopleft = false;
                    }
                }
            } else {
                stopleft = false;
            }
        }
        return max;
    }

    public int checkVertical(Tile tile, Color color) {
        int startrow = tile.getRow();
        int startcol = tile.getCol();
        int max = 1;
        int up = startrow - 1;
        int down = startrow + 1;
        boolean stopup = true;
        boolean stopdown = true;
        for(int i = 0; i < 20; i++) {
            if(up >=0) {
                if(board[up][startcol].getPiece() != null){
                    if(stopup && board[up][startcol].getPiece().getColor() == color){
                        max++;
                        up--;
                    } else {
                        stopup = false;
                    }
                }
            } else {
                stopup = false;
            }

            if(down <20) {
                if(board[down][startcol].getPiece() != null){
                    if(stopdown && board[down][startcol].getPiece().getColor() == color){
                        max++;
                        down++;
                    } else {
                        stopdown = false;
                    }
                }
            } else {
                stopdown = false;
            }
        }
        return max;
    }

    public int checkDiagonalRight(Tile tile, Color color) {
        int startrow = tile.getRow();
        int startcol = tile.getCol();
        int max = 1;
        int up = startrow - 1;
        int down = startrow + 1;
        int right = startcol + 1;
        int left = startcol - 1;
        boolean stopup = true;
        boolean stopright = true;
        boolean stopdown = true;
        boolean stopleft = true;
        for(int i = 0; i < 20; i++) {
            if(right < 20 && up >=0) {
                if(board[up][right].getPiece() != null){
                    if(stopright && stopup && board[up][right].getPiece().getColor() == color){
                        max++;
                        up--;
                        right++;
                    } else {
                        stopup = false;
                        stopright = false;
                    }
                }
            } else {
                stopup = false;
                stopright = false;
            }

            if(left >= 0 && down <20) {
                if(board[down][left].getPiece() != null){
                    if(stopleft  && stopdown  && board[down][left].getPiece().getColor() == color){
                        max++;
                        down++;
                        left--;
                    } else {
                        stopdown = false;
                        stopleft = false;
                    }
                }
            } else {
                stopdown = false;
                stopleft = false;
            }

        }
        return max;
    }

    public int checkDiagonalLeft(Tile tile, Color color) {
        int startrow = tile.getRow();
        int startcol = tile.getCol();
        int max = 1;
        int up = startrow - 1;
        int down = startrow + 1;
        int right = startcol + 1;
        int left = startcol - 1;
        boolean stopup = true;
        boolean stopright = true;
        boolean stopdown = true;
        boolean stopleft = true;
        for(int i = 0; i < 20; i++) {
            if(left >= 0 && up >=0) {
                if(board[up][left].getPiece() != null){
                    if(stopleft && stopup && board[up][left].getPiece().getColor() == color){
                        max++;
                        up--;
                        left--;
                    } else {
                        stopup = false;
                        stopleft = false;
                    }
                }
            } else {
                stopup = false;
                stopleft = false;
            }

            if(right < 20 && down <20) {
                if(board[down][right].getPiece() != null){
                    if(stopright && stopdown && board[down][right].getPiece().getColor() == color){
                        max++;
                        down++;
                        right++;
                    } else {
                        stopdown = false;
                        stopright = false;
                    }
                }
            } else {
                stopdown = false;
                stopright = false;
            }

        }
        return max;

    }

}
