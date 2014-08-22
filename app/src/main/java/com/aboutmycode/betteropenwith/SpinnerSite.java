package com.aboutmycode.betteropenwith;

/**
 * Created by Giorgi on 8/22/2014.
 */
public class SpinnerSite {
    private long id;
    private int icon;
    private String url;
    private String title;

    public SpinnerSite(String title, int icon, long id, String url) {
        this.title = title;
        this.icon = icon;
        this.id = id;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return this.title;
    }

    public int getIcon() {
        return this.icon;
    }
}