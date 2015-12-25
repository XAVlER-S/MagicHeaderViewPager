package com.culiu.mhvp.core.tabs;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;


public abstract class GridViewWithHeaderBaseAdapter extends BaseAdapter {

    public interface GridItemClickListener {

        void onGridItemClicked(View v, int position, long itemId);

    }

    private class ListItemClickListener implements OnClickListener {

        private int mPosition;

        public ListItemClickListener(int currentPos) {
            mPosition = currentPos;
        }

        @Override
        public void onClick(View v) {
            onGridItemClicked(v, mPosition);
        }
    }

    private int mNumColumns;

    protected Context mContext;

    private GridItemClickListener mGridItemClickListener;

    public GridViewWithHeaderBaseAdapter(Context context) {
        mContext = context;
        mNumColumns = 1;
    }

    public final void setOnGridClickListener(GridItemClickListener listener) {
        mGridItemClickListener = listener;
    }

    private final void onGridItemClicked(View v, int position) {
        if(mGridItemClickListener != null) {
            mGridItemClickListener.onGridItemClicked(v, position, getItemId(position));
        }
    }

    public final int getNumColumns() {
        return mNumColumns;
    }

    public final void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
        notifyDataSetChanged();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position)  {
        return false;
    }
    
    @Override
    public int getCount() {
        return (int) Math.ceil(getItemCount() * 1f / getNumColumns());
    }

    public abstract int getItemCount();

    protected abstract View getItemView(int position, View view, ViewGroup parent);

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LinearLayout layout;
        int columnWidth = 0;
        if(viewGroup != null) {
            columnWidth = viewGroup.getWidth() / mNumColumns;
        } else if(view != null) {
            columnWidth = view.getWidth() / mNumColumns;
        }
        // Make it be rows of the number of columns
        if(view == null) {
            // This is items view
            layout = createItemRow(position, viewGroup, columnWidth);
        } else {
            layout = (LinearLayout)view;
            updateItemRow(position, viewGroup, layout, columnWidth);
        }
        return layout;
    }

    private LinearLayout createItemRow(int position, ViewGroup viewGroup, int columnWidth) {
        LinearLayout layout;
        layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        // Now add the sub views to it
        View leftView = null, rightView = null;
        for(int i = 0; i < mNumColumns; i ++ ) {
            int currentPos = position * mNumColumns + i;
            // Get the new View
            View insideView;
            if(currentPos < getItemCount()) {
                insideView = getItemView(currentPos, null, viewGroup);
                insideView.setVisibility(View.VISIBLE);
                View theView = getItemView(currentPos, insideView, viewGroup);
                theView.setOnClickListener(new ListItemClickListener(currentPos));
            } else {
                insideView = getItemView(0, null, viewGroup);
                insideView.setVisibility(View.INVISIBLE);
            }
            if(i == 0) {
                leftView = insideView;
            } else {
                rightView = insideView;
            }
            
            layout.addView(insideView);
            // Set the width of this column
            LayoutParams params = insideView.getLayoutParams();
            params.width = columnWidth;
            params.height = LayoutParams.MATCH_PARENT;
            insideView.setLayoutParams(params);
        }
        setPaddingAndMargin(leftView, rightView, mNumColumns);
        return layout;
    }

    /**
     * @param leftView
     * @param rightView
     * @param culumn
     */
    protected void setPaddingAndMargin(View leftView, View rightView, int culumn) {

    }

    private void updateItemRow(int position, ViewGroup viewGroup, LinearLayout layout, int columnWidth) {
        for(int i = 0; i < mNumColumns; i ++ ) {
            int currentPos = position * mNumColumns + i;
            View insideView = layout.getChildAt(i);
            // If there are less views than objects. add a view here
            if(insideView == null) {
                insideView = new View(mContext);
                layout.addView(insideView);
            }
            // Set the width of this column
            LayoutParams params = insideView.getLayoutParams();
            params.width = columnWidth;
            insideView.setLayoutParams(params);

            if(currentPos < getItemCount()) {
                insideView.setVisibility(View.VISIBLE);
                // Populate the view
                View theView = getItemView(currentPos, insideView, viewGroup);
                if(insideView.getTag() == null) {
                    layout.removeViewAt(i);
                    layout.addView(theView, i);
                }
                theView.setOnClickListener(new ListItemClickListener(currentPos));
                if( ! theView.equals(insideView)) {
                    // DO NOT CHANGE THE VIEWS
                }
            }  else {
                insideView.setVisibility(View.INVISIBLE);
            }
        }
    }
}