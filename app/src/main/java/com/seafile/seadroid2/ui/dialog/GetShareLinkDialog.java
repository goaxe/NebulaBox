//package com.seafile.seadroid2.ui.dialog;
//
//import android.app.Dialog;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//
//import com.seafile.seadroid2.R;
//import com.seafile.seadroid2.account.Account;
//import com.seafile.seadroid2.network.SeafConnection;
//
//public class GetShareLinkDialog extends TaskDialog {
//    private String repoID;
//    private String path;
//    private boolean isdir;
//    private SeafConnection conn;
//
//    public void init(String repoID, String path, boolean isdir, Account account) {
//        this.repoID = repoID;
//        this.path = path;
//        this.isdir = isdir;
//        this.conn = new SeafConnection(account);
//    }
//
//    @Override
//    protected View createDialogContentView(LayoutInflater inflater, Bundle savedInstanceState) {
//        return null;
//    }
//
//    @Override
//    protected boolean executeTaskImmediately() {
//        return true;
//    }
//
//    @Override
//    protected void onDialogCreated(Dialog dialog) {
//        dialog.setTitle(getActivity().getString(R.string.generating_link));
//        // dialog.setTitle(getActivity().getString(R.string.generating_link));
//    }
//
//    @Override
//    protected GetShareLinkTask prepareTask() {
//        GetShareLinkTask task = new GetShareLinkTask(repoID, path, isdir, conn);
//        return task;
//    }
//
//    public String getLink() {
//        if (getTask() != null) {
//            GetShareLinkTask task = (GetShareLinkTask)getTask();
//            return task.getResult();
//        }
//
//        return null;
//    }
//}