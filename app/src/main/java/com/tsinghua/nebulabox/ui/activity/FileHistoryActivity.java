package com.tsinghua.nebulabox.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tsinghua.nebulabox.R;
import com.tsinghua.nebulabox.SeafException;
import com.tsinghua.nebulabox.account.AccountManager;
import com.tsinghua.nebulabox.data.DataManager;
import com.tsinghua.nebulabox.data.SeafCommit;
import com.tsinghua.nebulabox.ui.base.BaseActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class FileHistoryActivity extends BaseActivity {

    private static final String DEBUG_TAG = "FileHistoryActivity";

    private ListView mListview;
    private ListAdapter mListAdapter;

    private List<SeafCommit> commits = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_history);
        mListview = (ListView) findViewById(R.id.listView);
        mListAdapter = new ListAdapter();
        mListview.setAdapter(mListAdapter);

        String path = getIntent().getStringExtra("PATH");
        String repoId = getIntent().getStringExtra("REPO_ID");
        Log.e(DEBUG_TAG, path + " " + repoId);
        new MyThread(repoId, path).start();
    }

    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return commits.size();
        }

        @Override
        public Object getItem(int i) {
            return commits.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = LayoutInflater.from(FileHistoryActivity.this).inflate(R.layout.file_history_item_layout, null);
            TextView textView = (TextView) view.findViewById(R.id.text);

            SeafCommit commit = commits.get(i);
            String creatorName = commit.getCreatorName();
            String desc = commit.getDesc();
            Log.e(DEBUG_TAG, creatorName + " " + desc);
            textView.setText(commit.getCreatorName() + " " + commit.getDesc());
            return view;

        }
    }

    class MyThread extends Thread {
        private String repoID;
        private String path;

        public MyThread(String repoID, String path) {
            this.repoID = repoID;
            this.path = path;
        }

        @Override
        public void run() {
            AccountManager accountManager = new AccountManager(getApplicationContext());
            DataManager dataManager = new DataManager(accountManager.getAccount());
            try {
                commits = dataManager.getFileHistory(repoID, path);
                mListview.deferNotifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (SeafException e) {
                e.printStackTrace();
            }
        }
    }
}
