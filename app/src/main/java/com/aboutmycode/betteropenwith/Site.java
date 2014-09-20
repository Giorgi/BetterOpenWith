package com.aboutmycode.betteropenwith;

import android.graphics.drawable.Drawable;

/**
 * Created by Giorgi on 8/22/2014.
 */
public class Site extends ItemBase {
    private String name;
    private String iconResource;
    private String domain;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconResource() {
        return iconResource;
    }

    public void setIconResource(String iconResource) {
        this.iconResource = iconResource;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}