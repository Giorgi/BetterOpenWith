package com.aboutmycode.betteropenwith;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;

import it.gmariotti.changelibs.library.view.ChangeLogListView;

public class ChangelogDialogFragment extends DialogFragment {

    public ChangelogDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        ChangeLogListView chgList = (ChangeLogListView) layoutInflater.inflate(R.layout.changelog_fragment_dialog, null);

        return new AlertDialog.Builder(getActivity())
                .setTitle("Recent Changes")
                .setView(chgList)
                .setPositiveButton(android.R.string.ok, null)
                .create();

    }
}
