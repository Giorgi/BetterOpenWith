package com.aboutmycode.openwith.app;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectAppDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectAppDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_DATA = "data";

    // TODO: Rename and change types of parameters
    private ListView listView;
    private Uri data;
    private ResolveInfoAdapter adapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SelectAppDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectAppDialogFragment newInstance(Uri data) {
        SelectAppDialogFragment fragment = new SelectAppDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    public SelectAppDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            data = getArguments().getParcelable(ARG_PARAM_DATA);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(data, "application/pdf");

        Activity activity = getActivity();
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> resInfo = packageManager.queryIntentActivities(intent, 0);

        Collections.sort(resInfo, new ResolveInfo.DisplayNameComparator(packageManager));

        List<ResolveInfoDisplay> list = new ArrayList<ResolveInfoDisplay>();

        for (ResolveInfo item : resInfo) {
            if (item.activityInfo.packageName.equals(activity.getPackageName())) {
                continue;
            }

            ResolveInfoDisplay resolveInfoDisplay = new ResolveInfoDisplay();
            resolveInfoDisplay.setDisplayLabel(item.loadLabel(packageManager));
            resolveInfoDisplay.setDisplayIcon(item.loadIcon(packageManager));
            resolveInfoDisplay.setResolveInfo(item);

            list.add(resolveInfoDisplay);
        }

        adapter = new ResolveInfoAdapter(activity, android.R.id.text1, list);
        listView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(android.R.layout.list_content, container);

        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    }
}