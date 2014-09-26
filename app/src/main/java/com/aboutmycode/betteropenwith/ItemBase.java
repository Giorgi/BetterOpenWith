package com.aboutmycode.betteropenwith;

/**
 * Created by Giorgi on 8/22/2014.
 */
public class ItemBase {
    long _id;
    private String appComponentName;
    private String intentData;
    private String intentType;
    private boolean enabled;
    private String packageName;
    private String className;
    private boolean installed;
    private boolean skipList;
    private boolean useGlobalTimeout;
    private int customTimeout;

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public String getAppComponentName() {
        return appComponentName;
    }

    public void setAppComponentName(String appComponentName) {
        this.appComponentName = appComponentName;
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

    public boolean isUseGlobalTimeout() {
        return useGlobalTimeout;
    }

    public void setUseGlobalTimeout(boolean useGlobalTimeout) {
        this.useGlobalTimeout = useGlobalTimeout;
    }

    public int getCustomTimeout() {
        return customTimeout;
    }

    public void setCustomTimeout(int customTimeout) {
        this.customTimeout = customTimeout;
    }
}
