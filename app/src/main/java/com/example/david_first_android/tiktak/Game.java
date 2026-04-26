package com.example.david_first_android.tiktak;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public String player1;
    public String player2;

    public List<String> board;

    public String turn;
    public String status;


    public Game() {}

    // יצירת משחק חדש
    public Game(String uid) {

        player1 = uid;
        player2 = null;

        board = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            board.add("");
        }

        turn = uid;
        status = "waiting";
    }
}