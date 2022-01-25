package com.appnext.database;

import org.litepal.crud.LitePalSupport;

public class TimeSequence extends LitePalSupport {
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
