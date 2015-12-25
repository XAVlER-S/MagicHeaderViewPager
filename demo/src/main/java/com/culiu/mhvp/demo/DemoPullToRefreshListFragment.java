package com.culiu.mhvp.demo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.culiu.mhvp.R;
import com.culiu.mhvp.core.MagicHeaderUtils;
import com.culiu.mhvp.integrated.ptr.PullToRefreshInnerListView;

import java.util.ArrayList;

/**
 * Created by Xavier-S on 15/7/23.
 */
public class DemoPullToRefreshListFragment extends DemoListFragment {

    public static final String TAG = "sz[InnerPullToRefresh]";

    protected PullToRefreshInnerListView mPullToRefreshListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected View viewThis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mListView != null && viewThis != null) {
            if (viewThis.getParent() != null) {
                ((ViewGroup) viewThis.getParent()).removeView(viewThis);
            }
            return viewThis;
        }
        viewThis = inflater.inflate(R.layout.fragment_pulltorefresh_list, null);

        // TODO: Be sure have put PullToRefreshInnerListView or InnerListView in your layout and its height match parent or align parent's top.
        mPullToRefreshListView = (PullToRefreshInnerListView) viewThis.findViewById(R.id.pull_refresh_inner_listview);

        // Set refreshing scale. You may change it or delete it if needed.
        mPullToRefreshListView.setScaleRefreshing(0.568f);

        if(mPullToRefreshListView!=null) {
            mPullToRefreshListView.setOnRefreshListener(this);
        }

        mListView = mPullToRefreshListView.getRefreshableView();

        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setDividerHeight(0);

        // TODO: Every time listView initialized, don't forget to call registerToOuter.
        mListView.register2Outer(mOuterScroller, mIndex);

        // Demo: Use color to mark special areas.
        if(DemoConfig.ENABLE_COLOR) {
            // Optional: Customize empty content
            View colorView = new View(getActivity());
            colorView.setBackgroundColor(DemoConfig.COLOR_EMPTY_CONTENT);
            mListView.setCustomEmptyView(colorView);
            mListView.setCustomEmptyViewHeight(ViewGroup.LayoutParams.MATCH_PARENT, -1000);

            // Demo: color its auto completion area
            mListView.setContentAutoCompletionColor(DemoConfig.COLOR_CONTENT_AUTO_COMPLETION);
        }

        // Two ways to load data: 1. initAdapter();  then notifyDataSetChanged(); 2. requestData(); then initAdapter();
        // demonstrate the 2nd way
        requestData();

        return viewThis;
    }

    /************* InnerScrollerContainer interface End **************/


    /***********************     Test data      ***********************/

    protected void initAdapter() {
        if (mListItems == null) {
            mListItems = new ArrayList<Item>();
        }

        mAdapter = new BaseAdapter(

        ) {
            @Override
            public int getCount() {
                return mListItems.size();
            }

            @Override
            public Item getItem(int position) {
                return mListItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            int picHeight = (int) (MagicHeaderUtils.getScreenWidth(getActivity()) * 0.68f);

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_fragment, null);
                }
                ImageView iv = (ImageView) convertView.findViewById(R.id.iv);
                TextView tv = (TextView) convertView.findViewById(R.id.tv);
                iv.setImageResource(getItem(position).getDrawableResId());
                tv.setText(getItem(position).getTitle());

                if (iv.getLayoutParams() != null) {
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) iv.getLayoutParams();
                    if (lp.height != picHeight) {
                        lp.height = picHeight;
                        iv.setLayoutParams(lp);
                    }
                }
                return convertView;
            }
        };
        mPullToRefreshListView.setAdapter(mAdapter);
    }
}
