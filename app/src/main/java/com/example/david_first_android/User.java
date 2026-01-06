package com.example.david_first_android;

public class User {

    private String username;
    private int score;

    private int rate;


    public User(String username){
        this.username = username;
    }

    public String getUserName(){
        return username;
    }
    public int getScore(){
        return this.score;
    }

    public int getRate() {
        return rate;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setRate(int rate) {
        this.rate = rate;
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
