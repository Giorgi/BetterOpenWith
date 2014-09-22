package com.aboutmycode.betteropenwith.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.aboutmycode.betteropenwith.R;

public class YesNoDialogFragment extends DialogFragment {
    private static String arg_param_message = "ARG_PARAM_MESSAGE";
    private static String arg_param_title = "ARG_PARAM_TITLE";
    private static String arg_param_show_negative = "ARG_PARAM_SHOW_NEGATIVE";
    private String message;
    private String title;
    private boolean showNegative;

    public YesNoDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            message = arguments.getString(arg_param_message);
            title = arguments.getString(arg_param_title);
            showNegative = arguments.getBoolean(arg_param_show_negative);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                ((YesNoListener) getActivity()).yesClicked();
            }
        });

        if (showNegative) {
            alertDialogBuilder.setNegativeButton(android.R.string.cancel, null);
        }

        return alertDialogBuilder.create();
    }

    public static YesNoDialogFragment newInstance(String title, String message, boolean showNegative) {
        YesNoDialogFragment fragment = new YesNoDialogFragment();
        Bundle args = new Bundle();
        args.putString(arg_param_message, message);
        args.putString(arg_param_title, title);
        args.putBoolean(arg_param_show_negative, showNegative);
        fragment.setArguments(args);
        return fragment;
    }

    public static YesNoDialogFragment newInstance(String title, String message) {
        return newInstance(title, message, true);
    }
}