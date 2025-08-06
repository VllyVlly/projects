package org.example.gomoku;

import java.io.Serializable;

public class GameSettings implements Serializable {

    // Settings for game

    private int moves;
    private int max;
    private int startturn;
    private int timelimit;
    private int viewChances;

    public GameSettings(int moves, int max, int startturn, int timelimit, int viewChances) {
        this.moves = moves;
        this.max = max;
        this.startturn = startturn;
        this.timelimit = timelimit;
        this.viewChances = viewChances;
    }

    public int getMoves() {
        return moves;
    }

    public int getMax() {
        return max;
    }

    public int getStartTurn(){
        return startturn;
    }

    public int getTimeLimit() {
        return timelimit;
    }

    public int getViewChances() {
        return viewChances;
    }
}
