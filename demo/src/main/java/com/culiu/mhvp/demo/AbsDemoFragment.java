package com.culiu.mhvp.demo;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.culiu.mhvp.R;
import com.culiu.mhvp.core.InnerScrollerContainer;
import com.culiu.mhvp.core.OuterScroller;
import com.culiu.mhvp.integrated.ptr.pulltorefresh.PullToRefreshBase;

/**
 * @author Xavier-S
 *
 * Extracted codes for demo. In you practice, there's no need to
 * create an AbsFragment to extend it. Just let your fragment
 * implement InnerScrollerContainer. It's ok and good enough.
 *
 * @date 2015.11.12 11:20
 */
public abstract class AbsDemoFragment extends Fragment implements InnerScrollerContainer, PullToRefreshBase.OnRefreshListener2 {

    /************* InnerScrollerContainer interface **************/

    protected OuterScroller mOuterScroller;
    protected int mIndex;
    @Override
    public void setOuterScroller(OuterScroller outerScroller, int myPosition) {
        if(outerScroller == mOuterScroller && myPosition == mIndex) {
            return;
        }
        mOuterScroller = outerScroller;
        mIndex = myPosition;

        if(getInnerScroller() != null) {
            getInnerScroller().register2Outer(mOuterScroller, mIndex);
        }
    }
    /************* InnerScrollerContainer interface End **************/



    /****************************** Test code ******************************/
    /**
     * Emulate http asynchronous loading
     */
    protected void requestData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onResponse();
            }
        }, 1500);
    }

    public abstract void onResponse();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /**
     * After pull to refresh, add a random picture
     */
    public View addRandomPic() {

        ImageView iv = new ImageView(getActivity());
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setImageResource(RandomPic.getInstance().getPicResId());
        iv.setAdjustViewBounds(true);

        LinearLayout.LayoutParams lpIv = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        LinearLayout ll = new LinearLayout(getActivity());
        ll.addView(iv, lpIv);
        getInnerScroller().addHeaderView(ll);

        return ll;
    }

    View tempChild = null;

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        // Update the whole state, and forbidden touch
        getInnerScroller().onRefresh(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Return to normal state
                getInnerScroller().onRefresh(false);
                // Test code
                switch (WelcomeActivity.demoType) {
                    case Pull_to_add_Magic_Header_Mixed:
                    case Pull_to_add_Magic_Header_Mixed_Complicated_header:
                        if (getActivity() instanceof DemoActivity) {
                            ((DemoActivity) getActivity()).addRandomPic();
                        }
                        break;
                    case Pull_to_add_Inner_Header_Mixed:
                        addRandomPic();
                        break;
                    default:
                        // nothing
                        break;
                }
                if (getInnerScroller().getReceiveView() instanceof PullToRefreshBase) {
                    ((PullToRefreshBase)getInnerScroller().getReceiveView()).onRefreshComplete();
                }
            }
        }, 2000);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
    }
    /****************************** Test code End ******************************/
}
