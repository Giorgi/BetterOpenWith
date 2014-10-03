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

    private boolean useDefault;
    private int timeout;

    public static TimeoutDialogFragment newInstance(boolean useDefault, int timeout) {
        TimeoutDialogFragment fragment = new TimeoutDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM_USE_DEFAULT, useDefault);
        args.putInt(ARG_PARAM_TIMEOUT, timeout);
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

        final NumberPicker picker = (NumberPicker) view.findViewById(R.id.numberPicker);
        picker.setMinValue(1);
        picker.setMaxValue(120);
        picker.setValue(pickerValue);

        final CheckBox useDefaultCheckbox = (CheckBox) view.findViewById(R.id.useDefault);
        useDefaultCheckbox.setChecked(useDefault);
        useDefaultCheckbox.setText(String.format(getString(R.string.use_default_timeout), globalTimeout));

        picker.setEnabled(!useDefault);

        useDefaultCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                picker.setEnabled(!checked);
            }
        });

        ((TextView) view.findViewById(R.id.text_dialog_message)).setText(getString(R.string.custom_countdown_description));

        return new AlertDialog.Builder(activity)
                .setTitle(this.getString(R.string.custom_countdown))
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((HandlerDetailsActivity) activity).timeoutChanged(useDefaultCheckbox.isChecked(), picker.getValue());
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