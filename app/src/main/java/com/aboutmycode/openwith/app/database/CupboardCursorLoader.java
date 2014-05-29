package com.aboutmycode.openwith.app.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.aboutmycode.openwith.app.HandleItem;
import com.aboutmycode.openwith.app.common.AbstractListLoader;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.qbusict.cupboard.DatabaseCompartment;
import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Giorgi on 5/29/2014.
 */
public class CupboardCursorLoader extends AbstractListLoader<HandleItem> {
    SQLiteOpenHelper db = null;
    private final String criteria;
    private final long id;

    public CupboardCursorLoader(Context context, SQLiteOpenHelper db) {
        this(context, db, null, -1);
    }

    public CupboardCursorLoader(Context context, SQLiteOpenHelper db, String criteria, long id) {
        super(context);
        this.db = db;
        this.criteria = criteria;
        this.id = id;
    }

    @Override
    protected List<HandleItem> buildList() {
        DatabaseCompartment.QueryBuilder<HandleItem> query = cupboard().withDatabase(db.getReadableDatabase())
                                                                .query(HandleItem.class);

        if (criteria != null) {
            query = query.withSelection(criteria, String.valueOf(id));
        }

        QueryResultIterable<HandleItem> iterable = query.query();

        List<HandleItem> result = new ArrayList<HandleItem>();

        for (HandleItem handleItem : iterable) {
            result.add(handleItem);
        }
        iterable.close();
        db.close();
        return result;
    }
}
