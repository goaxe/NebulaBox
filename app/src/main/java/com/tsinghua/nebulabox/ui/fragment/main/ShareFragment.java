package com.tsinghua.nebulabox.ui.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.tsinghua.nebulabox.data.SeafShare;
import com.tsinghua.nebulabox.ui.activity.MainActivity;
import com.tsinghua.nebulabox.ui.base.BaseFragment;

import org.json.JSONException;

import java.util.List;

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
                mListView.deferNotifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (SeafException e) {
                e.printStackTrace();
            }
       }
    }

    class ListAdapter extends BaseAdapter {

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
            View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.share_list_item, null);
            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setText(share.getPath() + "===" + share.getCtime() + "===" + (share.getExpired() ? share.getExpireDate() : "--"));
            return view;
        }
    }
}
