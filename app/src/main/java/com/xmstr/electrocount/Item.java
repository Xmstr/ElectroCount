package com.xmstr.electrocount;

/**
 * Created by xmast_000 on 11.07.2015.
 */
public class Item {
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long id;
    public String date;
    public String text;
}
