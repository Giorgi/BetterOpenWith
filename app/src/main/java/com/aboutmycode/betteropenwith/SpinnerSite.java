package com.aboutmycode.betteropenwith;

/**
 * Created by Giorgi on 8/22/2014.
 */
public class SpinnerSite {
    private long id;
    private int icon;
    private String url;
    private String title;

    public SpinnerSite(long id) {
        this.id = id;
    }

    public SpinnerSite(String title, int icon, long id, String url) {
        this.title = title;
        this.icon = icon;
        this.id = id;
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpinnerSite that = (SpinnerSite) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
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