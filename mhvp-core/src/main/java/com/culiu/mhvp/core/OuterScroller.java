package com.culiu.mhvp.core;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author Xavier-S
 * @date 2015.07.20
 */
public interface OuterScroller extends ViewPager.OnPageChangeListener {

    /**
     * Callback form current innerScroller scroll.
     * @param pageIndex
     * @param scrollY InnerScroller's scrollY
     * {@hide}
     */
    void onInnerScroll(int pageIndex, int scrollY);

    /**
     * （as its name）
     * {@hide}
     */
    void onPageSelected(int position);

    /**
     * Get overall height of header
     * @return
     */
    int getHeaderHeight();

    /**
     * Get magic header's visible height.
     * @return
     */
    int getHeaderVisibleHeight();

    /**
     * Synchronize Header scroll position. It can be enormously called. With many verification
     * inside, innerScrollers won't perform scroll unless necessary.
     *
     * @param currentIndex Current index，also is the excluded index in synchronization.
     * {@hide}
     */
    void syncPagesPosition(int currentIndex);


    /**
     * （as its name）
     * @return
     */
    InnerScroller getCurrentInnerScroller();

    /**
     * （as its name）
     * @return
     */
    int getCurrentInnerScrollerIndex();

    /**
     * Add custom HeaderView
     * @param view
     */
    void addHeaderView(View view);

    /**
     * （as its name）
     * {@hide}
     */
    void adjustChildrenEmptyHeaderHeight();

    /**
     * Callback on current InnerScroller stop scrolling.
     * {@hide}
     */
    void onInnerScrollerStop();

    /**
     * @param index
     * @param innerScroller
     * {@hide}
     */
    void registerInnerScroller(int index, InnerScroller innerScroller);


    /************************ 内外刷新联动 **************************/

    /**
     * Callback of InnerScroller's PullToRefresh
     * @param scrollY
     */
    void onInnerPullToRefreshScroll(int scrollY);

    /**
     * Get content visible area max height, equals to (view Height - stable area height)
     * @return
     */
    int getContentAreaMaxVisibleHeight();

    /**
     * Update state of refresh.
     * @param isRefreshing the coming state
     * {@hide}
     */
    void updateRefreshState(boolean isRefreshing);

    /******************* unused methods from OnPageChangeListener ********************/
    /**
     * No need to do anything here
     * @param state
     * {@hide}
     */
    @Deprecated
    void onPageScrollStateChanged(int state);

    /**
     * No need to do anything here
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     * {@hide}
     */
    @Deprecated
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

}