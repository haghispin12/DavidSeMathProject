package com.example.david_first_android.mathproject;

import android.graphics.Bitmap;
import android.net.Uri;

public class User {

    private String username;
    private int score;

    private int rate;
    private long id;
    private Uri uri;
    private Bitmap bitmap;


    public User(String username){

        this.username = username;
    }

    public User(String username, int score, int rate, long id, Bitmap bitmap) {
        this.username = username;
        this.score = score;
        this.rate = rate;
        this.id = id;
        this.bitmap = bitmap;
    }

    public String getUserName(){
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public int getScore(){

        return this.score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public int getRate() {
        return rate;
    }
    public void setRate(int rate) {
        this.rate = rate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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
