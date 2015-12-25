package com.culiu.mhvp.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.culiu.mhvp.core.layout.SizeSensitiveLinearLayout;
import com.culiu.mhvp.core.layout.TranslatableLinearLayout;
import com.culiu.mhvp.core.tabs.com.astuetz.PagerSlidingTabStrip;

/**
 * A ViewPager with a header can be fixed and many listFragments, gridFragments and scrollFragments inside.
 *
 * @author Xavier-S
 * @date 2015.07.21
 *
 */
public abstract class
        MagicHeaderViewPager extends FrameLayout implements OuterScroller {

    public static final String TAG = "sz[mhvp]";

    private TranslatableLinearLayout mHeader;
    private SizeSensitiveLinearLayout mCustomHeaders;

    protected ViewGroup mTabsArea;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ScrollableViewPager mViewPager;

    private FragmentPagerAdapter mPagerAdapter;
    // Header height excluding tabs
    private int mHeaderHeightExcludeTabs;
    // Whole header height
    private int mHeaderHeight;
    // Header's max trasition in Y direction
    private int mMaxHeaderTransition;
    private OnHeaderScrollListener mOnHeaderScrollListener;

    public MagicHeaderViewPager(Context context) {
        this(context, null);
    }

    public MagicHeaderViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagicHeaderViewPager(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    protected void setTabsArea(ViewGroup mTabsArea) {
        this.mTabsArea = mTabsArea;
    }

    protected void setPagerSlidingTabStrip(PagerSlidingTabStrip pagerSlidingTabStrip) {
        mPagerSlidingTabStrip = pagerSlidingTabStrip;
    }


    /**
     * Get Inner ViewPager. Unless special settings, get it as least as possible.
     * Some methods have been implemented by MagicHeaderViewPager or PagerSlidingTabStrip.
     *
     * @return
     */
    public ScrollableViewPager getViewPager() {
        return mViewPager;
    }

    protected void initView() {
        LayoutInflater.from(getContext()).inflate(
                R.layout.mhvp_layout, this, true);

        setClipChildren(false);

        checkForbiddenMultiTouch();

        mHeader = (TranslatableLinearLayout) findViewById(R.id.mhvp_header);

        mCustomHeaders = (SizeSensitiveLinearLayout) findViewById(R.id.mhvp_headerCustom);
        initTabsArea(mHeader);
        initStableAreaHeight();
        mViewPager = (ScrollableViewPager) findViewById(R.id.mhvp_pager);
        mViewPager.setOffscreenPageLimit(1);

        initListener();
    }

    @Deprecated
    private void setDrawingCacheEnable(boolean enable) {
        mCustomHeaders.setDrawingCacheEnabled(enable);
        if(enable) {
            mCustomHeaders.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_LOW);
        }
    }

    /**
     * Initialize height of stable area.
     */
    private final void initStableAreaHeight() {
        if(mTabsArea!=null) {
            // calculate the height value manually set in figure
            measureHeaderHeightIncremental(mTabsArea.getLayoutParams().height, 0, 0);
        }
    }

    private boolean mBlockHeaderMeasure = false;

    public boolean blockHeaderMeasure() {
        return mBlockHeaderMeasure;
    }

    /**
     *  If you want to use this method, set it true before enormously add Header.
     *  Before last time add header, set it false. Default to false, means not to block.
     *
     */
    public void setBlockHeaderMeasure(boolean blockHeaderMeasure) {
        if(mBlockHeaderMeasure != blockHeaderMeasure) {
            this.mBlockHeaderMeasure = blockHeaderMeasure;
        }
    }

    private void initListener() {
        setEmptyOnTouchListener(mHeader);

        mCustomHeaders.setOnSizeChangedListener(new SizeSensitiveLinearLayout.SizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                if (!mBlockHeaderMeasure) {
                    reMeasureCustomHeader();
                    adjustChildrenEmptyHeaderHeight();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            mHeader.requestLayout();
                        }
                    });
                }
            }
        });
    }

    /**
     * Set empty OnTouchListener on parent view, usually to avoid
     * parent view not responding to touch event, and pass to parent's parent.
     * So child views won't be affected.
     *
     * @param view
     */
    private final static void setEmptyOnTouchListener(View view) {
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    /**
     * Initialize your tabsArea. Either findViewById or inflate is Ok.
     * Don't forget to setTabsArea() and setPagerSlidingTabStrip() at the end.
     * Or else they will be considered null.<br>
     *
     *  e.g. code:<br>
     *  ```<br>
     *    {<br>
     *        <blockquote>
     *        ViewGroup tabsArea = (ViewGroup) findViewById(R.id.tabs_area); <br>
     *        PagerSlidingTabStrip pagerSlidingTabStrip= (PagerSlidingTabStrip) findViewById(R.id.tabs);<br>
     *          <br>
     *        pagerSlidingTabStrip.setTextColor(Color.BLACK);<br>
     *        pagerSlidingTabStrip.setBackgroundColor(Color.WHITE);<br>
     *        pagerSlidingTabStrip.setIndicatorColor(Color.BLUE);<br>
     *          <br>
     *        setTabsArea(tabsArea);<br>
     *        setPagerSlidingTabStrip(pagerSlidingTabStrip);<br>
 *            </blockquote>
     *    }<br>
     * ```
     *
     * @param container
     */
    protected abstract void initTabsArea(LinearLayout container);


    public void setPagerAdapter(FragmentPagerAdapter pagerAdapter) {
        if(!(pagerAdapter instanceof OuterPagerAdapter)) {
            throw new IllegalArgumentException("MagicHeaderViewPager's " +
                    "FragmentPagerAdapter must implements interface OuterPagerAdapter!");
        }

        mPagerAdapter = pagerAdapter;

        ((OuterPagerAdapter)mPagerAdapter).setOuterScroller(this);

        if(mViewPager != null) {
            mViewPager.setAdapter(mPagerAdapter);
            if(mPagerSlidingTabStrip !=null) {
                mPagerSlidingTabStrip.setViewPager(mViewPager);
            }
        }
    }

    /**
     * Obtain PagerSlidingTabStrip. You may get and customize it.
     * @return
     */
    public PagerSlidingTabStrip getPagerSlidingTabStrip() {
        return mPagerSlidingTabStrip;
    }

    private boolean mRefreshState = false;

    @Override
    public void onPageSelected(int position) {
        if(mInnerScrollers != null) {
            InnerScroller currentInnerScroller = mInnerScrollers.get(position);
            if (currentInnerScroller != null) {
                currentInnerScroller.syncScroll();
            }
        }
    }

    /**
     * Synchronize Header scroll position. It can be enormously called. With many verification
     * inside, innerScrollers won't perform scroll unless necessary.
     *
     * {@hide}
     */
    @Override
    public void syncPagesPosition(int currentIndex) {
        InnerScroller innerScroller;
        for (int i = 0; i < mInnerScrollers.size(); i++) {
            int key = mInnerScrollers.keyAt(i);
            if (currentIndex == key) {
                continue;
            }
            innerScroller = mInnerScrollers.valueAt(i);
            if (innerScroller != null) {
                innerScroller.syncScroll();
            }
        }
    }

    private int mScrollYIn = 0;
    private int mHeaderStart = 0;
    private int currentItem = 0;

    private boolean mHeaderIntercept = false;

    /**
     * Call back on the current innerScroller scrolls.
     * {@hide}
     */
    @Override
    public void onInnerScroll(int pageIndex, int scrollY) {
        if (pageIndex != mViewPager.getCurrentItem()) {
            return;
        }
        /* State： pagerIndex equal to mViewPager.getCurrentItem().
        First time state switching check.*/
        if (mViewPager.getCurrentItem() != currentItem) {
            currentItem = mViewPager.getCurrentItem();
            // The first time onScroll() called, get temp value
            // header's current visible height
            int headerVisibleHeight = getHeaderVisibleHeight();
            mScrollYIn = scrollY;
            mHeaderStart = mHeaderHeight - headerVisibleHeight;
            /* Once reach the top, clear its temp state. Header won't move
            until current innerScroller scroll to first item (exclude empty header),
            to improve user experience. */
            if (mHeaderStart == mMaxHeaderTransition) {
                mScrollYIn = mMaxHeaderTransition;
                mHeaderStart = mMaxHeaderTransition;
            }
            return;
            /* The code is placed above for avoiding abnormal state when
           tabs clicked to switch fragments, positionSelected will be called later than
           onScroll. */
        }

        // Start processing
        // ScrollY delta
        int headerTransitionY = calcHeaderTransition(mHeaderStart, scrollY, mScrollYIn, mMaxHeaderTransition);
        /* Once reach the top, clear its temp state. Header won't move
        until current innerSroller scroll to first item (exclude empty header),
        to improve user experience. */
        if (headerTransitionY == mMaxHeaderTransition) {
            mScrollYIn = mMaxHeaderTransition;
            mHeaderStart = mMaxHeaderTransition;
        }

        /* Only when scroll downwards and not in the intercepting status,
       header displacement will be recalculated. */
        if (!mHeaderIntercept && scrollY < mScrollYIn) {
            /* Unless real scrollY reach top, namely: scrollY < headerStart, header will scroll.*/
            /* Note: add "==", or else if headerStart is 0, scrollYIn will never be set 0.
               So this boundary value is needed. */
            if (scrollY <= mHeaderStart) {
                // Set scrollYIn value to headerStart
                mScrollYIn = mHeaderStart;
                // And recalculate displacement again
                headerTransitionY = calcHeaderTransition(mHeaderStart, scrollY, mScrollYIn, mMaxHeaderTransition);
            } else {
                /* If normally scroll doesn't happen, insert logic of HeaderAlwaysScrollWithInner.
                   If switch on and point down on Header: to intercept it.
                 */
                if(mHeaderAlwaysScrollWithInner && mPointDownOnHeader) {
                    if(!mHeaderIntercept) {
                        mHeaderIntercept = true;
                    }
                } else {
                    // Or else forbidden scroll
                    return;
                }
            }
        }

        if(mBlockChangeTempScrollY) {
            headerTransitionY = (int) MagicHeaderUtils.clamp(mTempScrollY, 0, mMaxHeaderTransition);
        }

        boolean handled = MagicHeaderUtils.setParamY(mHeader, headerTransitionY, MagicHeaderUtils.TranslationMethods.VIEW_SCROLL);
        if(handled) {
            if(!mBlockChangeTempScrollY) {
                mTempScrollY = headerTransitionY;
            }
            if(mOnHeaderScrollListener != null) {
                mOnHeaderScrollListener.onHeaderScroll(headerTransitionY);
            }
            checkIfNeedScrollToTop();
        }
    }

    private float TEMP_SCROLL_Y_INIT = -9999999;

    private float mTempScrollY = TEMP_SCROLL_Y_INIT;

    /**
     * （as its name）
     * @param headerStart
     * @param scrollY
     * @param scrollYIn
     * @param maxHeaderTransition max header displacement in Y
     * @return
     */
    private int calcHeaderTransition(int headerStart, int scrollY, int scrollYIn, int maxHeaderTransition) {
        int headerTransition = headerStart + MagicHeaderUtils.calcDelta(scrollY, scrollYIn);
        headerTransition = Math.min(headerTransition, maxHeaderTransition);
        if(mHeaderAlwaysScrollWithInner) {
            headerTransition = Math.max(0, headerTransition);
        }
        return headerTransition;
    }

    private void checkIfNeedScrollToTop(/*int headerTransitionY*/) {
        if(mHeaderAlwaysScrollWithInner && mPointDownOnHeader
                 && mTempScrollY==0
                && getHeaderHeight() > this.getMeasuredHeight()) {
            InnerScroller currentInnerScroller = getCurrentInnerScroller();
            if(currentInnerScroller != null) {
                currentInnerScroller.scrollToTop();
                clearHeaderRelatedData();
            }
        }
    }

    private void clearHeaderRelatedData() {
        updateScrollYInAndHeaderStart();
    }

    /**
     * Get magic header's visible height. This method has been optimized using temp value.
     *   In fact some of these methods is redundant. visibleHeight + HeaderScrollY = HeaderHeight
     * @return
     */
    @Override
    public int getHeaderVisibleHeight() {
        if(Math.abs(mTempScrollY - TEMP_SCROLL_Y_INIT) > 0.1) {
            return (int) (mHeaderHeight - mTempScrollY);
        }
        float scrollY = MagicHeaderUtils.getParamY(mHeader, MagicHeaderUtils.TranslationMethods.VIEW_SCROLL);
        scrollY = MagicHeaderUtils.clamp(scrollY, 0, mMaxHeaderTransition);
        if(!mBlockChangeTempScrollY) {
            mTempScrollY = scrollY;
        }
        return (int) (mHeaderHeight - scrollY);
    }

    @Override
    public int getHeaderHeight() {
        return mHeaderHeight;
    }



    /**
     * Call back from pullToRefresh
     * @param scrollY
     * {@hide}
     */
    public void onInnerPullToRefreshScroll(int scrollY) {
        MagicHeaderUtils.setParamY(mHeader, scrollY, MagicHeaderUtils.TranslationMethods.LAYOUT_PARAMS);
    }

    /**
     *   The second implementation of addHeaderView, which need measure whole CustomHeader.
     *   Finishing adding all before render, and it will be measured automatically.
     * @param view
     */
    public void addHeaderView(View view) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        addHeaderView(view, lp);
    }

    /**
     * AddHeaderView with customized LayoutParams
     * @param view
     * @param lp
     */
    public final void addHeaderView(View view, LinearLayout.LayoutParams lp) {
        mCustomHeaders.addView(view, lp);
    }

    /**
     * AddHeaderView with customized height in Pixel
     * @param view
     * @param height in Pixel
     */
    public final void addHeaderView(View view, int height) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height
        );
        mCustomHeaders.addView(view, lp);
    }

    /**
     * @param view
     */
    public void removeHeaderView(View view) {
        mCustomHeaders.removeView(view);
    }

    public final void adjustChildrenEmptyHeaderHeight() {
        if(mInnerScrollers == null) {
            return;
        }
        for (int i = 0; i < mInnerScrollers.size(); i++) {
            InnerScroller innerScroller = mInnerScrollers.valueAt(i);
            if (innerScroller != null) {
                innerScroller.adjustEmptyHeaderHeight();
            }
        }
    }

    /**
     * ReMeasure custom header's height.
     * Support condition: "StableArea != TabsArea".
     */
    private final void reMeasureCustomHeader() {
        int stableAreaHeight = mHeaderHeight - mMaxHeaderTransition;
        int tabsHeight;
        if(mTabsArea == null) {
            if(mPagerSlidingTabStrip != null) {
                tabsHeight = mPagerSlidingTabStrip.getMeasuredHeight();
            } else {
                tabsHeight = 0;
            }
        } else {
            tabsHeight = mTabsArea.getMeasuredHeight();
        }
        if(mCustomHeaders != null) {
            mHeaderHeightExcludeTabs = mCustomHeaders.getMeasuredHeight();
            mHeaderHeight = tabsHeight + mHeaderHeightExcludeTabs;
            mMaxHeaderTransition = mHeaderHeight - stableAreaHeight;
        }
    }

    /**
     * Method of single param will set the one value to three params.
     * @param heightIncremental
     */
    private final void measureHeaderHeightIncremental(int heightIncremental) {
        measureHeaderHeightIncremental(heightIncremental, heightIncremental, heightIncremental);
    }

    /**
     * @param headerHeightIncremental Incremental of whole header height
     * @param headerHeightExcludeTabsIncremental Incremental of area-excluding-tabs' height
     * @param maxHeaderTransitionIncremental Incremental of max header transition
     */
    private final void measureHeaderHeightIncremental(int headerHeightIncremental, int headerHeightExcludeTabsIncremental, int maxHeaderTransitionIncremental) {
        if(mHeader != null) {
            mHeaderHeight += headerHeightIncremental;
        }
        if(mPagerSlidingTabStrip!= null) {
            mHeaderHeightExcludeTabs += headerHeightExcludeTabsIncremental;
        }
        mMaxHeaderTransition += maxHeaderTransitionIncremental;
    }

    private SparseArrayCompat<InnerScroller> mInnerScrollers = new SparseArrayCompat<InnerScroller>();

    @Override
    public InnerScroller getCurrentInnerScroller() {
        if(mInnerScrollers!=null && mViewPager!=null) {
            return mInnerScrollers.get(mViewPager.getCurrentItem());
        }
        return null;
    }

    private static final int INVALID_INDEX = -2;
    @Override
    public int getCurrentInnerScrollerIndex() {
        if(mViewPager!=null) {
            return mViewPager.getCurrentItem();
        }
        return INVALID_INDEX;
    }

    /**
     * @param index
     * @param innerScroller
     * * {@hide}
     */
    @Override
    public void registerInnerScroller(int index, InnerScroller innerScroller) {
        if(innerScroller != null) {
            mInnerScrollers.put(index, innerScroller);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mRefreshState) {
            if(mProcessTouchEvent) {
                mProcessTouchEvent = false;
            }
            super.onInterceptTouchEvent(event);
            return true;
        }
        return super.onInterceptTouchEvent(event);
    }


    private float x;
    private float y;
    private float downX = -9999;
    private float downY;
    private float delta_X;
    private float delta_Y;


    /**
     * As holding a MotionEvent's reference is useless, using boolean instead.
     */
    private boolean mHasEventDown = false;

    /**
     * Only two states, so using boolean to optimize.
     * {@hide}
     */
    public boolean intercept2InnerScroller = false;

    /** Whether pointed down on Header. Generated from touch down, and destroyed by Inner scroll stop.）
     */
    private boolean mPointDownOnHeader;

    private final float TOUCH_CLICK_THRESHOLD = MagicHeaderUtils.dp2px(getContext(), 5);

    /**
     *  Maintained by dispatchTouchEvent() and interceptTouchEvent().
     *  Why interceptTouchEvent()？ cuz it's another exit of dispatchTouchEvent().
     */
    private boolean mProcessTouchEvent = false;

    /**
     * Maintained by Action_Move in dispatchTouchEvent()
     */
    private boolean mProcessTouchEventMove = false;


    /**
     * @param event
     * @return
     */
    public boolean dispatchTouchEvent(final MotionEvent event) {

        boolean handled = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(mProcessTouchEvent) {
                    return false;
                }
                InnerScroller currentInner = getCurrentInnerScroller();
                if( currentInner != null && currentInner.isScrolling()) {
                    syncPagesPosition(mViewPager.getCurrentItem());
                }

                mHasEventDown = true;
                downX = event.getX();
                downY = event.getY();
                if(mBlockChangeTempScrollY) {
                    mBlockChangeTempScrollY = false;
                }

                /**
                 *  whether point down on header, judged on point down.
                 *
                 *  Based on peculiarity of this layout, one special judging method is provided,
                 *   to improve execution effeciency.
                 */
                if(mHeader!=null && downY < mHeader.getVisualBottom()) {
                    mPointDownOnHeader = true;
                    /**
                     *  Refresh scrollYIn and HeaderStart at TouchDown again.
                     */
                    if(mHeaderAlwaysScrollWithInner) {
                        /**
                         *   If switch header always scroll with InnerScroller on,
                         *   Refresh scrollYIn and HeaderStart
                         */
                        updateScrollYInAndHeaderStart();
                    }
                }

                /** some phone will crash due to ViewPager's onTouch Event:
                / "Fatal Exception: java.lang.IllegalArgumentException
                / pointerIndex out of range" on Crashlytics,
                / so... try... catch... */
                try {
                    handled = super.dispatchTouchEvent(event);
                } catch (Exception iae) {
                }
                /**
                 *  If failed, mProcessTouchEvent won't be true forever, but will be set false
                 */
                mProcessTouchEvent = handled;
                break;
            case MotionEvent.ACTION_MOVE:
                if(mProcessTouchEventMove) {
                    return false;
                } else if(mProcessTouchEvent){
                    mProcessTouchEventMove = true;
                }

                if(downX < -9998) {
                    //initialize
                    downX = event.getX();
                    downY = event.getY();
                } else {
                    //normal execute
                    x = event.getX();
                    y = event.getY();
                    delta_X = x - downX;
                    delta_Y = y - downY;
                    /**
                     *  Only intercept when touch on Header area
                     */
                    if( mPointDownOnHeader ) {
                        if( !intercept2InnerScroller && Math.abs(delta_Y) > Math.abs(delta_X) && radiusLargerThan(delta_X, delta_Y, TOUCH_CLICK_THRESHOLD)) {
                            intercept2InnerScroller = true;
                        } else {
                        }
                    }
                }

                if(intercept2InnerScroller) {
                    View receiveInnerView;
                    receiveInnerView = getCurrentInnerView4ReceivingTouch();
                    if(receiveInnerView != null) {
                        /**
                         * Wanna intercept? dispatch a down first.
                         */
                        if (mHasEventDown) {
                            MagicHeaderUtils.cancelTouchEvent(mHeader);
                            boolean result = MagicHeaderUtils.copyAndDispatchTouchEvent(receiveInnerView, event, MotionEvent.ACTION_DOWN);
                            mHasEventDown = false;
                            mProcessTouchEventMove = false;
                            return result;
                        }

                        boolean result = receiveInnerView.dispatchTouchEvent(event);
                        mProcessTouchEventMove = false;
                        return result;
                    }
                }
                mProcessTouchEventMove = false;
                try {
                    handled = super.dispatchTouchEvent(event);
                } catch (Exception iae) {
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!mProcessTouchEvent) {
                    return false;
                }
                if(intercept2InnerScroller) {
                    final View receiveInnerView = getCurrentInnerView4ReceivingTouch();
                    if(receiveInnerView != null) {
                        MagicHeaderUtils.copyAndDispatchTouchEvent(receiveInnerView, event, MotionEvent.ACTION_UP);
                    }
                }
                resetTempMembers();
                mProcessTouchEvent = false;
                try {
                    handled = super.dispatchTouchEvent(event);
                } catch (Exception iae) {
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                resetTempMembers();
                mProcessTouchEvent = false;
                try {
                    handled = super.dispatchTouchEvent(event);
                } catch (Exception iae) {
                }
                break;
            default:
                if(isForbiddenMultiTouch() && needSingleTouchCompat()) {
                    /* If need forbidden multi-Touch and also need compat, just discard multitouch events. */
                } else {
                    try {
                        handled = super.dispatchTouchEvent(event);
                    } catch (Exception iae) {
                    }
                }
                break;
        }
        return handled;
    }

    private static boolean radiusLargerThan(float x, float y, float touchClickThreshold) {
        return ((x * x+ y * y) > touchClickThreshold * touchClickThreshold);
    }

    private void resetTempMembers() {
        InnerScroller currentScroller = getCurrentInnerScroller();
        if(currentScroller!=null && currentScroller.isScrolling()) {
            /**
             * Extend it's lifecycle to scrollStateChanged()
             */
        } else {
            if(mPointDownOnHeader) {
                mPointDownOnHeader = false;
            }
        }
        intercept2InnerScroller = false;
        downX = -9999;
    }

    /**
     * Extracted method for reuse.
     * @return
     */
    private View getCurrentInnerView4ReceivingTouch() {
        View receiveInnerView;
        InnerScroller scroller = getCurrentInnerScroller();
        if(scroller != null) {
            receiveInnerView = scroller.getReceiveView();
            if(receiveInnerView != null) {
                return receiveInnerView;
            } else if(scroller instanceof View) {
                return (View)scroller;
            }
        }
        return null;
    }

    public boolean setTabOnPageChangeListener(final ViewPager.OnPageChangeListener onPageChangeListener) {
        if(mPagerSlidingTabStrip == null || onPageChangeListener == null) {
            Log.e(TAG,  "ERROR: parameter error.");
            return false;
        }
        mPagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                onPageChangeListener.onPageScrolled(i, v, i1);
            }

            @Override
            public void onPageSelected(int i) {
                MagicHeaderViewPager.this.onPageSelected(i);
                onPageChangeListener.onPageSelected(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                onPageChangeListener.onPageScrollStateChanged(i);
            }
        });
        return true;
    }

    /**
     * Synchronize position on innerScroller stop besides touch down. Make full use of performance at leisure time.
     */
    @Override
    public void onInnerScrollerStop() {
        /** This parameter's lifecycle is extended from "touchDown - touchUp"
             to "touchDown - InnerScrollStop"  */
        if(mPointDownOnHeader) {
            /**
             *  On InnerScroller stop scrolling, refresh both ScrollYIn and HeaderStart again.
             */
            if(mHeaderAlwaysScrollWithInner) {
                /**
                 *    If switch mHeaderAlwaysScrollWithInner on, every time touch on Header,
                 *    renew scrollYIn and HeaderStart
                 */
                updateScrollYInAndHeaderStart();
            }
            mPointDownOnHeader = false;
            mHeaderIntercept = false;
        }
        syncPagesPosition(mViewPager.getCurrentItem());
    }

    private final void updateScrollYInAndHeaderStart() {
        InnerScroller currentScroller = getCurrentInnerScroller();
        if(currentScroller != null
                // v3.2.3.2: add this judgement to fix bug that:
                // in header-always-scroll-with-inner mode, switching tabs in some special sequence,
                // may causes innerScroller using another one's scrollYIn.
                && currentItem == mViewPager.getCurrentItem()
                ) {
            int scrollY = currentScroller.getInnerScrollY();
            if(scrollY != -1) {
                mScrollYIn = currentScroller.getInnerScrollY();
                mHeaderStart = mHeaderHeight - getHeaderVisibleHeight();
            }
        }
    }

    /********************************* Inner Listener Interface ******************************/
    public interface OnHeaderScrollListener {
        void onHeaderScroll(int headerTransitionY);
    }

    public void setOnHeaderScrollListener(OnHeaderScrollListener onHeaderScrollListener) {
        mOnHeaderScrollListener = onHeaderScrollListener;
    }

    /*********** Section of manually add custom stable height  ****************/
//    private int mCustomStableAreaHeight;
    public void addCustomStableAreaHeight(int height) {
//        mCustomStableAreaHeight += height;
        mMaxHeaderTransition -= height;
    }

    /**
     * Get content visible area max height, equals to (view Height - stable area height)
     */
    @Override
    public int getContentAreaMaxVisibleHeight() {
        return getMeasuredHeight() - (mHeaderHeight - mMaxHeaderTransition);
    }

    /*****  Whether header always follows if it's touched **********/
    public void setHeaderalwaysScrollWithInner(boolean headeralwaysScrollWithInner) {
        this.mHeaderAlwaysScrollWithInner = headeralwaysScrollWithInner;
    }

    private boolean mHeaderAlwaysScrollWithInner = true;

    public boolean isHeaderalwaysScrollWithInner() {
        return mHeaderAlwaysScrollWithInner;
    }

    /****************************  Save and restore *****************************/
    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.selectedIndex = currentItem;
        ss.tempScrollY = mTempScrollY;
        return ss;
    }

    static class SavedState extends BaseSavedState {
        int selectedIndex;
        float tempScrollY;

        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            selectedIndex = in.readInt();
            tempScrollY = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(selectedIndex);
            out.writeFloat(tempScrollY);
        }

        @Override
        public String toString() {
            return "mhvp.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " selectedIndex=" + selectedIndex
                    + " tempScrollY=" + tempScrollY
                     + "}";
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private boolean mBlockChangeTempScrollY;
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        currentItem = ss.selectedIndex;
        mTempScrollY = ss.tempScrollY;
        mBlockChangeTempScrollY = true;
    }

    /*********           PullToRefresh interaction           ********************/
    private OnReFreshListener mOnReFreshListener;

    public interface OnReFreshListener {
        void onRefresh();
        void onRefreshComplete();
    }

    public OnReFreshListener getOnReFreshListener() {
        return mOnReFreshListener;
    }

    public void setOnReFreshListener(OnReFreshListener onReFreshListener) {
        mOnReFreshListener = onReFreshListener;
    }

    /****    (not frequnetly used) Manually set if Header will be taller than screen    ****/
    private boolean mHeaderTallerThanScreen = true;

    /**
     * If header is never taller than screen (be careful of small screen), you man set this value
     *   false. This may fix some potential measure problems (currently none) in UnSpecified
     *   mode of some WONDERFUL FLOWER views.
     * @param headerTallerThanScreen
     */
    public void setHeaderTallerThanScreen(boolean headerTallerThanScreen) {
        this.mHeaderTallerThanScreen = headerTallerThanScreen;
    }

    public boolean isHeaderTallerThanScreen() {
        return mHeaderTallerThanScreen;
    }

    /**************** intercept Touch when refreshing ************/
    public void updateRefreshState(boolean isRefreshing) {
        mRefreshState = isRefreshing;
        if(mOnReFreshListener != null) {
            if(isRefreshing) {
                mOnReFreshListener.onRefresh();
            } else {
                mOnReFreshListener.onRefreshComplete();
            }
        }
    }

    /**
     *** （To forbidden multitouch cuz have problem integrated with ***
     *** pull to refresh, if you don't need ptr or use other ptr ***
     ***               framework, you can open it.）              ***
     ** --------------------------------------------------------- **
     **  News: But now PullToRefresh has been fixed, so default to not to forbidden. **
     ***     But if you wanna forbidden multitouch for some reason,      ***
     ***               you can still call me.            ***/
    public void setForbiddenMultiTouch(boolean forbiddenMultiTouch) {
        mForbiddenMultiTouch = forbiddenMultiTouch;
    }

    private boolean mForbiddenMultiTouch = false;

    public boolean isForbiddenMultiTouch() {
        return mForbiddenMultiTouch;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void checkForbiddenMultiTouch() {
        if(isForbiddenMultiTouch()) {
            if(needSingleTouchCompat()) {
            } else {
                setMotionEventSplittingEnabled(false);
            }
        }
    }

    /***************      single touch compat      ****************/
    public static final boolean needSingleTouchCompat() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
    }

    /***    Control whether ViewPager horizontally scrollable    ***
     *****                   defaults to "true"                *****/
    public boolean enableViewPagerHorizontalScroll(boolean enable) {
        if(mViewPager != null) {
            mViewPager.setScrollable(enable);
            return true;
        }
        return false;
    }

    /****************** unused interface ********************/
    /**
     * {@hide}
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {
        // nothing
    }

    /**
     * {@hide}
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // nothing
    }
}