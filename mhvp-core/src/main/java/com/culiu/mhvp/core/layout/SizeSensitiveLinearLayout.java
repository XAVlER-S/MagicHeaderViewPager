package com.culiu.mhvp.core.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * A LinearLayout, its onSizeChanged() in mSizeChangedListener will be called
 * When sizeChanged.
 * @author Xavier-S
 * @date 2015.07.23
 */
public class SizeSensitiveLinearLayout extends LinearLayout {

    public static final String TAG = "sz[SSLL]";

    private SizeChangedListener mSizeChangedListener;

    public SizeSensitiveLinearLayout(Context context) {
        super(context);
    }

    public SizeSensitiveLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mSizeChangedListener != null) {
            mSizeChangedListener.onSizeChanged(w, h, oldw, oldh);
        }
    }

    public void setOnSizeChangedListener(SizeChangedListener sizeChangedListener) {
        mSizeChangedListener = sizeChangedListener;
    }

    public interface SizeChangedListener {
        void onSizeChanged(int w, int h, int oldw, int oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.UNSPECIFIED;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
