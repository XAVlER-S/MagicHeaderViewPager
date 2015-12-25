package com.culiu.mhvp.demo;

import android.graphics.Color;

import com.culiu.mhvp.R;

/**
 * @author Xavier-S
 * @date 2015.11.19 11:55
 */
public interface DemoConfig {

    /**
     * Demo子页面对应的类型
     */
    enum DemoType {
        Only_ListView,
        Only_Scrollview, Only_GridView, Not_Pullable_Mixed,
        Pull_to_add_Inner_Header_Mixed, Pull_to_add_Magic_Header_Mixed,
        Pull_to_add_Magic_Header_Mixed_Complicated_header
    }


    /**
     * 用配色帮助理解
     */
    boolean ENABLE_COLOR = true;

    /**
     * 空白内容的颜色
     */
    int COLOR_EMPTY_CONTENT = Color.TRANSPARENT;

    /**
     * 自动补全(内容)的颜色
     */
    int COLOR_CONTENT_AUTO_COMPLETION = Color.TRANSPARENT;

}
