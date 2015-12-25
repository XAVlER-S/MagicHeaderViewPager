package com.culiu.mhvp.demo;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.culiu.mhvp.R;
import com.culiu.mhvp.core.MagicHeaderUtils;
import com.culiu.mhvp.core.MagicHeaderViewPager;
import com.culiu.mhvp.core.tabs.com.astuetz.PagerSlidingTabStrip;

public class DemoActivity extends FragmentActivity {

    public static final String TAG = "sz[mhvp-demo]";

    private MagicHeaderViewPager mMagicHeaderViewPager;
    private DemoPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mhvp_demo);


        /* TODO: Initialize MagicHeaderViewPager. Override initTabsArea() to initialize tabs or stable area. */
        mMagicHeaderViewPager = new MagicHeaderViewPager(this) {
            @Override
            protected void initTabsArea(LinearLayout container) {
                //You can customize your tabStrip or stable area here
                ViewGroup tabsArea = (ViewGroup) LayoutInflater.from(DemoActivity.this).inflate(R.layout.layout_tabs1, null);

                // TODO: Set height of stable area manually, then it can be calculated.
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        MagicHeaderUtils.dp2px(DemoActivity.this, 48));
                container.addView(tabsArea, lp);

                // some style
                PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) tabsArea.findViewById(R.id.tabs);
                pagerSlidingTabStrip.setTextColor(Color.BLACK);
                pagerSlidingTabStrip.setBackgroundColor(Color.WHITE);

                // TODO: These two methods must be called to let magicHeaderViewPager know who is stable area and tabs.
                setTabsArea(tabsArea);
                setPagerSlidingTabStrip(pagerSlidingTabStrip);
            }
        };
        // Note: Cuz tabs or stable area of each ViewPager may not
        // the same. So it's abstract for developers to override.

        //TODO: Just add MagicHeaderViewPager into your Layout. MATCH_PARENT-MATCH_PARENT is recommended.
        LinearLayout mhvpParent = (LinearLayout) findViewById(R.id.mhvp_parent);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        mhvpParent.addView(mMagicHeaderViewPager, lp);

        // TODO: Use an OuterPagerAdapter as FragmentPagerAdapter
        mPagerAdapter = new DemoPagerAdapter(getSupportFragmentManager());

        //  TODO: Use this method instead of those of PagerSlidingTabStrip or ViewPager.
        mMagicHeaderViewPager.setPagerAdapter(mPagerAdapter);

        // call this if needed
        mMagicHeaderViewPager.setTabOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Then you can do anything like before:)

        // add your custom Header content
        initCustomHeader();

    }


    /************************************************************
     ****                       Test code                    ****
     ************************************************************/

    private void initCustomHeader() {
        if (WelcomeActivity.demoType != DemoConfig.DemoType.Pull_to_add_Magic_Header_Mixed_Complicated_header) {
            // Simply add a picture
            addRandomPic();
        } else {
            // Example: add a custom Layout

            View customLayout = LayoutInflater.from(this).inflate(R.layout.header_custom_layout, null);
            Button btn = ((Button) customLayout.findViewById(R.id.button));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DemoActivity.this, "btn Clicked", Toast.LENGTH_SHORT).show();
                }
            });
            mMagicHeaderViewPager.addHeaderView(customLayout);

            HorizontalScrollView sv = new HorizontalScrollView(this);
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            for (int i = 5; i > 0; i--) {
                ImageView iv = new ImageView(this);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setImageResource(RandomPic.getInstance().getPicResId());
                ll.addView(iv, new LinearLayout.LayoutParams(MagicHeaderUtils.getScreenWidth(this), (int) (MagicHeaderUtils.getScreenWidth(this)*.66f)));
            }
            sv.addView(ll, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mMagicHeaderViewPager.addHeaderView(sv);
        }
    }

    /**
     * Add a random picture
     */
    public void addRandomPic() {
        ImageView iv = new ImageView(this);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        Drawable drawable = getResources().getDrawable(RandomPic.getInstance().getPicResId());
        iv.setImageDrawable(drawable);
        int height = MagicHeaderUtils.getHeightWhenFullWidth(this, drawable);
        mMagicHeaderViewPager.addHeaderView(iv, height);
    }

    /************************************************************
      ****                   Test code End                  ****
     ************************************************************/
}
