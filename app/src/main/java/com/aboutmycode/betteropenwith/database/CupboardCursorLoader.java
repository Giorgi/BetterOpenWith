package com.aboutmycode.betteropenwith.database;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.aboutmycode.betteropenwith.HandleItem;
import com.aboutmycode.betteropenwith.R;
import com.aboutmycode.betteropenwith.common.AbstractListLoader;

import java.util.ArrayList;
import java.util.List;

import nl.qbusict.cupboard.DatabaseCompartment;
import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Giorgi on 5/29/2014.
 */
public class CupboardCursorLoader<T> extends AbstractListLoader<T> {
    SQLiteOpenHelper db = null;
    private final Class<T> typeClass;
    private final String criteria;
    private final long id;

    public CupboardCursorLoader(Context context, SQLiteOpenHelper db, Class<T> typeClass) {
        this(context, db, typeClass, null, -1);
    }

    public CupboardCursorLoader(Context context, SQLiteOpenHelper db, Class<T> typeClass, String criteria, long id) {
        super(context);
        this.db = db;
        this.typeClass = typeClass;
        this.criteria = criteria;
        this.id = id;
    }

    @Override
    protected List<T> buildList() {
        DatabaseCompartment.QueryBuilder<T> query = cupboard().withDatabase(db.getReadableDatabase()).query(typeClass);

        if (criteria != null) {
            query = query.withSelection(criteria, String.valueOf(id));
        }

        List<T> result = query.list();

        db.close();
        return result;
    }

    public void update(T item) {
        SQLiteDatabase database = db.getWritableDatabase();
        cupboard().withDatabase(database).put(item);
        database.close();
    }

    public <T> void delete(T item) {
        SQLiteDatabase database = db.getWritableDatabase();
        cupboard().withDatabase(database).delete(item);
        database.close();
    }

    public <T> void insert(T item) {
        SQLiteDatabase database = db.getWritableDatabase();
        cupboard().withDatabase(database).put(item);
        database.close();
    }
}