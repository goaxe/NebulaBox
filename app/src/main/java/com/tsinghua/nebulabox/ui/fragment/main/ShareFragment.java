package com.tsinghua.nebulabox.ui.fragment.main;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tsinghua.nebulabox.R;
import com.tsinghua.nebulabox.SeafException;
import com.tsinghua.nebulabox.account.AccountManager;
import com.tsinghua.nebulabox.data.DataManager;
import com.tsinghua.nebulabox.data.SeafShare;
import com.tsinghua.nebulabox.ui.base.BaseFragment;
import com.tsinghua.nebulabox.util.Utils;

import org.json.JSONException;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Alfred on 2016/7/11.
 */
public class ShareFragment extends BaseFragment {

    private static final String DEBUG_TAG = "ShareFragment";

    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<SeafShare> shares;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        mListView = (ListView) view.findViewById(R.id.listView);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);

        new GetShareThread().start();

        return view;
    }

    class GetShareThread extends Thread {

        public GetShareThread() {}

        @Override
        public void run() {
            try {
                AccountManager accountManager = new AccountManager(mActivity.getApplicationContext());
                DataManager dataManager = new DataManager(accountManager.getAccount());
                shares = dataManager.getShareLinks();
                mActivity.runOnUiThread(new Runnable() {
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

    class ListAdapter extends BaseAdapter {
        private String[] pictureFormat;
        private String[] documentFormat;

        public ListAdapter(){
            Resources resources = mActivity.getResources();
            pictureFormat = resources.getStringArray(R.array.format_picture);
            documentFormat = resources.getStringArray(R.array.format_document);
        }

        @Override
        public int getCount() {
            if (shares == null) {
                return 0;
            }
            return shares.size();
        }

        @Override
        public Object getItem(int i) {
            return shares.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            SeafShare share = shares.get(i);
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.share_list_item, viewGroup,false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.dateTextView.setText(share.getExpired() ? share.getExpireDate() : "--");
            viewHolder.repoTextView.setText(share.getRepoId());
            viewHolder.nameTextView.setText(share.getPath());
            viewHolder.frequencyTextView.setText(String.valueOf(share.getViewCount()));
            if (Utils.isNeedFormat(share.getPath(),pictureFormat)){
                viewHolder.iconImageView.setImageResource(R.drawable.file_image);
            } else if (Utils.isNeedFormat(share.getPath(),documentFormat)){
                viewHolder.iconImageView.setImageResource(R.drawable.file_ms_word);
            }else {
                viewHolder.iconImageView.setImageResource(R.drawable.file);
            }
            return convertView;
        }

        class ViewHolder{
            @Bind(R.id.icon_item_share_list_iv)
            ImageView iconImageView;
            @Bind(R.id.name_item_share_list_tv)
            TextView nameTextView;
            @Bind(R.id.repo_item_share_list_tv)
            TextView repoTextView;
            @Bind(R.id.date_item_share_list_tv)
            TextView dateTextView;
            @Bind(R.id.frequency_item_share_list_tv)
            TextView frequencyTextView;
            public ViewHolder(View view){
                ButterKnife.bind(this,view);
            }
        }
    }
}
