package com.culiu.mhvp.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.culiu.mhvp.R;
import com.culiu.mhvp.integrated.ptr.PullToRefreshInnerScrollView;

/**
 * @author Xavier-S
 * @date 2015.11.09 14:07
 */
public class DemoPullToRefreshScrollFragment extends DemoScrollFragment {


    protected PullToRefreshInnerScrollView mPullToRefreshScrollView;

    public static final String TAG = "sz[InnerPullToRefresh]";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected View viewThis;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mScrollView != null && viewThis != null) {
            if (viewThis.getParent() != null) {
                ((ViewGroup) viewThis.getParent()).removeView(viewThis);
            }
            return viewThis;
        }
        viewThis = inflater.inflate(R.layout.fragment_pulltorefresh_scrollview, null);

        // TODO: Be sure have put PullToRefreshInnerScrollView or InnerScrollView in your layout. ensure your ListView's height match parent or align parent's top.
        mPullToRefreshScrollView = (PullToRefreshInnerScrollView) viewThis.findViewById(R.id.pull_refresh_inner_scrollview);

        // Set refreshing scale. You may change it or delete it if you need.
        mPullToRefreshScrollView.setScaleRefreshing(0.568f);

        if(mPullToRefreshScrollView!=null) {
            mPullToRefreshScrollView.setOnRefreshListener(this);
        }

        mScrollView = mPullToRefreshScrollView.getRefreshableView();

        // TODO: Every time listView initialized, don't forget to call registerToOuter.
        mScrollView.register2Outer(mOuterScroller, mIndex);

        // Demo: Use color to mark special areas.
        if(DemoConfig.ENABLE_COLOR) {
            // Optional: Customize empty content
            View colorView = new View(getActivity());
            colorView.setBackgroundColor(DemoConfig.COLOR_EMPTY_CONTENT);
            mScrollView.setCustomEmptyView(colorView);
            mScrollView.setCustomEmptyViewHeight(ViewGroup.LayoutParams.MATCH_PARENT, -300);

            // Demo: color its auto completion area
            mScrollView.setContentAutoCompletionColor(DemoConfig.COLOR_CONTENT_AUTO_COMPLETION);
        }

        requestData();

        tempChild = addRandomPic();

        return viewThis;
    }

}
