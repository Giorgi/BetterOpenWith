package com.aboutmycode.betteropenwith.database;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.aboutmycode.betteropenwith.HandleItem;
import com.aboutmycode.betteropenwith.R;

import java.util.List;

public class HandleItemLoader extends CupboardCursorLoader<HandleItem> {

    public HandleItemLoader(Context context, SQLiteOpenHelper db) {
        super(context, db, HandleItem.class);
    }

    public HandleItemLoader(Context context, SQLiteOpenHelper db, String criteria, long id) {
        super(context, db, HandleItem.class, criteria, id);
    }

    @Override
    protected List<HandleItem> buildList() {
        List<HandleItem> handleItems = super.buildList();

        Resources resources = super.getContext().getResources();

        for (HandleItem handleItem : handleItems) {
            handleItem.setName(resources.getString(resources.getIdentifier(handleItem.getNameResource(), "string", R.class.getPackage().getName())));

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
                List<ResolveInfo> resInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

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