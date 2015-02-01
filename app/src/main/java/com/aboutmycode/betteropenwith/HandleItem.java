package com.aboutmycode.betteropenwith;

import android.graphics.drawable.Drawable;

public class HandleItem extends ItemBase {
    private String nameResource;
    private transient String name;
    private String darkIconResource;
    private String lightIconResource;

    private transient String selectedAppLabel;
    private transient Drawable selectedAppIcon;

    public String getSelectedAppLabel() {
        return selectedAppLabel;
    }

    public void setSelectedAppLabel(String selectedAppLabel) {
        this.selectedAppLabel = selectedAppLabel;
    }


    public String getNameResource() {
        return nameResource;
    }

    public void setNameResource(String nameResource) {
        this.nameResource = nameResource;
    }

    public String getDarkIconResource() {
        return darkIconResource;
    }

    public void setDarkIconResource(String darkIconResource) {
        this.darkIconResource = darkIconResource;
    }

    public String getLightIconResource() {
        return lightIconResource;
    }

    public void setLightIconResource(String lightIconResource) {
        this.lightIconResource = lightIconResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getSelectedAppIcon() {
        return selectedAppIcon;
    }

    public void setSelectedAppIcon(Drawable selectedAppIcon) {
        this.selectedAppIcon = selectedAppIcon;
    }
}

