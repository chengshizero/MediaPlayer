package com.example.administrator.mediaplayer;

/**
 * Created by Administrator on 2016/12/9.
 */
public class Song {
    private long id;
    private String title;
    private String album;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAlbum() {
        return album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }
}
