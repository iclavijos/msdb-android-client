package com.icesoft.msdb.android.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.icesoft.msdb.android.R;

public class OldVersionDialogFragment extends DialogFragment {

    private final String currentVersion, newVersion;

    public OldVersionDialogFragment(String currentVersion, String newVersion) {
        super();
        this.currentVersion = currentVersion;
        this.newVersion = newVersion;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder
            .setTitle(R.string.oldVersionTitle)
            .setMessage(getString(R.string.oldVersionMsg, currentVersion, newVersion))
            .setPositiveButton(R.string.oldVersionUpdate, (dialog, id) -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.icesoft.msdb.android"));
                startActivity(intent);
            })
            .setNeutralButton(R.string.oldVersionLater, (dialog, id) -> {
            });
        return builder.create();
    }
}