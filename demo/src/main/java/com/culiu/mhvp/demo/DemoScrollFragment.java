package com.culiu.mhvp.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.culiu.mhvp.R;
import com.culiu.mhvp.core.InnerScrollView;
import com.culiu.mhvp.core.InnerScroller;
import com.culiu.mhvp.core.MagicHeaderUtils;

/**
 * @author Xavier-S
 * @date 2015.11.09 14:07
 */
public class DemoScrollFragment extends AbsDemoFragment {

    /************* InnerScrollerContainer interface **************/
    protected InnerScrollView mScrollView;

    /**
     * TODO: make sure through this method, can get your InnerScroller(InnerListView) in your fragment.
     * @return
     */
    @Override
    public InnerScroller getInnerScroller() {
        return mScrollView;
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
        viewThis = inflater.inflate(R.layout.fragment_scrollview, null);

        // TODO: Be sure have put PullToRefreshInnerScrollView or InnerScrollView in your layout and its height match parent or align parent's top.
        mScrollView = (InnerScrollView) viewThis.findViewById(R.id.inner_scrollview);

        // TODO: Every time scrollview initialized, don't forget to call registerToOuter.
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

        return viewThis;
    }

    /************* InnerScrollerContainer interface End **************/


    /***********************     Test data      ***********************/

    private TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onResponse() {

        if(getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        initContent();
    }


    LinearLayout mContentLinearLayout;

    private void initContent() {
        mScrollView.clearContent();

        if (getActivity() == null) {
            return;
        }

        mContentLinearLayout = new LinearLayout(getActivity());
        mContentLinearLayout.setOrientation(LinearLayout.VERTICAL);

        // picture
        ImageView iv = new ImageView(getActivity());
        iv.setImageResource(RandomPic.getInstance().getPicResId());
        iv.setAdjustViewBounds(true);
        int _5dp = MagicHeaderUtils.dp2px(getActivity(), 5f);
        iv.setPadding(_5dp, 0, _5dp, _5dp);
        mContentLinearLayout.addView(iv, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Text data
        tv = new TextView(getActivity());
        tv.setText(getString(R.string.text_3_kingdoms_chapter_1));
        tv.setTextSize(20);
        tv.setTextColor(Color.parseColor("#2c3e50"));
        tv.setLineSpacing(0, 1.2f);
        tv.setPadding(_5dp, _5dp, _5dp, 0);
        mContentLinearLayout.addView(tv, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mScrollView.setContentView(mContentLinearLayout);
    }
}
