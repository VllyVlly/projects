package org.example.gomoku;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

import static org.example.gomoku.Game.HEIGHT;
import static org.example.gomoku.Game.WIDTH;

public class GameController{

    // Nodes in FXML file
    @FXML
    private Pane board;
    @FXML
    private Label gamelogPlayer1;
    @FXML
    private Label gamelogPlayer2;
    @FXML
    public Label inform;
    @FXML
    private Label turn;
    @FXML
    private Label max1;
    @FXML
    private Label max2;
    @FXML
    private Label moveslabel1;
    @FXML
    private Label moveslabel2;
    @FXML
    private Label timer;
    @FXML
    private Label chancesLabel;
    @FXML
    private Button revealButton;
    @FXML
    private Label strikesNote;
    @FXML
    private Label strikesLabel;

    // Scenes and stages
    private Parent root;
    private Scene scene;
    private Stage stage;
    private Game game;
    private Scene gameScene;

    // Game settings
    private int maxlength;
    private int maxlength1 = 0;
    private int maxlength2 = 0;
    public int moves1;
    public int moves2;
    public int currentturn;
    private int timelimit; // in seconds


    // Invisible Mode Variables
    private boolean invisibleMode = false;
    private boolean isBoardInvis = false;
    private int viewChances;
    private int strikes = 0;

    // FXML loader
    private FXMLLoader loader;

    // Handle results
    private ResultController resultController;
    private Scene resultScene;

    // Multiplayer
    public int playernum;
    private int port;
    private Host host;
    private Client client;
    private Server server;

    // Timer
    private GameTimer gameTimer;


    public void startGame() {
        if(playernum == 1){
            if (game == null) {
                game = new Game(this);
                game.setLength(maxlength);
                game.setStartTurn();
                game.createBoard();
            }

            server.setGameController(this);
            new Thread(server).start();

            host = new Host(port, game);
            host.setGameController(this);
            new Thread(host).start();

            currentturn = game.getStartTurn();

            if(viewChances == 0){
                invisibleMode = false;
                revealButton.setDisable(true);
                revealButton.setVisible(false);
            } else {
                invisibleMode = true;
                chancesLabel.setVisible(true);
                chancesLabel.setText("Chances: " + viewChances);
                revealButton.setDisable(false);
                revealButton.setVisible(true);
                strikesLabel.setVisible(true);
                strikesNote.setVisible(true);
            }

            if(game != null) {
                loadBoard(game);
                loadPiece(game);
                if(currentturn == 1) {
                    turn.setText("Current Turn: Player 1");
                } else if(currentturn == 2) {
                    turn.setText("Current Turn: Player 2");
                }
            }

        } else if (playernum == 2){
            if (game == null) {
                game = new Game(this);
                game.createBoard();
            }

            client.setGame(game);
            client.setController(this);
            new Thread(client).start();

            viewChances = client.getViewChances();
            if(viewChances == 0){
                invisibleMode = false;
                revealButton.setDisable(true);
                revealButton.setVisible(false);
            } else {
                invisibleMode = true;
                chancesLabel.setVisible(true);
                chancesLabel.setText("Chances: " + viewChances);
                revealButton.setDisable(false);
                revealButton.setVisible(true);
                strikesLabel.setVisible(true);
                strikesNote.setVisible(true);
            }

            game.setLength(client.getMax());
            currentturn = client.getStartTurn();
            maxlength = client.getMax();
            setMoves(client.getMoves());

            timelimit = client.getTimeLimit();
            makeTimer();
            startTimer();

            if(game != null) {
                loadBoard(game);
                loadPiece(game);
            }

            if(currentturn == 1) {
                turn.setText("Current Turn: Player 1");
            } else if(currentturn == 2) {
                turn.setText("Current Turn: Player 2");
            }
        }

        initializeResultMenu();
    }

    public void pauseMenu(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PauseMenu.fxml"));
        root = fxmlLoader.load();
        PauseController controller = fxmlLoader.getController();
        scene = new Scene(root);
        controller.setGameScene(gameScene);
        controller.setGameController(this);
        stage.setScene(scene);
        stage.show();
    }

    // Load board nodes with animation
    public void loadBoard(Game game) {
        board.getChildren().add(game.boardParent);
        game.boardParent.setOpacity(0);
        SequentialTransition boardAnimation = new SequentialTransition();

        FadeTransition containerFade = new FadeTransition(Duration.millis(10), game.boardParent);
        containerFade.setFromValue(0);
        containerFade.setToValue(1);
        boardAnimation.getChildren().add(containerFade);

        ParallelTransition tileAnimations = new ParallelTransition();

        int delayBetweenTiles = 4;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = game.board[y][x];

                FadeTransition tileFade = new FadeTransition(Duration.millis(4), tile.getRoot());
                tileFade.setFromValue(0);
                tileFade.setToValue(1);
                tileFade.setDelay(Duration.millis(delayBetweenTiles * (y * WIDTH + x)));

                tileAnimations.getChildren().add(tileFade);
            }
        }
        boardAnimation.getChildren().add(tileAnimations);
        boardAnimation.play();

        // If invisible mode is active
        if(isInvisibleModeOn()){
            isBoardInvis = true;
        }
    }

    // Invisible mode functions
    public void revealBoard(ActionEvent event){
        if(isBoardInvis){
            if(viewChances == 0){
                chancesLabel.setText("No chances left");
            } else {
                game.revealPieces();
                isBoardInvis = false;
                viewChances--;
                chancesLabel.setText("Chances: " + viewChances);
            }
        }
    }

    public void makeBoardDisappear(){
        if(invisibleMode && !isBoardInvis){
            game.makePiecesDisappear();
            isBoardInvis = true;
        }
    }

    public boolean isInvisibleModeOn(){
        return invisibleMode;
    }

    public boolean isBoardInvis(){
        return isBoardInvis;
    }

    public void increaseStrikes(){
        this.strikes++;
        strikesLabel.setText("Strikes: " + this.strikes);
        if(this.strikes == 3){
            strikes = 0;
            strikesLabel.setText("Strikes: " + this.strikes);
            if(currentturn == 1){
                reduceMoves1();
                updateMoveLabels();
                changeCurrentTurn();
                updateCurrentTurnLabels();
            } else {
                reduceMoves2();
                updateMoveLabels();
                changeCurrentTurn();
                updateCurrentTurnLabels();
            }

            if(playernum == 1){
                GameInfo turnInfo = new GameInfo();
                turnInfo.setturnChange(true, "Player 2");
                host.send(turnInfo);
                GameInfo startTimer = new GameInfo(true);
                host.send(startTimer);
            } else {
                GameInfo turnInfo = new GameInfo();
                turnInfo.setturnChange(true, "Player 1");
                client.send(turnInfo);
                GameInfo startTimer = new GameInfo(true);
                client.send(startTimer);
            }
        }
    }

    // Load pieces
    public void loadPiece(Game game) {
        Tile[][] gameBoard = game.getBoard();
        if (gameBoard == null) return;

        for (int row = 0; row < gameBoard.length; row++) {
            for (int col = 0; col < gameBoard[row].length; col++) {
                Tile tile = gameBoard[row][col];
                if (tile.getPiece() != null) {
                    board.getChildren().add(tile.getPiece().getRoot());
                }
            }
        }
    }

    // Timer functions
    public void makeTimer(){
        gameTimer = new GameTimer(timelimit, this);
    }

    public void startTimer(){
        gameTimer.startTimer();
    }

    public void stopTimer(){
        if(gameTimer != null) {
            gameTimer.cancelTimer();
        }
    }

    public void updateTimer(int seconds) {
        int minutes = seconds / 60;
        int second = seconds % 60;
        String timeString = minutes + ":" + (second < 10 ? "0" + second : second);
        timer.setText(timeString);
    }

    // Piece adding function
    public void addPieceOnClick() {
        if(playernum == 1){
            gamelogPlayer1.setText(" ");
            if(server.hasClient()){
                if(moves1 != 0 && moves2 != 0) {
                    if(currentturn == 1 && moves1 > 0){
                        if(game.checkSelected()){
                            Piece piece = game.addPiece(Color.BLACK);
                            if(piece != null){
                                makeBoardDisappear();
                                GameInfo serializablePiece = new GameInfo(piece.getRow(), piece.getCol(), "black");
                                host.send(serializablePiece);
                                GameInfo startTimer = new GameInfo(true);
                                host.send(startTimer);
                                currentturn = 2;
                                moves1--;
                                updateLabelsAfterPlacing(1);
                            }
                        } else {
                            gamelogPlayer1.setText("Please select a valid tile first.");
                        }
                    } else {
                        gamelogPlayer1.setText("It's not your turn.");
                    }
                } else {
                    resultMenu("Draw.");
                }
            } else {
                inform.setText("Please wait for an opponent.");
            }
        } else if(playernum == 2){
            gamelogPlayer2.setText(" ");
            if(moves1 != 0 && moves2 != 0) {
                if(currentturn == 2 && moves2 > 0){
                    if(game.checkSelected()){
                        Piece piece = game.addPiece(Color.WHITE);
                        if(piece != null){
                            makeBoardDisappear();
                            GameInfo serializablePiece = new GameInfo(piece.getRow(), piece.getCol(), "white");
                            client.send(serializablePiece);
                            GameInfo startTimer = new GameInfo(true);
                            client.send(startTimer);
                            currentturn = 1;
                            moves2--;
                            updateLabelsAfterPlacing(2);
                        }
                    } else {
                        gamelogPlayer2.setText("Please select a valid tile first.");
                    }
                } else {
                    gamelogPlayer2.setText("It's not your turn.");
                }
            } else {
                resultMenu("Draw.");
            }
        }

    }

    public void initializeResultMenu(){
        try {
            loader = new FXMLLoader(getClass().getResource("Result.fxml"));
            Parent result = loader.load();
            resultController = loader.getController();
            resultScene = new Scene(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resultMenu(String text) {
        resultController.setResult(text);
        stage.setScene(resultScene);
        stage.show();
    }

    // Update functions
    public void updateLabelsAfterPlacing(int playernum) {
        if(playernum == 1){
            updateMoveLabels();
            updateCurrentTurnLabels();
            Tile tile = game.getSelectedTile();
            int x;
            if(maxlength1 < (x = game.checkVertical(tile,Color.BLACK))){
                maxlength1 = x;
            } else if(maxlength1 < (x = game.checkHorizontal(tile,Color.BLACK))){
                maxlength1 = x;
            } else if(maxlength1 < (x = game.checkDiagonalLeft(tile,Color.BLACK))){
                maxlength1 = x;
            } else if(maxlength1 < (x = game.checkDiagonalRight(tile,Color.BLACK))){
                maxlength1 = x;
            }

            if(maxlength1 == maxlength) {
                resultMenu("Player 1 Wins.");
                GameInfo winnerInfo = new GameInfo("1");
                host.send(winnerInfo);
                endGameHost();
            } else {
                max1.setText("Max length: " + maxlength1);
            }
        } else if(playernum == 2){
            updateMoveLabels();
            updateCurrentTurnLabels();
            Tile tile = game.getSelectedTile();
            int x;
            if(maxlength2 < (x = game.checkVertical(tile,Color.WHITE))){
                maxlength2 = x;
            } else if(maxlength2 < (x = game.checkHorizontal(tile,Color.WHITE))){
                maxlength2 = x;
            } else if(maxlength2 < (x = game.checkDiagonalLeft(tile,Color.WHITE))){
                maxlength2 = x;
            } else if(maxlength2 < (x = game.checkDiagonalRight(tile,Color.WHITE))){
                maxlength2 = x;
            }

            if(maxlength2 == maxlength) {
                resultMenu("Player 2 Wins.");
                GameInfo winnerInfo = new GameInfo("2");
                client.send(winnerInfo);
                endGameClient();
            } else {
                max2.setText("Max length: " + maxlength2);
            }
        }
    }

    public void updateLabelsAfterReceiving(int playernum, Tile tile) {
        if(playernum == 1){
            currentturn++;
            moveslabel1.setText("Moves: " + moves1);
            turn.setText("Current Turn: Player 2");
            int x;
            if(maxlength1 < (x = game.checkVertical(tile,Color.BLACK))){
                maxlength1 = x;
            } else if(maxlength1 < (x = game.checkHorizontal(tile,Color.BLACK))){
                maxlength1 = x;
            } else if(maxlength1 < (x = game.checkDiagonalLeft(tile,Color.BLACK))){
                maxlength1 = x;
            } else if(maxlength1 < (x = game.checkDiagonalRight(tile,Color.BLACK))){
                maxlength1 = x;
            }

            if(maxlength1 == maxlength) {
                resultMenu("Player 1 Wins.");
            } else {
                max1.setText("Max length: " + maxlength1);
            }
        } else if(playernum == 2){
            currentturn--;
            moveslabel2.setText("Moves: " + moves2);
            turn.setText("Current Turn: Player 1");
            int x;
            if(maxlength2 < (x = game.checkVertical(tile,Color.WHITE))){
                maxlength2 = x;
            } else if(maxlength2 < (x = game.checkHorizontal(tile,Color.WHITE))){
                maxlength2 = x;
            } else if(maxlength2 < (x = game.checkDiagonalLeft(tile,Color.WHITE))){
                maxlength2 = x;
            } else if(maxlength2 < (x = game.checkDiagonalRight(tile,Color.WHITE))){
                maxlength2 = x;
            }

            if(maxlength2 == maxlength) {
                resultMenu("Player 2 Wins.");
            } else {
                max2.setText("Max length: " + maxlength2);
            }
        }
    }

    // Setter and getter methods
    public void setGameScene(Scene scene) {
        this.gameScene = scene;
    }

    public void setMaxLength(int maxlength) {
        this.maxlength = maxlength;
    }

    public void setMoves(int moves) {
        this.moves1 = moves;
        this.moves2 = moves;
        moveslabel1.setText("Moves: " + moves1);
        moveslabel2.setText("Moves: " + moves2);
    }

    public void setTimeLimit(String timelimit) {
        if(timelimit.equalsIgnoreCase("30 seconds")){
            this.timelimit = 30;
        } else if(timelimit.equalsIgnoreCase("1 minute")){
            this.timelimit = 60;
        } else if(timelimit.equalsIgnoreCase("2 minutes")){
            this.timelimit = 120;
        } else if(timelimit.equalsIgnoreCase("5 minutes")){
            this.timelimit = 300;
        }
    }

    public void setViewChances(int chances) {
        this.viewChances = chances;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPort(int port){
        this.port = port;
    }

    public void setPlayernum(int playernum) {
        this.playernum = playernum;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getMaxlength() {
        return maxlength;
    }

    public int getMoves() {
        return moves1;
    }

    public int getStartTurn() {
        return game.getStartTurn();
    }

    public int getCurrentTurn() {
        return currentturn;
    }

    public int getViewChances() {
        return viewChances;
    }

    public int getPlayerNum() {
        return playernum;
    }

    public int getTimeLimit() {
        return timelimit;
    }

    // Reduce moves function
    public void reduceMoves1(){
        if(moves1 != 0) {
            moves1--;
        }
    }

    public void reduceMoves2(){
        if(moves2 != 0) {
            moves2--;
        }
    }

    // Shutdown functions
    public void endOnExit(){
        if(playernum == 1){
            if(server.hasClient()){
                GameInfo winnerInfo = new GameInfo("3");
                host.send(winnerInfo);
            }
            try{
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            endGameHost();

        } else {
            GameInfo winnerInfo = new GameInfo("4");
            client.send(winnerInfo);
            try{
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            endGameClient();
        }
    }

    public void endGameHost(){
        server.stopHandlers();
        host.stop();
        server.serverStop();
        stopTimer();
    }

    public void endGameClient(){
        client.stop();
        stopTimer();
    }

    // Game related functions
    public void changeCurrentTurn() {
        if(currentturn == 1){
            currentturn = 2;
        } else {
            currentturn = 1;
        }
    }

    public void updateMoveLabels(){
        moveslabel1.setText("Moves: " + moves1);
        moveslabel2.setText("Moves: " + moves2);
    }

    public void updateCurrentTurnLabels(){
        if(currentturn == 1){
            turn.setText("Current Turn: Player 1");
        }
        if(currentturn == 2){
            turn.setText("Current Turn: Player 2");
        }
    }

    public void listenForDraw(){
        if(moves1 == 0 && moves2 == 0){
            resultMenu("Draw.");
        }
    }

}
