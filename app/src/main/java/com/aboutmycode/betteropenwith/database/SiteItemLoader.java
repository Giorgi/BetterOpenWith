package com.aboutmycode.betteropenwith.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aboutmycode.betteropenwith.HiddenApp;
import com.aboutmycode.betteropenwith.Site;

import java.util.ArrayList;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class SiteItemLoader extends CupboardCursorLoader<Site> {

    public SiteItemLoader(Context context, SQLiteOpenHelper db, Class<Site> typeClass) {
        super(context, db, typeClass);
    }

    public SiteItemLoader(Context context, SQLiteOpenHelper db, Class<Site> typeClass, String criteria, long id) {
        super(context, db, typeClass, criteria, id);
    }

    @Override
    protected List<Site> buildList() {
        List<Site> sites = super.buildList();

        for (Site site : sites) {
            SQLiteDatabase database = db.getReadableDatabase();
            List<HiddenApp> hiddenApps = cupboard().withDatabase(database)
                    .query(HiddenApp.class).withSelection("siteId=?", String.valueOf(site.getId()))
                    .query().list();
            database.close();

            site.setHiddenApps(new ArrayList<>(hiddenApps));
        }

        return sites;
    }
}
