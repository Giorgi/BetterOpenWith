package com.aboutmycode.betteropenwith.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.aboutmycode.betteropenwith.R;

public class YesNoDialogFragment extends DialogFragment {
    private String message;

    public YesNoDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            message = arguments.getString("ARG_PARAM_MESSAGE");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.confirm));
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                ((YesNoListener) getActivity()).yesClicked();
            }
        });
        alertDialogBuilder.setNegativeButton(android.R.string.cancel, null);

        return alertDialogBuilder.create();
    }

    public static YesNoDialogFragment newInstance(String message) {
        YesNoDialogFragment fragment = new YesNoDialogFragment();
        Bundle args = new Bundle();
        args.putString("ARG_PARAM_MESSAGE", message);
        fragment.setArguments(args);
        return fragment;
    }
}