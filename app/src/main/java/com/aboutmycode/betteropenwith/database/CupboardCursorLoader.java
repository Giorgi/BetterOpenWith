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

        Resources resources = super.getContext().getResources();

        for (HandleItem handleItem : iterable) {
            handleItem.setName(resources.getString(resources.getIdentifier(handleItem.getNameResource(), "string", R.class.getPackage().getName())));
            result.add(handleItem);

            if (!TextUtils.isEmpty(handleItem.getPackageName())) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage(handleItem.getPackageName());
                intent.setClassName(handleItem.getPackageName(), handleItem.getClassName());

                String intentData = handleItem.getIntentData();

                if (TextUtils.isEmpty(intentData)) {
                    intent.setType(handleItem.getIntentType());
                } else {
                    intent.setDataAndType(Uri.parse(intentData), handleItem.getIntentType());
                }

                PackageManager packageManager = getContext().getPackageManager();
                List<ResolveInfo> resInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                if (resInfo.size() == 1) {
                    handleItem.setSelectedAppLabel(resInfo.get(0).loadLabel(packageManager).toString());
                    handleItem.setSelectedAppIcon(resInfo.get(0).loadIcon(packageManager));
                } else {
                    Log.w("openwith", "other than one resolve info found");
                }
            }
        }

        iterable.close();
        db.close();
        return result;
    }

    public void update(HandleItem item) {
        SQLiteDatabase database = db.getWritableDatabase();
        cupboard().withDatabase(database).put(item);
        database.close();
    }
}