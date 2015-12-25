package com.culiu.mhvp.demo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.culiu.mhvp.R;
import com.culiu.mhvp.core.MagicHeaderUtils;
import com.culiu.mhvp.core.tabs.GridViewWithHeaderBaseAdapter;

import java.util.ArrayList;

/**
 * Created by Xavier-S on 15/7/23.
 */
public class DemoGridFragment extends DemoListFragment {

    /**
     * Using LinearLayout in ListView items to realize
     * gridView result.
     */
    protected void initAdapter() {
        if (mListItems == null) {
            mListItems = new ArrayList<Item>();
        }
        mAdapter = new GridViewWithHeaderBaseAdapter(getActivity()) {
            @Override
            protected void setPaddingAndMargin(View leftView, View rightView, int culumn) {
                super.setPaddingAndMargin(leftView, rightView, culumn);
                int _5dp = MagicHeaderUtils.dp2px(getActivity(), 5f);
                leftView.setPadding(_5dp, 0, _5dp / 2, 0);
                rightView.setPadding(_5dp / 2, 0, _5dp, 0);
            }

            @Override
            public int getItemCount() {
                return mListItems.size();
            }

            @Override
            protected View getItemView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_grid_fragment, null);
                }
                ImageView iv = (ImageView) convertView.findViewById(R.id.iv);
                TextView tv = (TextView) convertView.findViewById(R.id.tv);
                iv.setImageResource(getItem(position).getDrawableResId());
                tv.setText(getItem(position).getTitle());
                return convertView;
            }

            @Override
            public Item getItem(int position) {
                return mListItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }
        };
        ((GridViewWithHeaderBaseAdapter) mAdapter).setNumColumns(2);
        mListView.setAdapter(mAdapter);
    }
}
