package com.aboutmycode.openwith.app;

import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

/**
 * Created by Giorgi on 5/10/2014.
 */
public class ResolveInfoDisplay {
    private CharSequence displayLabel;
    private Drawable displayIcon;
    private ResolveInfo resolveInfo;

    public CharSequence getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(CharSequence displayLabel) {
        this.displayLabel = displayLabel;
    }

    public Drawable getDisplayIcon() {
        return displayIcon;
    }

    public void setDisplayIcon(Drawable displayIcon) {
        this.displayIcon = displayIcon;
    }

    public ResolveInfo getResolveInfo() {
        return resolveInfo;
    }

    public void setResolveInfo(ResolveInfo resolveInfo) {
        this.resolveInfo = resolveInfo;
    }
}