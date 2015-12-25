package com.culiu.mhvp.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.ArrayList;


/**
 * @author Xavier-S
 * @date 2015.07.21
 */
public class MagicHeaderUtils {

    public static final String TAG = "sz[MagicHeaderUtils]";

    /******************** Inner Tool Methods ***********************/
    /**
     * （as its name）
     * @param value
     * @param base
     * @return
     */
    public static int calcDelta(int value, int base) {
        return value - base;
    }

    /**
     * Get a drawable's fit height when width == Screen_Width
     * @param context
     * @param drawable
     * @return
     */
    public static int getHeightWhenFullWidth(Context context, Drawable drawable) {
        return (int) (1f * MagicHeaderUtils.getScreenWidth(context) / drawable.getIntrinsicWidth() * drawable.getIntrinsicHeight());
    }

    /**
     * （as its name）
     * @param context
     * @param drawableId
     * @return
     */
    public static int getHeightWhenFullWidth(Context context, int drawableId) {
        return getHeightWhenFullWidth(context, context.getResources().getDrawable(drawableId));
    }

    /**
     *   Three ways to shift View: margin of LayoutParams, setTransitionX/Y， View's Scroll.
     *   Note: if minSDK > 2.3, using SET_TRANSLATION is ok without nineOldAndroids dependency.
     *   if min SDK <= 2.3, suggest use LAYOUT_PARAMS or View's Scroll.）
     *
     */
    public interface TranslationMethods {
        int LAYOUT_PARAMS = 0;
        int SET_TRANSLATION = 1;
        int VIEW_SCROLL = 2;
    }

    /**
     * （as its name）
     * @param view
     * @param translation_Y Positive when upwards
     * @param translationMethod
     * @return true if success
     */
    @SuppressLint("NewApi")
    public static boolean setParamY(View view, int translation_Y, int translationMethod) {
        if (view == null) {
            Log.e(TAG, "ERROR: warning: your params contains null in setParamY()");
            return false;
        }
        boolean result;
        switch (translationMethod) {
            case TranslationMethods.VIEW_SCROLL:
                if(translation_Y != view.getScrollY()) {
                    view.scrollTo(0, translation_Y);
                    result = true;
                } else {
                    result = false;
                }
                break;
            case TranslationMethods.LAYOUT_PARAMS:
                translation_Y = -translation_Y;
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                if (!(lp instanceof FrameLayout.LayoutParams)) {
//                    Log.w(TAG, "Warning: view " + view + "'s parent must be FrameLayout T.T");
                    return false;
                }
                FrameLayout.LayoutParams fl_lp = (FrameLayout.LayoutParams) lp;
                if (translation_Y != fl_lp.topMargin) {
                    fl_lp.topMargin = translation_Y;
                    view.requestLayout();
                    result = true;
                } else {
                    result = false;
                }
                break;
            case TranslationMethods.SET_TRANSLATION:
                translation_Y = -translation_Y;
                view.setTranslationY(translation_Y);
                result = true;
                break;
            default:
                Log.e(TAG, "ERROR:Sorry. in setParamY, what is your TranslationMethods?");
                result = false;
        }
        return result;
    }

    /**
     * (as its name).
     * @param view
     * @param translationMethod
     * @return Y value, positive when upwards
     */
    @SuppressLint("NewApi")
    public static float getParamY(View view, int translationMethod) {
        float result = 0f;
        if(view == null) {
            return result;
        }
        switch (translationMethod) {
            case TranslationMethods.VIEW_SCROLL:
                result = view.getScrollY();
                break;
            case TranslationMethods.LAYOUT_PARAMS:
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                if (!(lp instanceof FrameLayout.LayoutParams)) {
                    Log.e(TAG, "Sorry, view " + view + "'s parent must be FrameLayout T.T");
                    return 0;
                }
                FrameLayout.LayoutParams fl_lp = (FrameLayout.LayoutParams) lp;
                result = -fl_lp.topMargin;
                break;
            case TranslationMethods.SET_TRANSLATION:
                result = view.getTranslationY();
                break;

        }
        return result;
    }

    /**
     * （as its name）
     */
    public static float clamp(float value, float min, float max) {

        if(min > max) {
            return value;
        }
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Convert dp to px
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Convert px to dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     *  (as its name)
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     *  (as its name)
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * Dispatch a CANCEL event to view, force to stop responding to any touch event.
     * @param view
     */
    public static void cancelTouchEvent(View view) {
        final long now = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(now, now,
                MotionEvent.ACTION_CANCEL, 0.0f, 0.0f, 0);
        view.dispatchTouchEvent(event);
        event.recycle();
    }

    /**
     * @param view
     * @param event_source
     * @param actionType System will reuse the event object, so actionType is also needed.
     */
    public static final boolean copyAndDispatchTouchEvent(View view, MotionEvent event_source, int actionType) {
        final long now = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(now, now,
                actionType, event_source.getX(), event_source.getY(), event_source.getMetaState());
        boolean result = view.dispatchTouchEvent(event);
        event.recycle();
        return result;
    }

    /********************* ArrayList Tool Method *************************/

    public static <T> void ensureCapacityWithEmptyObject(ArrayList<T> arrayList, int capacity, Class<T> clazz) {

        if(arrayList == null) {
            arrayList = new ArrayList<T>();
        }

        int delta = capacity - arrayList.size();

        if(delta <= 0) {
            return;
        }

        arrayList.ensureCapacity(capacity);
        try {
            for(;delta > 0; delta--) {
                arrayList.add(clazz.newInstance());
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
