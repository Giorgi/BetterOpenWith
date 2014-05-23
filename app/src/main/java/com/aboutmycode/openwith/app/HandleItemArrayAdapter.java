package com.aboutmycode.openwith.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class HandleItemArrayAdapter extends ArrayAdapter<HandleItem> {

    private Context context;
    private List<HandleItem> items;

    public HandleItemArrayAdapter(Context context, List<HandleItem> items) {
        super(context, R.layout.handler_types, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.handler_types, parent, false);
        }

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        HandleItem item = items.get(position);
        textView.setText(item.getName());
        imageView.setImageDrawable(context.getResources().getDrawable(item.getIcon()));

        return rowView;
    }
}

class HandleItem{
    private String name;
    private int icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
