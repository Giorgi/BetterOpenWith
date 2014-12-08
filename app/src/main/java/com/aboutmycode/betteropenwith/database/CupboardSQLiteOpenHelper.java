package com.aboutmycode.betteropenwith.database;

import android.content.Context;

import com.aboutmycode.betteropenwith.HandleItem;
import com.aboutmycode.betteropenwith.Site;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Giorgi on 5/29/2014.
 */
public class CupboardSQLiteOpenHelper extends SQLiteAssetHelper {
    static {
        cupboard().register(HandleItem.class);
        cupboard().register(Site.class);
    }

    public CupboardSQLiteOpenHelper(Context context) {
        super(context, "applist.db", null, 7);
    }
}