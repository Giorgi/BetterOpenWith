package com.aboutmycode.openwith.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aboutmycode.openwith.app.HandleItem;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Giorgi on 5/29/2014.
 */
public class CupboardSQLiteOpenHelper extends SQLiteAssetHelper {
    static {
        cupboard().register(HandleItem.class);
    }

    public CupboardSQLiteOpenHelper(Context context) {
        super(context, "applist.db", null, 1);
    }
}