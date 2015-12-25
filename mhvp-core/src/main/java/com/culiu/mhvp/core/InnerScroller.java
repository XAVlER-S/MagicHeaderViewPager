package com.culiu.mhvp.core;

import android.view.View;


/**
 * @author Xavier-S
 * @date 2015.07.20
 */
public interface InnerScroller {

    /**
     * （as its name）
     * {@hide}
     */
    void triggerOuterScroll();

    /**
     * （as its name）
     * @param firstVisibleItem
     * {@hide}
     */
    void recordScrollPosition(int firstVisibleItem);

    /**
     * Called by OuterScroller. verification inside. And will not directly produce scrolling. It will call performScroll() to produce substantial rolling.
     * {@hide}
     */
    void syncScroll();
    /**
     * （as its name）
     * {@hide}
     */
    void adjustEmptyHeaderHeight();

    /************** Methods for use both in and out ****************/

    /**
     * {@hide}
     */
    int getInnerScrollY();


    /*********    Methods exploded for customization    *************/
    OuterScroller getOuterScroller();

    /**
     *  Everytime when you initialize and innerScroller, you must
     *  call register2Outer(), or else this innerScroller would act
     *  like ordinary Scroller(ListView/ScrollView).
     * @param mOuterScroller
     * @param mIndex
     */
    void register2Outer(OuterScroller mOuterScroller, int mIndex);

    /**
     * Get the view to receive touch event. Defaults to this.
     * @return
     */
    View getReceiveView();

    /**
     * （as its name）
     */
    void scrollToTop();

    /**
     * （as its name）
     * @return
     */
    boolean isScrolling();

    /**
     * Scroll to innerScroller's top.
     */
    void scrollToInnerTop();

    /**
     * Add inner header view
     * @param headerView
     */
    void addHeaderView(View headerView);

    /**
     * Callback to be implemented to inform OuterScroller
     * @param isRefreshing
     */
    void onRefresh(boolean isRefreshing);

    /**
     * Customize empty content view
     * @param emptyView
     */
    void setCustomEmptyView(View emptyView);

    /**
     * Customize  empty content view's height.
     * @param height
     * @param offset
     */
    void setCustomEmptyViewHeight(int height, int offset);

    /**
     * Customize color of auto completion.
     * @param color
     */
    void setContentAutoCompletionColor(int color);
}
