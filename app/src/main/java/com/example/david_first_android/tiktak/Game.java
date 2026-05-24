package com.example.david_first_android.tiktak;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public String player1;
    public String player2;
    public String status;
    public String turn;
    public List<String> board;


    public String player1Nickname;
    public String player2Nickname;

    public String winner;

    public Game() {}

    public Game(String player1, String player1Nickname) { // שמירת הכינוי של יוצר המשחק
        this.player1 = player1;
        this.player1Nickname = player1Nickname;
        this.status = "waiting";
        this.turn = player1;
        this.board = java.util.Arrays.asList("", "", "", "", "", "", "", "", "");
    }
}