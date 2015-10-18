package com.aboutmycode.betteropenwith.database;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.aboutmycode.betteropenwith.HandleItem;
import com.aboutmycode.betteropenwith.HiddenApp;
import com.aboutmycode.betteropenwith.Site;

import java.util.ArrayList;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class HandleItemLoader extends CupboardCursorLoader<HandleItem> {
    private final int MATCH_ALL = 131072;

    public HandleItemLoader(Context context, SQLiteOpenHelper db) {
        super(context, db, HandleItem.class);
    }

    public HandleItemLoader(Context context, SQLiteOpenHelper db, String criteria, long id) {
        super(context, db, HandleItem.class, criteria, id);
    }

    @Override
    protected List<HandleItem> buildList() {
        List<HandleItem> handleItems = super.buildList();

        for (HandleItem handleItem : handleItems) {

            SQLiteDatabase database = db.getReadableDatabase();
            List<HiddenApp> hiddenApps = cupboard().withDatabase(database)
                    .query(HiddenApp.class).withSelection("itemId=?", String.valueOf(handleItem.getId()))
                    .query().list();
            database.close();

            handleItem.setHiddenApps(new ArrayList<>(hiddenApps));

            //If there is a selected app for handle item load details for that app.
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
                List<ResolveInfo> resInfo = packageManager.queryIntentActivities(intent, MATCH_ALL);

                if (resInfo.size() == 1) {
                    handleItem.setSelectedAppLabel(resInfo.get(0).loadLabel(packageManager).toString());
                    handleItem.setSelectedAppIcon(resInfo.get(0).loadIcon(packageManager));
                } else {
                    Log.w("openwith", "other than one resolve info found");
                }
            }
        }

        return handleItems;
    }
}

