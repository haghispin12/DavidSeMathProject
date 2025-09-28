package com.example.david_first_android;

import java.util.Random;

public class Exercise {
    private int num1;
    private int num2;
    private int result;

    public Exercise(){

    }

    public void generateLH(){
        Random r = new Random();
        int num1 = r.nextInt(10);
        int num2 = r.nextInt(10);


        result = num1 * num2;
    }

    public void generateTo20(){
        Random r = new Random();
        int num1 = r.nextInt(10);
        int num2 = r.nextInt(9)+11;


        result = num1 * num2;
    }

    public void generateEtgar(){
        Random r = new Random();
        int num1 = r.nextInt(10);
        int num2 = r.nextInt(89)+11;


        result = num1 * num2;
    }





}
