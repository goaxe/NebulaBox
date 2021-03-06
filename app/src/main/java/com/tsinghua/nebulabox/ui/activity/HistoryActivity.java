package com.tsinghua.nebulabox.ui.activity;

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
import com.tsinghua.nebulabox.util.Utils;
import com.tsinghua.nebulabox.ui.base.BaseActivity;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryActivity extends BaseActivity {

    private static final String DEBUG_TAG = "HistoryActivity";

    @Bind(R.id.title_toolbar_main_tv)
    TextView titleTextView;
    @Bind(R.id.subtitle_toolbar_main_tv)
    TextView subTitleTextView;
    @Bind(R.id.listView)
    ListView mListview;
    private ListAdapter mListAdapter;

    private List<SeafCommit> commits = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_history);
        ButterKnife.bind(this);

        String title = getIntent().getStringExtra("TITLE");
        String path = getIntent().getStringExtra("PATH");
        String repoId = getIntent().getStringExtra("REPO_ID");

        titleTextView.setText(title);
        subTitleTextView.setVisibility(View.GONE);

        mListAdapter = new ListAdapter();
        mListview.setAdapter(mListAdapter);

        Log.e(DEBUG_TAG, path + " " + repoId);

        new GetHistoryThread(repoId, path).start();
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
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            SeafCommit commit = commits.get(position);
            if (convertView != null) {
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.file_history_item_layout, viewGroup, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }

            viewHolder.creatorNameTextView.setText(commit.getCreatorName());
            viewHolder.dateTextView.setText(Utils.translateCommitTime(commit.getCtime()*1000));
            viewHolder.descTextView.setText(commit.getDesc());
//            viewHolder.sizeTextView.setText(commit.get);
            return convertView;

        }
    }

    static class ViewHolder {
        @Bind(R.id.creator_name_item_file_history_tv)
        TextView creatorNameTextView;
        @Bind(R.id.date_item_file_history_tv)
        TextView dateTextView;
        @Bind(R.id.desc_item_file_history_tv)
        TextView descTextView;
//        @Bind(R.id.size_item_file_history_tv)
//        TextView sizeTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class GetHistoryThread extends Thread {
        private String repoID;
        private String path;

        public GetHistoryThread(String repoID, String path) {
            this.repoID = repoID;
            this.path = path;
        }

        @Override
        public void run() {
            AccountManager accountManager = new AccountManager(getApplicationContext());
            DataManager dataManager = new DataManager(accountManager.getAccount());
            try {
                if (this.path == null) {
                    commits = dataManager.getLibraryHistory(repoID);
                } else {
                    commits = dataManager.getFileHistory(repoID, path);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapter.notifyDataSetChanged();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (SeafException e) {
                e.printStackTrace();
            }
        }
    }
}
