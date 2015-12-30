**Guide: How to implement simplest MagicHeaderViewPager?**

1.Add dependencies in your build.gradle
```Java
dependencies {
    compile 'com.culiu.android:mhvp-core:2.1.2@aar'
}
```

2.Build your DemoListFragment. Create a layout fragment_list.xml like:
```Xml
<com.culiu.mhvp.core.InnerListView xmlns:android="http://schemas.android.com/apk/res/android"
	android:id="@+id/listView"
	android:layout_width="match_parent"
	android:layout_height="match_parent"
	android:scrollbars="none" />
```

3.create DemoListFragment.java: 
```Java
public class DemoListFragment extends Fragment implements InnerScrollerContainer {
	protected View viewThis;
	protected InnerListView mListView;

    @Override
    public InnerScroller getInnerScroller() {
        return mListView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mListView != null && viewThis != null) {
            if (viewThis.getParent() != null) {
                ((ViewGroup) viewThis.getParent()).removeView(viewThis);
            }
            return viewThis;
        }
        viewThis = inflater.inflate(R.layout.fragment_list, null);
        mListView = (InnerListView) viewThis.findViewById(R.id.listView);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setDividerHeight(0);

        mListView.register2Outer(mOuterScroller, mIndex);

        TODO: do something about your adapter and data

        return viewThis;
    }

    ...

}
```
You need to do something about your adapter and data.

4.Build your DemoPagerAdapter. Create a class DemoPagerAdapter, let it implement interface OutPagerAdapter and contains code below is enough for MagicHeaderViewPager.

```Java
public class DemoPagerAdapter extends FragmentPagerAdapter implements OuterPagerAdapter{

    private OuterScroller mOuterScroller;

    @Override
    public void setOuterScroller(OuterScroller outerScroller) {
        mOuterScroller = outerScroller;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        InnerScrollerContainer fragment =
                (InnerScrollerContainer) super.instantiateItem(container, position);

        if (null != mOuterScroller) {
            fragment.setOuterScroller(mOuterScroller, position);
        }
        return fragment;
    }

    protected CharSequence[] mTitles = {"page 1", "page 2", "page 3", "page 4", "page 5", "page 6"};

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
        return new DemoListFragment();
    }

    ...

}
```

5.Add follow code into your DemoActivity's onCreate(Bundle) :

```Java
	mMagicHeaderViewPager = new MagicHeaderViewPager(this) {
        @Override
        protected void initTabsArea(LinearLayout container) {
            PagerSlidingTabStrip pagerSlidingTabStrip = new PagerSlidingTabStrip(XActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    MagicHeaderUtils.dp2px(DemoActivity.this, 48));
            container.addView(pagerSlidingTabStrip, lp);
            setTabsArea(pagerSlidingTabStrip);
            setPagerSlidingTabStrip(pagerSlidingTabStrip);
        }
    };
    LinearLayout mhvpParent = (LinearLayout) findViewById(R.id.mhvp_parent);
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
    );
    mhvpParent.addView(mMagicHeaderViewPager, lp);
    mPagerAdapter = new DemoPagerAdapter(getSupportFragmentManager());
    mMagicHeaderViewPager.setPagerAdapter(mPagerAdapter);
```

"R.id.mhvp_parent" is id of the parent you want magicHeaderViewPager attach to. Usually to let the root view in your activity's layout xml like this is Ok.
```Xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/mhvp_parent"
...
```

6.Now, you can enjoy your MagicHeaderViewPager. To add a magic header:
```Java
    //TODO create your view
    View view = ...;

    // the first way
    mMagicHeaderViewPager.addHeaderView(view);

    // the second way
    // int height = 200;// in pixel
    // mMagicHeaderViewPager.addHeaderView(view, height);

    // the third way
    // LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
    // mMagicHeaderViewPager.addHeaderView(view, lp);
```

For more details, see demo code in [Module Demo](https://github.com/XavierSAndroid/MagicHeaderViewPager/tree/master/demo).
