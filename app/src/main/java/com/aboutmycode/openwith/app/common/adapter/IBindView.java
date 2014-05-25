package com.aboutmycode.openwith.app.common.adapter;

import android.content.Context;
import android.view.View;

/**
 * Created by Giorgi on 5/25/2014.
 */
public interface IBindView<T> {
    View bind(View row, T item, Context context);
}
