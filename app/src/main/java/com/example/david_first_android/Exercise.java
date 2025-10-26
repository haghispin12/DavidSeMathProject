package com.example.david_first_android;

import java.util.Random;

public class Exercise {
    private int num1;
    private int num2;
    private int result;
    private Interface inface;

    public Exercise(Interface inface){
        this.inface = inface;
    }

    public void generateLH(){
        Random r = new Random();
        num1 = r.nextInt(10);
        num2 = r.nextInt(10);


        result = num1 * num2;
        inface.showNumbers(num1 , num2);
    }

    public void generateTo20(){
        Random r = new Random();
        num1 = r.nextInt(10);
        num2 = r.nextInt(9)+11;


        result = num1 * num2;
    }

    public void generateEtgar(){
        Random r = new Random();
        num1 = r.nextInt(10);
        num2 = r.nextInt(89)+11;


        result = num1 * num2;
    }





}
