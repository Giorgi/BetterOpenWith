package com.aboutmycode.openwith.app;

import nl.qbusict.cupboard.annotation.Ignore;

public class HandleItem {
    private long _id;

    private String nameResource;
    private transient String name;
    private String darkIconResource;
    private String lightIconResource;

    private String appComponentName;

    private String intentData;
    private String intentType;

    private boolean enabled;

    private String packageName;
    private String className;
    private boolean installed;
    private boolean skipList;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
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

    public String getIntentData() {
        return intentData;
    }

    public void setIntentData(String intentData) {
        this.intentData = intentData;
    }

    public String getIntentType() {
        return intentType;
    }

    public void setIntentType(String intentType) {
        this.intentType = intentType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public boolean isSkipList() {
        return skipList;
    }

    public void setSkipList(boolean skipList) {
        this.skipList = skipList;
    }

    public String getAppComponentName() {
        return appComponentName;
    }

    public void setAppComponentName(String appComponentName) {
        this.appComponentName = appComponentName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}