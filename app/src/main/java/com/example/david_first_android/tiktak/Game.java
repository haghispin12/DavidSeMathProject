package com.example.david_first_android.tiktak;

import java.util.List;
import java.util.Arrays;

public class Game {
    public String player1;
    public String player2;
    public String status;
    public String turn;
    public List<String> board;

    public String player1Nickname;
    public String player2Nickname;

    public String winner;

    // שדה חדש עבור הטיימר - Firebase יזהה אותו ויסנכרן אותו אוטומטית
    public long timer;

    // בנאי ריק נדרש עבור Firebase Realtime Database
    public Game() {}

    public Game(String player1, String player1Nickname) {
        this.player1 = player1;
        this.player1Nickname = player1Nickname;
        this.status = "waiting";
        this.turn = player1;
        this.board = Arrays.asList("", "", "", "", "", "", "", "", "");
        this.timer = 10; // אתחול התחלתי לטיימר
    }
}