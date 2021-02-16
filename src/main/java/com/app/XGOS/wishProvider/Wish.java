package com.app.XGOS.wishProvider;

public class Wish {
    private final Child child = new Child();
    private String text;
    private String date;

    public Child getChild() {
        return child;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
