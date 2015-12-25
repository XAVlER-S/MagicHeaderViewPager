/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless
 * required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package com.culiu.mhvp.integrated.ptr;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.View;

import com.culiu.mhvp.integrated.ptr.pulltorefresh.PullToRefreshBase;
import com.culiu.mhvp.integrated.ptr.R;
import com.culiu.mhvp.integrated.ptr.pulltorefresh.OverscrollHelper;
import com.culiu.mhvp.core.InnerScrollView;

public class PullToRefreshInnerScrollView extends PullToRefreshBase<InnerScrollView> {

    public PullToRefreshInnerScrollView(Context context) {
        super(context);
    }

    public PullToRefreshInnerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshInnerScrollView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshInnerScrollView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    InnerScrollView scrollView;
    @Override
    protected InnerScrollView createRefreshableView(Context context, AttributeSet attrs) {
        if(VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
            scrollView=new InternalScrollViewSDK9(context, attrs);
        } else {
            scrollView=new InnerScrollView(context, attrs);
        }

        scrollView.setId(R.id.scrollview);
        // Set it so that it can be reached from InnerSroller -- Xavier-S
        scrollView.setReceiveView(this);
        return scrollView;
    }

    @Override
    protected boolean isReadyForPullStart() {
        return mRefreshableView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        View scrollViewChild=mRefreshableView.getChildAt(0);
        if(null != scrollViewChild) {
            return mRefreshableView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
        }
        return false;
    }

    @TargetApi(9)
    final class InternalScrollViewSDK9 extends InnerScrollView {

        public InternalScrollViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
            int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue=
                super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
                    isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(PullToRefreshInnerScrollView.this, deltaX, scrollX, deltaY, scrollY, getScrollRange(),
                isTouchEvent);

            return returnValue;
        }

        /**
         * Taken from the AOSP ScrollView source
         */
        private int getScrollRange() {
            int scrollRange=0;
            if(getChildCount() > 0) {
                View child=getChildAt(0);
                scrollRange=Math.max(0, child.getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
            }
            return scrollRange;
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            View view=(View)getChildAt(getChildCount() - 1);
            int diff=(view.getBottom() - (getHeight() + getScrollY()));// Calculate the scrolldiff
            if(diff == 0) { // if diff is zero, then the bottom has been reached
//                Log.d("scrollview", "MyScrollView: Bottom has been reached");
                if(mOnReachBottomListener != null) {
                    mOnReachBottomListener.onReachBotton();
                }
            } else {
                if(mOnReachBottomListener != null) {
                    mOnReachBottomListener.onLeaveBottton();
                }
            }
            super.onScrollChanged(l, t, oldl, oldt);
        }

    }

    public interface OnReachBottomListener {

        void onReachBotton();

        void onLeaveBottton();
    }

    OnReachBottomListener mOnReachBottomListener;

    public void setOnReachBottomListener(OnReachBottomListener mOnReachBottomListener) {
        this.mOnReachBottomListener=mOnReachBottomListener;
    }


    /********************* Custom Listener **************************/
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if(getState() == State.OVERSCROLLING) {
            setState(State.RESET);
            if(scrollView.getOuterScroller() != null) {
                scrollView.getOuterScroller().onInnerPullToRefreshScroll(0);
            }
        }
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.onScrollChangeListener != null) {
            onScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
        }
        if(t<=0 && oldt <=0) {
            informOuterScrollerThatImScrolling(t);
        }
    }

    private void informOuterScrollerThatImScrolling(int t) {
        if(scrollView != null && scrollView.getOuterScroller()!=null) {
            scrollView.getOuterScroller().onInnerPullToRefreshScroll(t);
        }
    }

    private OnScrollChangeListener onScrollChangeListener;

    private void setOnScrollChangeListener(OnScrollChangeListener listener) {
        this.onScrollChangeListener = listener;
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

}
