package com.aboutmycode.openwith.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Giorgi on 5/10/2014.
 */
public class ResolveInfoAdapter extends ArrayAdapter<ResolveInfoDisplay> {
    private final Activity context;
    private final List<ResolveInfoDisplay> items;
    private final LayoutInflater inflater;
    private int mIconSize;

    public ResolveInfoAdapter(Activity context, int resource, List<ResolveInfoDisplay> objects) {
        super(context, resource, objects);

        this.context = context;
        this.items = objects;
        inflater = context.getLayoutInflater();

        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        mIconSize = am.getLauncherLargeIconSize();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.resolve_list_item, parent, false);

            final ViewHolder holder = new ViewHolder(view);
            view.setTag(holder);

            ViewGroup.LayoutParams lp = holder.icon.getLayoutParams();
            lp.width = lp.height = mIconSize;
        } else {
            view = convertView;
        }

        bindView(view, items.get(position));

        return view;
    }

    private void bindView(View view, ResolveInfoDisplay info) {
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.text.setText(info.getDisplayLabel());
        holder.icon.setImageDrawable(info.getDisplayIcon());
    }
}

class ViewHolder {
    public TextView text;
    public ImageView icon;

    public ViewHolder(View view) {
        text = (TextView) view.findViewById(android.R.id.text1);
        icon = (ImageView) view.findViewById(R.id.icon);
    }
}