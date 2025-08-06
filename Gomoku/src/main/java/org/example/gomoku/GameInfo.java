package org.example.gomoku;


import java.io.Serializable;

public class GameInfo implements Serializable {

    // Send game info between host and client

    private int row;
    private int col;
    private String color;
    private String winner;
    private boolean startTimer;
    private boolean turnChange;
    private String sendToWho;

    public GameInfo(int row, int col, String color) {
        this.row = row;
        this.col = col;
        this.color = color;
    }

    public GameInfo(String winner) {
        this.winner = winner;
    }

    public GameInfo(){

    }

    public GameInfo(boolean startTimer){
        this.startTimer = startTimer;
    }

    public void setturnChange(boolean turnChange, String sendToWho) {
        this.turnChange = turnChange;
        this.sendToWho = sendToWho;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getColor() {
        return color;
    }

    public String getWinner() {
        return winner;
    }

    public String getSendToWho() {return sendToWho;}

    public boolean hasColor() {
        return color != null;
    }

    public boolean hasWinner() {
        return winner != null;
    }

    public boolean startTimer(){
        return startTimer;
    }

    public boolean changeTurn(){
        return turnChange;
    }
}
