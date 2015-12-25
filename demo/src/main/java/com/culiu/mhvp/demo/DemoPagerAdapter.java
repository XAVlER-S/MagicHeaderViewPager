package com.culiu.mhvp.demo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.culiu.mhvp.core.InnerScrollerContainer;
import com.culiu.mhvp.core.OuterPagerAdapter;
import com.culiu.mhvp.core.OuterScroller;

/**
 * @author Xavier-S
 * @date 2015.10.08 20:33
 */
public class DemoPagerAdapter extends FragmentPagerAdapter implements OuterPagerAdapter{


    /****    OuterPagerAdapter methods   ****/
    private OuterScroller mOuterScroller;

    @Override
    public void setOuterScroller(OuterScroller outerScroller) {
        mOuterScroller = outerScroller;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO: Make sure to put codes below in your PagerAdapter's instantiateItem()
        // cuz Fragment has some weird life cycle.
        InnerScrollerContainer fragment =
                (InnerScrollerContainer) super.instantiateItem(container, position);

        if (null != mOuterScroller) {
            fragment.setOuterScroller(mOuterScroller, position);
        }
        return fragment;
    }
    /****  OuterPagerAdapter methods End   ****/


    /************************ Test data *********************/
    public DemoPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    protected CharSequence[] mTitles = {"page 1", "page 2", "page 3", "page 4"/*, "page 5", "page 6"*/};

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public final Fragment getItem(int position) {
        Fragment fragment;

        switch (WelcomeActivity.demoType) {
            case Only_ListView:
                fragment = new DemoListFragment();
                break;
            case Only_GridView:
                fragment = new DemoGridFragment();
                break;
            case Only_Scrollview:
                fragment = new DemoScrollFragment();
                break;
            case Not_Pullable_Mixed:
                fragment = getMixedFragment(position);
                break;
            case Pull_to_add_Inner_Header_Mixed:
            case Pull_to_add_Magic_Header_Mixed:
            case Pull_to_add_Magic_Header_Mixed_Complicated_header:
                fragment = getMixedPullableFragment(position);
                break;
            default:
                fragment = new DemoGridFragment();
                break;
        }
        return fragment;
    }

    private Fragment getMixedFragment(int position) {
        switch (position % 3) {
            case 0:
                return new DemoListFragment();
            case 1:
                return new DemoGridFragment();
            case 2:
                return new DemoScrollFragment();
            default:
                return new DemoGridFragment();
        }
    }

    private Fragment getMixedPullableFragment(int position) {
        switch (position % 3) {
            case 0:
                return new DemoPullToRefreshListFragment();
            case 1:
                return new DemoPullToRefreshScrollFragment();
            case 2:
                return new DemoPullToRefreshGridFragment();
            default:
                return new DemoPullToRefreshGridFragment();
        }
    }
}
