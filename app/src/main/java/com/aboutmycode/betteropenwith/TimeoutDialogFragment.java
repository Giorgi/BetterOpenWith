package com.aboutmycode.betteropenwith;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextView;

public class TimeoutDialogFragment extends DialogFragment {
    private static final String ARG_PARAM_USE_DEFAULT = "param1";
    private static final String ARG_PARAM_TIMEOUT = "param2";
    private static final String ARG_PARAM_HAS_PREFERRED = "param3";
    private static final String ARG_PARAM_SKIP_LIST = "param4";
    private static final String ARG_PARAM_ITEM_TYPE = "param5";

    private boolean useDefault;
    private int timeout;
    private boolean hasPreferred;
    private boolean skipList;
    private String itemType;

    public static TimeoutDialogFragment newInstance(boolean useDefault, int timeout, boolean hasPreferred, boolean skipList, String itemType) {
        TimeoutDialogFragment fragment = new TimeoutDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM_USE_DEFAULT, useDefault);
        args.putInt(ARG_PARAM_TIMEOUT, timeout);
        args.putBoolean(ARG_PARAM_HAS_PREFERRED, hasPreferred);
        args.putBoolean(ARG_PARAM_SKIP_LIST, skipList);
        args.putString(ARG_PARAM_ITEM_TYPE, itemType);
        fragment.setArguments(args);
        return fragment;
    }

    public TimeoutDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            useDefault = getArguments().getBoolean(ARG_PARAM_USE_DEFAULT);
            timeout = getArguments().getInt(ARG_PARAM_TIMEOUT);
            hasPreferred = getArguments().getBoolean(ARG_PARAM_HAS_PREFERRED);
            skipList = getArguments().getBoolean(ARG_PARAM_SKIP_LIST);
            itemType = getArguments().getString(ARG_PARAM_ITEM_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View view = activity.getLayoutInflater().inflate(R.layout.fragment_timeout_dialog, null);

        Resources resources = getResources();
        int globalTimeout = PreferenceManager.getDefaultSharedPreferences(activity).getInt("timeout", resources.getInteger(R.integer.default_timeout));

        int pickerValue = timeout;

        if (timeout == -1) {
            pickerValue = globalTimeout;
        }

        final NumberPicker timeoutNumberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
        timeoutNumberPicker.setMinValue(1);
        timeoutNumberPicker.setMaxValue(120);
        timeoutNumberPicker.setValue(pickerValue);

        final CheckBox useDefaultCheckbox = (CheckBox) view.findViewById(R.id.useDefault);
        useDefaultCheckbox.setChecked(useDefault);
        useDefaultCheckbox.setText(String.format(getString(R.string.use_default_timeout), globalTimeout));

        useDefaultCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                timeoutNumberPicker.setEnabled(!checked);
            }
        });

        final CheckBox skipListCheckBox = (CheckBox) view.findViewById(R.id.skipList);
        skipListCheckBox.setChecked(skipList);
        skipListCheckBox.setVisibility(hasPreferred ? View.VISIBLE : View.GONE);
        skipListCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                useDefaultCheckbox.setEnabled(!checked);
                timeoutNumberPicker.setEnabled(!checked && !useDefaultCheckbox.isChecked());
            }
        });

        useDefaultCheckbox.setEnabled(!skipList);
        timeoutNumberPicker.setEnabled(!useDefault && !skipList);

        String text = getString(R.string.custom_countdown_description_site);
        text = String.format(text, itemType);
        ((TextView) view.findViewById(R.id.text_dialog_message)).setText(text);

        return new AlertDialog.Builder(activity)
                .setTitle(this.getString(R.string.custom_countdown))
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((HandlerDetailsActivity) activity).timeoutChanged(useDefaultCheckbox.isChecked(), timeoutNumberPicker.getValue(), skipListCheckBox.isChecked());
                            }
                        }
                ).setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }
                ).setView(view).create();
    }
}