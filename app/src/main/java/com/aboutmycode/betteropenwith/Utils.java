package com.aboutmycode.betteropenwith;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

public class Utils {
    public static String getDeviceInfo(Context context) {
        String text = "Sent from: " + getDeviceName() + System.getProperty("line.separator");
        text = text + "Android version: " + Build.VERSION.RELEASE + System.getProperty("line.separator");
        text = text + "Android OS: " + Build.DISPLAY + System.getProperty("line.separator");
        try {
            text = text + "Application Version: " + getCurrentPackageInfo(context).versionName + System.getProperty("line.separator");
        } catch (PackageManager.NameNotFoundException e) {
        }
        text = text + System.getProperty("line.separator");
        return text;
    }

    public static PackageInfo getCurrentPackageInfo(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(context.getPackageName(),0);
    }

    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}