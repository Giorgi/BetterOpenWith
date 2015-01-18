package com.aboutmycode.betteropenwith;

public class HiddenApp {
    private Long _id;
    private long itemId;
    private String packageName;

    public HiddenApp() {
    }

    public HiddenApp(String packageName) {
        this.packageName = packageName;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HiddenApp hiddenApp = (HiddenApp) o;

        if (!packageName.equals(hiddenApp.packageName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return packageName.hashCode();
    }
}
