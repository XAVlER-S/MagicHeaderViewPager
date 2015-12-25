package com.culiu.mhvp.core.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.culiu.mhvp.core.MagicHeaderViewPager;

/**
 * A LinearLayout that act as it can translation in Y,
 * while actually it didn't.
 * {@hide}
 * @author Xavier-S
 * @date 2015.08.11
 */
public class TranslatableLinearLayout extends LinearLayout {

    public static final String TAG = "sz[TLL]";
    private MagicHeaderViewPager mMagicHeaderViewPager;

    public TranslatableLinearLayout(Context context) {
        super(context);
    }

    public TranslatableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TranslatableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initView();
    }

    private void initView() {
        if(getParent()!=null) {
            mMagicHeaderViewPager = (MagicHeaderViewPager)getParent();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mMagicHeaderViewPager!= null && mMagicHeaderViewPager.isHeaderTallerThanScreen()) {
            heightMeasureSpec = MeasureSpec.UNSPECIFIED;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean mProcessTouchEvent;

    /**
     * 对控件内的触摸进行分发控制，让它只处理看起来的边界内的事件
     * （control the touch event in this view, to let it
     *   just deal with event located in area where this looks）
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int visualBottom = getVisualBottom();
        switch(ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(ev.getY() < visualBottom && !mMagicHeaderViewPager.intercept2InnerScroller) {
                    mProcessTouchEvent = true;
                    return super.dispatchTouchEvent(ev);
                } else {
                    mProcessTouchEvent = false;
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                if(mProcessTouchEvent) {
                    if( !mMagicHeaderViewPager.intercept2InnerScroller) {
                        return super.dispatchTouchEvent(ev);
                    } else {
                        mProcessTouchEvent = false;
                        return false;
                    }
                } else {
                    return false;
                }
            case MotionEvent.ACTION_UP:
                if(mProcessTouchEvent && !mMagicHeaderViewPager.intercept2InnerScroller) {
                    return super.dispatchTouchEvent(ev);
                } else {
                    return false;
                }
            case MotionEvent.ACTION_CANCEL:
                if(mProcessTouchEvent) {
                    mProcessTouchEvent = false;
                }
                return super.dispatchTouchEvent(ev);
            default:
                return false;
        }
    }

    public int getVisualBottom() {
        return getBottom() - getScrollY();
    }
}
