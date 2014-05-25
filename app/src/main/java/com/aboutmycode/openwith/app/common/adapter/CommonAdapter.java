package com.aboutmycode.openwith.app.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Giorgi on 5/25/2014.
 */
public class CommonAdapter<T> extends ArrayAdapter<T> {

    private final Context context;
    private final List<T> items;
    private int layout;
    private final IBindView<T> rowBinder;

    public CommonAdapter(Context context, List<T> items, int layout, IBindView<T> rowBinder) {
        super(context, layout, items);
        this.context = context;
        this.items = items;
        this.layout = layout;
        this.rowBinder = rowBinder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(layout, parent, false);
        }

        T item = items.get(position);

        return rowBinder.bind(rowView, item, context);
    }
}
