package com.culiu.mhvp.core;

/**
 * Interface of MagicHeaderViewPager's Adapter.
 * Use a FragmentPagerAdapter class to implement like this:
 *
 * <p/>
 * ```<br>
 * public class DemoPagerAdapter extends FragmentPagerAdapter implements OuterPagerAdapter {<br>
 *     <blockquote>
 *     <br>
 *     ...<br>
 *     <br>
 *      private OuterScroller mOuterScroller;<br>
 *<br>
 *      public void setOuterScroller(OuterScroller outerScroller) {<br>
 *      <blockquote>
 *          mOuterScroller = outerScroller;<br>
 *      </blockquote>
 *      }<br>
 *<br>
 *      public Object instantiateItem(ViewGroup container, int position) {<br>
 *          <blockquote>
 *          // TODO: Make sure to put codes below in your PagerAdapter's instantiateItem()<br>
 *          // cuz Fragment has some weird life cycle.<br>
 *          InnerScrollerContainer fragment =<br>
 *              <blockquote>
*               (InnerScrollerContainer) super.instantiateItem(container, position);<br>
 *              </blockquote>
 * <br>
 *          if (null != mOuterScroller) {<br>
 *              <blockquote>
 *              fragment.setOuterScroller(mOuterScroller, position);<br>
 *              </blockquote>
 *          }<br>
 *          return fragment;<br>
 *          </blockquote>
 *      }<br>
 *
 *      <br>
 *     ...<br>
 *     <br>
 *     </blockquote>
 * }<br>
 * ```
 * @date 2015.07.23
 */
public interface OuterPagerAdapter {

    void setOuterScroller(OuterScroller outerScroller);

}
