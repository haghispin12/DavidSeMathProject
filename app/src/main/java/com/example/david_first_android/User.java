package com.example.david_first_android;

public class User {

    private String username;
    private int score;

    public User(String username){
        this.username = username;
    }

    public void setScore(boolean LH, boolean To20, boolean Etgar){
        if(LH){
            this.score += 5;
        }
        else if (To20) {
            this.score += 10;
        }
        else if (Etgar) {
            this.score += 20;
        }
    }


}
