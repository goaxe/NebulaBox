package com.tsinghua.nebulabox.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tsinghua.nebulabox.R;
import com.tsinghua.nebulabox.data.DatabaseHelper;
import com.tsinghua.nebulabox.data.StorageManager;

class ClearCacheTask extends TaskDialog.Task {

    @Override
    protected void runTask() {
        StorageManager storageManager = StorageManager.getInstance();
        storageManager.clearCache();

        // clear cached data from database
        DatabaseHelper dbHelper = DatabaseHelper.getDatabaseHelper();
        dbHelper.delCaches();
    }
}

public class ClearCacheTaskDialog extends TaskDialog {
    @Override
    protected View createDialogContentView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete_cache, null);
        return view;
    }

    @Override
    protected void onDialogCreated(Dialog dialog) {
        dialog.setTitle(getString(R.string.settings_clear_cache_title));
    }

    @Override
    protected ClearCacheTask prepareTask() {
        ClearCacheTask task = new ClearCacheTask();
        return task;
    }
}
