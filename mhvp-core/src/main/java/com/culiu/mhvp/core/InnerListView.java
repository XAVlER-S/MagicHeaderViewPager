package com.culiu.mhvp.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.culiu.mhvp.core.specialview.InnerSpecialViewHelper;
import com.culiu.mhvp.core.util.IntegerVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * ListView in Fragment in ViewPager of MagicHeaderViewPager.
 *
 * @author Xavier-S
 * @date 2015.07.23
 */
public class InnerListView extends ListView implements InnerScroller, AbsListView.OnScrollListener {

    public static final String TAG = "szlc[InnerListView]";

    private InflateFirstItemIfNeededAdapter mInnerAdapter;

    public InnerListView(Context context) {
        super(context);
        initView();
    }

    public InnerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public InnerListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected View mEmptyHeader;

    private void initView() {
        // init Empty Header
        initEmptyHeader();
        checkCompat();
    }

    /**
     * Check compat things
     */
    private void checkCompat() {
        checkHeaderAdditionIfNeeded();
        checkScrollModeCompat();
    }

    // Go into effect on device SDK >= Android 3.0
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void checkScrollModeCompat() {
        if(needCompatScrollMode()) {
        } else {
            setOverScrollMode(OVER_SCROLL_NEVER);
        }
    }

    private static boolean needCompatScrollMode() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
    }

    private void initEmptyHeader() {
        mEmptyHeader = new FrameLayout(getContext());
        super.addHeaderView(mEmptyHeader, null, false);
    }

    @Override
    public int getInnerScrollY() {
        return getListViewScrollY();
    }

    /************************************************************
      **********         Interaction with Outer        *********
     ************************************************************/
    @Override
    public final void onScroll(final AbsListView absListView, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        if (!mAttached || mOuterScroller == null || mBlockMeasure) {
            return;
        }

        if (mIndex == mOuterScroller.getCurrentInnerScrollerIndex()) {
            triggerOuterScroll();
            recordScrollPosition(firstVisibleItem);
        }
    }

    private boolean mGettingScrollY = false;

    /**
     * Trigger Outer Scroller
     * {@hide}
     */
    @Override
    public final void triggerOuterScroll() {
        if (!mGettingScrollY && mOuterScroller != null) {
            mGettingScrollY = true;
            int scrollY = getInnerScrollY();
            if(scrollY != -1) {
                mOuterScroller.onInnerScroll(mIndex, scrollY);
            }
            mGettingScrollY = false;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldw == 0 && oldh == 0 && !mRendered) {
            mRendered = true;
            onRender();
        }
    }

    private boolean mRendered;
    /**
     * Based on onSizeChanged() at the first time.
     */
    private void onRender() {
        adjustEmptyHeaderHeight();
    }

    private IntegerVariable mEmptyHeaderHeight = new IntegerVariable(0);

    /**
     * (as its name)
     * {@hide}
     */
    @Override
    public final void adjustEmptyHeaderHeight() {
        if (mEmptyHeader == null || mOuterScroller == null || mOuterScroller.getHeaderHeight() == 0) {
            return;
        }
        if (mEmptyHeaderHeight.getValue() != mOuterScroller.getHeaderHeight()) {
            post(new Runnable() {
                @Override
                public void run() {
                    mEmptyHeader.setPadding(0, mOuterScroller.getHeaderHeight(), 0, 0);
                }
            });
            mEmptyHeaderHeight.setValue(mOuterScroller.getHeaderHeight());
            updateEmptyHeaderHeight();
        }
    }

    /************************************************************
     ****     save and restore position related to mhvp     ****
     ************************************************************/

    // item positon
    protected int mItemPosition = ORIGIN_ITEM_POSITION;
    protected static final int ORIGIN_ITEM_POSITION = -1;

    // first item's marginTop to magic header
    protected int mItemMarginTop2Header = ORIGIN_ITEM_MARGIN_TOP_2HEADER;
    protected static final int ORIGIN_ITEM_MARGIN_TOP_2HEADER = 0;

    protected int mLastHeaderVisibleHeight = 0;

    /**
     * （as its name）
     * <p/>
     * {@hide}
     */
    public final void recordScrollPosition(int firstVisibleItem) {
        mLastHeaderVisibleHeight = mOuterScroller.getHeaderVisibleHeight();
        if (getChildAt(0) != null) {
            int itemMarginTop = getChildAt(0).getTop();

            mItemPosition = firstVisibleItem;
            mItemMarginTop2Header = itemMarginTop - mLastHeaderVisibleHeight;
        }
    }

    /**
     * Scroll only responding to Magic Header's change.
     * {@hide}
     */
    public final void performScroll(final int itemMariginTop2Header) {

        if (!mAttached || mOuterScroller == null) {
            return;
        }

        mLastHeaderVisibleHeight = mOuterScroller.getHeaderVisibleHeight();

        if (getChildAt(0) != null) {
            if (mItemPosition < 0) {
                scrollToInnerTop();
            } else {
                setSelectionFromTop(mItemPosition, itemMariginTop2Header + mLastHeaderVisibleHeight);
            }
        }
    }

    /**
     * Method performScroll() usually produce substantial scroll action, unable to be frequently called.
     * While this method syncScroll() has verification inside, will not directly produce scrolling,
     * and can be frequently called.
     * {@hide}
     */
    @Override
    public final void syncScroll() {
        if (!mAttached || mOuterScroller == null) {
            return;
        }
        if (mOuterScroller.getHeaderVisibleHeight() != mLastHeaderVisibleHeight) {
            performScroll(mItemMarginTop2Header);
        }

    }

    /******************** Outer Settings ******************/

    protected OuterScroller mOuterScroller;
    private int mIndex = -1;

    @Override
    public OuterScroller getOuterScroller() {
        return mOuterScroller;
    }

    @Override
    public void register2Outer(OuterScroller outerScroller, int index) {

        if (outerScroller != null && (outerScroller != mOuterScroller || mIndex != index)) {
            mIndex = index;
            mOuterScroller = outerScroller;
            mOuterScroller.registerInnerScroller(index, this);
            getEmptyViewHelper().setOuterScroller(mOuterScroller);
            adjustEmptyHeaderHeight();
            checkEmptyAdapterInitialization();
        }
        if (mInnerScrollListener == null) {
            setOnScrollListener(null);
        }
    }

    public void checkEmptyAdapterInitialization() {
        if (mInnerAdapter != null) {
            return;
        }
        setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return null;
            }
        });
    }

    /*************************************************************
      *****               Touch event receiver              *****
     *************************************************************/
    public View getReceiveView() {
        return mReceiveView == null ? this : mReceiveView;
    }

    public void setReceiveView(View receiveView) {
        this.mReceiveView = receiveView;
    }

    private View mReceiveView;

    /************************************************************
      *****            Header counts management            *****
     ************************************************************/
    int mVisibleHeaderCount;

    @Override
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        if (needCompatHeaderAddition() && mHeaderContainerCompat != null && mHeaderContainerCompat != v) {
            mHeaderContainerCompat.addView(v);
        } else {
            super.addHeaderView(v, data, isSelectable);
            mVisibleHeaderCount++;
        }
    }

    public boolean removeHeaderView(View view) {
        boolean success = super.removeHeaderView(view);
        if(success) {
            mVisibleHeaderCount--;
        }
        return success;
    }

    private int getInvisibleHeaderCount() {
        return getHeaderViewsCount() - mVisibleHeaderCount;
    }

    /**
     * Call this method on refresh state changed, to ensure there's no response to touch event,
     * to avoid going into abnormal state.
     *
     * @param isRefreshing
     */
    public void onRefresh(boolean isRefreshing) {
        if (mOuterScroller != null) {
            mOuterScroller.updateRefreshState(isRefreshing);
        }
    }

    private int mScrollState = SCROLL_STATE_IDLE;

    @Override
    public final void onScrollStateChanged(AbsListView view, int scrollState) {
        this.mScrollState = scrollState;
        if (scrollState == SCROLL_STATE_IDLE) {

            if (mOuterScroller != null && mIndex == mOuterScroller.getCurrentInnerScrollerIndex()) {
                // bug fixed on MX3(API 17): trigger Magic Header not so well cuz onScroll() callback frequency is too low on that device.
                // 所以这儿再来trigger一下吧，防火防盗防魅族~XD
                triggerOuterScroll();
                recordScrollPosition(getFirstVisiblePosition());

                mOuterScroller.onInnerScrollerStop();
            }
        }
    }

    /************************************************************
      ****             OnScrollListener Decoration          ****
     ************************************************************/
    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mInnerScrollListener = new InnerScrollListener(l);
        super.setOnScrollListener(mInnerScrollListener);
    }

    private InnerScrollListener mInnerScrollListener;

    /**
     * Listener set by others
     **/
    OnScrollListener mOnScrollListener;

    /**
     * Decorate scroll listener.
     */
    class InnerScrollListener implements OnScrollListener {

        InnerScrollListener(OnScrollListener onScrollListener) {
            mOnScrollListener = onScrollListener;
        }

        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            InnerListView.this.onScrollStateChanged(absListView, scrollState);
            if (mOnScrollListener != null && mOnScrollListener != InnerListView.this) {
                mOnScrollListener.onScrollStateChanged(absListView, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            InnerListView.this.onScroll(absListView, i, i1, i2);
            if (mOnScrollListener != null && mOnScrollListener != InnerListView.this) {
                mOnScrollListener.onScroll(absListView, i, i1, i2);
            }
        }
    }

    /************************************************************
     ****                  Adapter Decoration               ****
     ************************************************************/
    /**
     * ListView also creates a decorator, so we can only place this decorator in middle.
     */
    public class InflateFirstItemIfNeededAdapter extends BaseAdapter {
        ListAdapter mAdapter;

        public InflateFirstItemIfNeededAdapter(ListAdapter adapter) {
            if(adapter == null) {
                throw new NullPointerException();
            }
            mAdapter = adapter;
        }

        public void setAdapter(ListAdapter adapter) {
            if(adapter == null) {
                throw new NullPointerException();
            }
            this.mAdapter = adapter;
        }

        public boolean hasStableIds() {
            return mAdapter.hasStableIds();
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            mAdapter.registerDataSetObserver(observer);
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            mAdapter.unregisterDataSetObserver(observer);
        }

        @Override
        public void notifyDataSetChanged() {
            if (mAdapter instanceof BaseAdapter) {
                ((BaseAdapter) mAdapter).notifyDataSetChanged();
            }
        }

        public void notifyDataSetInvalidated() {
            if (mAdapter instanceof BaseAdapter) {
                ((BaseAdapter) mAdapter).notifyDataSetInvalidated();
            }
        }

        public boolean areAllItemsEnabled() {
            return mAdapter.areAllItemsEnabled();
        }

        public boolean isEnabled(int position) {
            if (position >= getCount()) {
                return true;
            }
            return mAdapter.isEnabled(position);
        }

        public int getItemViewType(int position) {
            innerTempCount = mAdapter.getCount();

            // empty content +0
            if (isEmptyContent(position, innerTempCount)) {
                return mAdapter.getItemViewType(position);
            }

            // auto completion +1
            if (isAutoCompletion(position, innerTempCount)) {
                return mAdapter.getItemViewType(position) + 1;
            }

            // plain item +2
            return mAdapter.getItemViewType(position) + 2;
        }

        public int getViewTypeCount() {
            return mAdapter.getViewTypeCount() + 2;
        }

        /**
         * Get the origin decorated adapter.
         * @return
         */
        public ListAdapter getAdapter() {
            return mAdapter;
        }

        @Override
        public int getCount() {
            innerTempCount = mAdapter.getCount();
            if(mOuterScroller == null) {
                return innerTempCount;
            }

            if (innerTempCount == 0) {
                return 2; // empty content + auto completion
            }

            return innerTempCount + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position >= getCount()) {
                return null;
            }
            return mAdapter.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            if (position >= getCount()) {
                return -1L;
            }
            return mAdapter.getItemId(position);
        }

        public int empty_first_position = -127;

        private int innerTempCount;
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            innerTempCount = mAdapter.getCount();
            // Empty Content
            if (isEmptyContent(position, innerTempCount)) {
                View viewEmptyFirst = getInnerEmptyViewSafely();
                viewEmptyFirst.setTag(R.id.id_for_empty_content, "");
                LayoutParams lp =  new LayoutParams(MagicHeaderUtils.getScreenWidth(getContext()), getCustomEmptyViewHeight());
                viewEmptyFirst.setLayoutParams(lp);
                if (empty_first_position < -126) {
                    empty_first_position = position;
                }
                return viewEmptyFirst;
            }

            // Empty Completion View
            if(isAutoCompletion(position, innerTempCount)) {
                if(mDataStatus != DataStatus.CHANGING) {
                    convertView = configureAutoEmptyCompletionView(getGapHeight(position));
                } else {
                    // If just on notifyDataSetChanged(), the heights are not correct cuz children
                    // are still old ones and also they haven't been placed in the right way.
                    // So we can only give a large enough height for safety to avoid scroll-range-too-small risk;
                    // After refresh, heights can be calculated correctly then.
                    mDataStatus = DataStatus.IDLE;
                    convertView = configureAutoEmptyCompletionView(mOuterScroller.getContentAreaMaxVisibleHeight());
                    post(new Runnable() {
                        @Override
                        public void run() {
                            //Children new but heights old.
                            // So remeasure them to get right heights.
                            reMeasureHeights();
                            configureAutoEmptyCompletionView(getGapHeight(position));
                        }
                    });
                }
                return convertView;
            }

            return mAdapter.getView(position, convertView, parent);
        }

        private boolean isEmptyContent(int position, int innerCount) {
            if(innerCount == 0 && position == innerCount) {
                return true;
            }
            return false;
        }

        private boolean isAutoCompletion(int position, int innerCount) {
            if(innerCount == 0 && position == innerCount + 1) {
                return true;
            }
            if(position == innerCount) {
                return true;
            }
            return false;
        }
    }

    private boolean mBlockMeasure;

    private void setBlockMeasure(boolean blockMeasure) {
        this.mBlockMeasure = blockMeasure;
    }

    public boolean isBlockMeasure() {
        return mBlockMeasure;
    }

    /**
     * @param adapter
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        // v2.6.18 Get rid of limitation that sync position only after >= 2nd time.
        // This way, many kinds of lifecycle can be received, and become more robust.
        if (mNeedMaintainPositionWhenSetAdapter) {
            setBlockMeasure(true);
            setVisibility(INVISIBLE);
        }

        // 数据状态置为改变中
        mDataStatus = DataStatus.CHANGING;

        if(mInnerAdapter != null) {
            unRegisterPreDataSetObserver(mInnerAdapter.getAdapter());
        }

        // just one observer is ok, cuz notifyDataSetChanged() and getView()
        // are called in different message.
//        registerPostDataSetObserver(adapter);

        mInnerAdapter = new InflateFirstItemIfNeededAdapter(adapter);
        super.setAdapter(mInnerAdapter);

        registerPreDataSetObserver(adapter);

        if (mNeedMaintainPositionWhenSetAdapter) {
            onSetAdapterSuccess();
        }
    }

    private void onSetAdapterSuccess() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                scrollToInnerTop();

                if(isBlockMeasure()) {
                    setVisibility(VISIBLE);
                    if(mAttached) {
                        setBlockMeasure(false);
                    }
                }
            }
        };
        safelyPost(runnable);
    }

    /********************* About observer ******************/
    /**
     * @param adapter
     */
    private void unRegisterPreDataSetObserver(ListAdapter adapter) {
        if(adapter != null && mPreDataSetObserverRegistered) {
            adapter.unregisterDataSetObserver(mPreDataSetObserver);
            mPreDataSetObserverRegistered = false;
        }
    }

    private void registerPreDataSetObserver(ListAdapter newAdapter) {
        if(newAdapter != null && !mPreDataSetObserverRegistered) {
            mPreDataSetObserverRegistered = true;
            newAdapter.registerDataSetObserver(mPreDataSetObserver);
        }
    }

    private enum DataStatus{IDLE, CHANGING}

    // Only used and maintained for content auto completion view.
    private DataStatus mDataStatus = DataStatus.IDLE;

    boolean mPreDataSetObserverRegistered;
    DataSetObserver mPreDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            mDataStatus = DataStatus.CHANGING;
            super.onChanged();
        }

        @Override
        public void onInvalidated() {
            mDataStatus = DataStatus.CHANGING;
            super.onInvalidated();
        }
    };

    /**
     * Note that if you use :
     * ```
     * if( ListView.getAdapter() == null) {
     * ...
     * }
     * ```
     * to do sth., you have to realize in other ways, cuz ListView.getAdapter() may
     * be always not null.
     * @return
     */
    @Override
    public ListAdapter getAdapter() {
        return super.getAdapter();
    }

    public InflateFirstItemIfNeededAdapter getInnerAdapter() {
        return mInnerAdapter;
    }

    /************************************************************
      ***************        View Lifecycle       **************
     ************************************************************/
    boolean mAttached = false;
    boolean mHasDetached = false;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mAttached = true;

        if (mHasDetached) {
            onReAttached();
        }
    }

    /**
     * (as its name). Its implementation has been changed to a more safe one.
     */
    private void reMeasureHeights() {
        old_FirstVisiblePosition = old_LastVisiblePosition = 0;
        getInnerScrollY();
    }

    private void onReAttached() {
        // Directly call won't go into effect, so call it in the next loop.
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                performScroll(mItemMarginTop2Header);
                if (isBlockMeasure()) {
                    setVisibility(VISIBLE);
                    // must switch this state when attaching
                    if(mAttached) {
                        setBlockMeasure(false);
                    }
                }
            }
        };
        safelyPost(runnable);
    }

    /**
     * @param runnable
     */
    private void safelyPost(Runnable runnable) {
        if(mAttached || !mHasDetached) {
            // Used by normal setAdapter():  mAttatched not sure && mHasDetached = false，
            // and during attched:  mAttached = true && mHasDetached not sure.
            post(runnable);
        } else {
            // For setAdapter() during detached status after attached
            runnable.run();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mHasDetached = true;
        mAttached = false;
        super.onDetachedFromWindow();
    }

    /*************************************************************
      ***          Implements scroll related methods          ***
     *************************************************************/

    @Override
    public void scrollToTop() {
        setSelection(0);
    }

    @Override
    public void scrollToInnerTop() {
        if (mOuterScroller != null) {
            final int invisibleHeaderCount = getInvisibleHeaderCount();
            setSelectionFromTop(invisibleHeaderCount, mOuterScroller.getHeaderVisibleHeight());
        } else {
            setSelection(0);
        }
    }

    @Override
    public boolean isScrolling() {
        return mScrollState != SCROLL_STATE_IDLE;
    }

    /*************************************************************
      ************       Drawing optimization       *************
     *************************************************************/
    @Override
    public void draw(Canvas canvas) {
        final int restoreCount = canvas.save();
        if (mOuterScroller != null) {
            canvas.clipRect(0, mOuterScroller.getHeaderVisibleHeight(), getWidth(), getHeight());
        }
        super.draw(canvas);
        canvas.restoreToCount(restoreCount);
    }

    private final boolean mNeedMaintainPositionWhenSetAdapter = true;

    /***** Support addHeaderView() after setAdapter() in SDK <= 4.2   *************/
    private LinearLayout mHeaderContainerCompat;

    private void checkHeaderAdditionIfNeeded() {
        if (needCompatHeaderAddition()) {
            if (mHeaderContainerCompat == null) {
                mHeaderContainerCompat = new LinearLayout(getContext());
                mHeaderContainerCompat.setOrientation(LinearLayout.VERTICAL);
                if (mHeaderContainerCompat.getParent() == null) {
                    addHeaderView(mHeaderContainerCompat, null, true);
                }
            }
        }
    }

    private static boolean needCompatHeaderAddition() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /*********************************************************************
     ****         Customize empty content view and its height        ****
     *********************************************************************/
    private InnerSpecialViewHelper mEmptyViewHelper;

    private InnerSpecialViewHelper getEmptyViewHelper() {
        if(mEmptyViewHelper == null) {
            mEmptyViewHelper = new InnerSpecialViewHelper(getContext());
        }
        return mEmptyViewHelper;
    }

    @Override
    public void setCustomEmptyView(View emptyView) {
        getEmptyViewHelper().setCustomEmptyView(emptyView);
    }

    /**
     * Note: the sum of height and offset is always no more than outScroller's contentAreaMaxVisibleHeight for safety.
     *
     * @param height You may pass wrap_content, match_parent or a figure. Defaults to match_parent.
     * @param offset Height offset, positive larger and negative smaller. Defaults to 0.
     * @return
     */
    @Override
    public void setCustomEmptyViewHeight(int height, int offset) {
        getEmptyViewHelper().setCustomEmptyViewHeight(height, offset);
    }

    public int getCustomEmptyViewHeight() {
        return getEmptyViewHelper().getInnerEmptyViewHeightSafely();
    }

    public View getCustomEmptyView() {
        return getEmptyViewHelper().getCustomEmptyView();
    }

    private View getInnerEmptyViewSafely() {
        return getEmptyViewHelper().getInnerEmptyViewSafely();
    }

    /*********************************************************************
      ***          Content auto completion view and its height        ***
     *********************************************************************/
    @Override
    public void setContentAutoCompletionColor(int color) {
        getEmptyViewHelper().setContentAutoCompletionColor(color);
    }

    private View getAutoCompletionView() {
        return getEmptyViewHelper().getContentAutoCompletionView();
    }

    private View getAutoCompletionViewSafely() {
        return getEmptyViewHelper().getContentAutoCompletionViewSafely();
    }

    public void setContentAutoCompletionViewOffset(int offset) {
        getEmptyViewHelper().setContentAutoCompletionViewOffset(offset);
    }

    private View configureAutoEmptyCompletionView(int height) {
        View autoEmptyCompletion = getAutoCompletionViewSafely();
        LayoutParams lp = (LayoutParams) autoEmptyCompletion.getLayoutParams();
        if(lp == null) {
            lp =  new LayoutParams(MagicHeaderUtils.getScreenWidth(getContext()), height);
            autoEmptyCompletion.setLayoutParams(lp);
        } else {
            if(lp.height != height) {
                lp.height = height;
                autoEmptyCompletion.requestLayout();
            }
        }
        return autoEmptyCompletion;
    }

    /**
     * （as its name）
     * @return
     */
    private int getGapHeight(int position) {
        List<IntegerVariable> heights = getHeightsSafely();
        int heightSum = getItemHeightSum(position, heights);
        int itemAreaHeight = mOuterScroller.getContentAreaMaxVisibleHeight();
        int result =  Math.max(0, itemAreaHeight - heightSum - mEmptyViewHelper.getContentAutoCompletionViewOffset());
        return result;
    }
    
    /*************** ListView items' heights management *****************/

    ArrayList<IntegerVariable> heights;

    private static final int INVALID_RESULT = -1;
    // Their value will be update before use every time, so no need to clear them.
    private int old_FirstVisiblePosition;
    private int old_LastVisiblePosition;

    /**
     * Get ListView pixel scrollY
     * （Logic ScrollY, can be thousands, even tens of thousands ）
     */
    private int getListViewScrollY() {

        if(getChildCount() == 0) {
            return INVALID_RESULT;
        }

        int result = 0;

        // use a local variable to accelerate
        ArrayList<IntegerVariable> heightsLocal = getHeightsSafely();

        int firstVisiblePosition = getFirstVisiblePosition();
        int lastVisiblePosition = getLastVisiblePosition();

        // measure if the first or last position changed
        if(firstVisiblePosition != old_FirstVisiblePosition || lastVisiblePosition != old_LastVisiblePosition) {
            // update value
            old_FirstVisiblePosition = firstVisiblePosition;
            old_LastVisiblePosition = lastVisiblePosition;

            if( Math.max(heightsLocal.size() -1,getInvisibleHeaderCount()-1) < firstVisiblePosition) {
                Log.w(TAG, "Warning：heights.size() -1="+(heights.size() -1)+", firstVisiblePosition="+ firstVisiblePosition +", Some items may not be measured.");
            }
            MagicHeaderUtils.ensureCapacityWithEmptyObject(heightsLocal, lastVisiblePosition+1, IntegerVariable.class);

            int tempMeasureHeight;
            IntegerVariable tempMeaseredHeight;
            for(int i = Math.max(firstVisiblePosition,getInvisibleHeaderCount()); i<= lastVisiblePosition; i++) {

                tempMeasureHeight = getChildAt(i - firstVisiblePosition).getMeasuredHeight();
                tempMeaseredHeight = heightsLocal.get(i);

                if(tempMeasureHeight != tempMeaseredHeight.getValue()){
                    tempMeaseredHeight.setValue(tempMeasureHeight);
                }
            }

            // clear invalid value
            IntegerVariable tempIntegerVariable;
            for(int i=lastVisiblePosition+1;i<heightsLocal.size();i++) {
                tempIntegerVariable = heightsLocal.get(i);
                if(tempIntegerVariable.getValue() != 0) {
                    tempIntegerVariable.setValue(0);
                }
            }
        }

        for (int i = 0; i< firstVisiblePosition; i++) {
            result += heightsLocal.get(i).getValue();
        }

        final int top = getChildAt(0).getTop();

        result -= top;

        return result;
    }

    /**
     * (as its name)
     */
    private ArrayList<IntegerVariable> ensureEmptyHeaderHeight() {
        if(heights == null) {
            heights = new ArrayList<IntegerVariable>();
        }

        MagicHeaderUtils.ensureCapacityWithEmptyObject(heights, 1, IntegerVariable.class);

        heights.set(0, mEmptyHeaderHeight);
        return heights;
    }

    private ArrayList<IntegerVariable> getHeightsSafely() {
        if(heights == null) {
            ensureEmptyHeaderHeight();
        }
        return heights;
    }

    private void updateEmptyHeaderHeight() {
        ensureEmptyHeaderHeight();
        if (mItemPosition > ORIGIN_ITEM_POSITION) {
            mItemPosition = ORIGIN_ITEM_POSITION;
            mItemMarginTop2Header = ORIGIN_ITEM_MARGIN_TOP_2HEADER;
            performScroll(mItemMarginTop2Header);
        }
    }

    /**
     * Get heights of items excluding empty header, used for scrolling.
     * @param LastPosition
     * @param heights
     * @return
     */
    private int getItemHeightSum(int LastPosition, List<IntegerVariable> heights) {
        int heightSum = 0;
        int start = getInvisibleHeaderCount();
        int index = getHeaderViewsCount() + LastPosition;
        int end = Math.min(index+1, heights.size());
        for(int i = start; i < end; i++) {
            if(i == index) {
                continue;
            }
            heightSum += heights.get(i).getValue();
        }
        return heightSum;
    }
}