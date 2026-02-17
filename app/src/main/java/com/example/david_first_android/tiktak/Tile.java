package com.example.david_first_android.tiktak;

import android.widget.ImageView;

public class Tile {


    private ImageView iv;
    private int type;   // 0 = null, 1 = X, 2 = O
    private int res;    // תמונה של X\O

    public Tile(ImageView iv) {
        this.iv = iv;   // שומר את ה-ImageView שהכניסו לו כפרמטר
        this.type = 0;  // משבצת ריקה - ברירת מחדל
        this.res = 0;   // אין תמונה
    }


    public ImageView getIv() {
        return iv;
    }

    public int getType() {
        return type;
    }

    public int getRes() {
        return res;
    }


    public void setIv(ImageView iv) {
        this.iv = iv;
    }
    public void setType(int type) {
        this.type = type;
    }

    public void setRes(int res) {
        this.res = res;
    }
}
