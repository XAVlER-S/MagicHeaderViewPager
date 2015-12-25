/**
 * ****************************************************************************
 * Copyright 2011, 2012 Chris Banes. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless
 * required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * *****************************************************************************
 */
package com.culiu.mhvp.integrated.ptr;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import com.culiu.mhvp.integrated.ptr.pulltorefresh.internal.LoadingLayout;
import com.culiu.mhvp.integrated.ptr.R;
import com.culiu.mhvp.integrated.ptr.pulltorefresh.LoadingLayoutProxy;
import com.culiu.mhvp.integrated.ptr.pulltorefresh.OverscrollHelper;
import com.culiu.mhvp.integrated.ptr.pulltorefresh.PullToRefreshAdapterViewBase;
import com.culiu.mhvp.integrated.ptr.pulltorefresh.internal.EmptyViewMethodAccessor;
import com.culiu.mhvp.core.InnerListView;


/**
 * InnerListView wrapped in PullToRefreshAdapterViewBase with several custom methods inside.
 * @author  Xavier-S
 * @date 2015.07.23
 */
public class PullToRefreshInnerListView extends PullToRefreshAdapterViewBase<InnerListView> {

    private static final String TAG = "sz[PTRInnerLV]";

    private LoadingLayout mHeaderLoadingView;

    private LoadingLayout mFooterLoadingView;

    private FrameLayout mLvFooterLoadingFrame;

    private boolean mListViewExtrasEnabled;

    public PullToRefreshInnerListView(Context context) {
        super(context);
    }

    public PullToRefreshInnerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshInnerListView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshInnerListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected void onRefreshing(final boolean doScroll) {
        /**
         * If we're not showing the Refreshing view, or the list is empty, the
         * the header/footer views won't show so we use the normal method.
         */
        ListAdapter adapter = mRefreshableView.getAdapter();
        if (!mListViewExtrasEnabled || !getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
            super.onRefreshing(doScroll);
            return;
        }
//
        super.onRefreshing(false);
//
        final LoadingLayout origLoadingView, listViewLoadingView, oppositeListViewLoadingView;
        final int selection, scrollToY;

        switch (getCurrentMode()) {
            case MANUAL_REFRESH_ONLY:
            case PULL_FROM_END:
                origLoadingView = getFooterLayout();
                listViewLoadingView = mFooterLoadingView;
                oppositeListViewLoadingView = mHeaderLoadingView;
                selection = mRefreshableView.getCount() - 1;
                scrollToY = getScrollY() - getFooterSize();
//				smoothScrollTo(0);
                smoothScrollTo(getFooterSize());
                break;
            case PULL_FROM_START:
            default:
                origLoadingView = getHeaderLayout();
                listViewLoadingView = mHeaderLoadingView;
                oppositeListViewLoadingView = mFooterLoadingView;
                selection = 0;
                scrollToY = (int) (getScrollY() + getHeaderSize() * LoadingLayout.SCALE_REFRESHING);
                smoothScrollTo((int) (-getHeaderSize() * LoadingLayout.SCALE_REFRESHING));
                break;
        }
        oppositeListViewLoadingView.setVisibility(View.GONE);
    }


    @Override
    protected void onReset() {
        /**
         * If the extras are not enabled, just call up to super and return.
         */
        if (!mListViewExtrasEnabled) {
            super.onReset();
            return;
        }

        final LoadingLayout originalLoadingLayout, listViewLoadingLayout;
        final int scrollToHeight, selection;
        final boolean scrollLvToEdge;

        switch (getCurrentMode()) {
            case MANUAL_REFRESH_ONLY:
            case PULL_FROM_END:
                originalLoadingLayout = getFooterLayout();
                listViewLoadingLayout = mFooterLoadingView;
                selection = mRefreshableView.getCount() - 1;
                scrollToHeight = getFooterSize();
                scrollLvToEdge = Math.abs(mRefreshableView.getLastVisiblePosition() - selection) <= 1;
                break;
            case PULL_FROM_START:
            default:
                originalLoadingLayout = getHeaderLayout();
                listViewLoadingLayout = mHeaderLoadingView;
                scrollToHeight = -getHeaderSize();
                selection = 0;
                scrollLvToEdge = Math.abs(mRefreshableView.getFirstVisiblePosition() - selection) <= 1;
                break;
        }

        // If the ListView header loading layout is showing, then we need to
        // flip so that the original one is showing instead
        if (listViewLoadingLayout.getVisibility() == View.VISIBLE) {

            // Set our Original View to Visible
            originalLoadingLayout.showInvisibleViews();

            // Hide the ListView Header/Footer
            listViewLoadingLayout.setVisibility(View.GONE);

            /**
             * Scroll so the View is at the same Y as the ListView header/footer, but only scroll if: we've pulled to refresh, it's
             * positioned correctly
             */
            if (scrollLvToEdge && getState() != State.MANUAL_REFRESHING) {
                mRefreshableView.setSelection(selection);
                setHeaderScroll(scrollToHeight);
            }
        }

        // Finally, call up to super
        super.onReset();
    }

    @Override
    protected LoadingLayoutProxy createLoadingLayoutProxy(final boolean includeStart, final boolean includeEnd) {
        LoadingLayoutProxy proxy = super.createLoadingLayoutProxy(includeStart, includeEnd);

        if (mListViewExtrasEnabled) {
            final Mode mode = getMode();

            if (includeStart && mode.showHeaderLoadingLayout()) {
                proxy.addLayout(mHeaderLoadingView);
            }
            if (includeEnd && mode.showFooterLoadingLayout()) {
                proxy.addLayout(mFooterLoadingView);
            }
        }

        return proxy;
    }

    protected InnerListView createListView(Context context, AttributeSet attrs) {
        final InnerListView lv;
        if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
            lv = new InternalListViewSDK9(context, attrs);
        } else {
            lv = new InternalListView(context, attrs);
        }
        return lv;
    }

    InnerListView lv;
    @Override
    protected InnerListView createRefreshableView(Context context, AttributeSet attrs) {
        lv = createListView(context, attrs);

        // Set it to this so it can be used in ListActivity/ListFragment
        lv.setId(android.R.id.list);
        // Set it to this so it can receive touch event instead of innerScroller -- Xavier-S
        lv.setReceiveView(this);
        return lv;
    }

    @Override
    protected void handleStyledAttributes(TypedArray a) {
        super.handleStyledAttributes(a);

        mListViewExtrasEnabled = a.getBoolean(R.styleable.PullToRefresh_ptrListViewExtrasEnabled, true);

        if (mListViewExtrasEnabled) {
            final FrameLayout.LayoutParams lp =
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                            Gravity.CENTER_HORIZONTAL);

            int layoutId = a.getResourceId(R.styleable.PullToRefresh_ptrHeaderLayout, R.layout.default_pull_to_refresh_header_vertical);

            // Create Loading Views ready for use later
            FrameLayout frame = new FrameLayout(getContext());
            mHeaderLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_START, a, layoutId);
            mHeaderLoadingView.setVisibility(View.GONE);
            frame.addView(mHeaderLoadingView, lp);
            mRefreshableView.addHeaderView(frame, null, false);

            mLvFooterLoadingFrame = new FrameLayout(getContext());
            mFooterLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_END, a, layoutId);
            mFooterLoadingView.setVisibility(View.GONE);
            mLvFooterLoadingFrame.addView(mFooterLoadingView, lp);

            /**
             * If the value for Scrolling While Refreshing hasn't been explicitly set via XML, enable Scrolling While Refreshing.
             */
            if (!a.hasValue(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
                setScrollingWhileRefreshingEnabled(true);
            }
        }
    }

    @TargetApi(9)
    final class InternalListViewSDK9 extends InternalListView {

        public InternalListViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
                                       int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue =
                    super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
                            isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(PullToRefreshInnerListView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

            return returnValue;
        }
    }

    protected class InternalListView extends InnerListView implements EmptyViewMethodAccessor {

        private boolean mAddedLvFooter = false;

        public InternalListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it when using Header/Footer Views and the list is empty.
             * This masks the issue so that it doesn't cause an FC. See Issue #66.
             */
            try {
                super.dispatchDraw(canvas);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it when using Header/Footer Views and the list is empty.
             * This masks the issue so that it doesn't cause an FC. See Issue #66.
             */
            try {
                return super.dispatchTouchEvent(ev);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void setAdapter(ListAdapter adapter) {
            // Add the Footer View at the last possible moment
            if (null != mLvFooterLoadingFrame && !mAddedLvFooter) {
//                addFooterView(mLvFooterLoadingFrame, null, false);
                mAddedLvFooter = true;
            }

            super.setAdapter(adapter);
        }

        @Override
        public void setEmptyView(View emptyView) {
            PullToRefreshInnerListView.this.setEmptyView(emptyView);
        }

        @Override
        public void setEmptyViewInternal(View emptyView) {
            super.setEmptyView(emptyView);
        }


    }

    /******************* Custom Methods ******************/
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if(getState() == State.OVERSCROLLING) {
            setState(State.RESET);
            if(lv.getOuterScroller() != null) {
                lv.getOuterScroller().onInnerPullToRefreshScroll(0);
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
        if(lv != null && lv.getOuterScroller()!=null) {
            lv.getOuterScroller().onInnerPullToRefreshScroll(t);
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
