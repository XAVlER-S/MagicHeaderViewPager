package com.culiu.mhvp.demo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.culiu.mhvp.R;

/**
 * Created by Xavier-S on 15/7/26.
 */
public class WelcomeActivity extends Activity {

    static DemoConfig.DemoType demoType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pure_listview:
                // Only ListView
                demoType = DemoConfig.DemoType.Only_ListView;
                break;
            case R.id.btn_pure_scrollview:
                // Only ScrollView
                demoType = DemoConfig.DemoType.Only_Scrollview;
                break;
            case R.id.btn_pure_gridview:
                // Only GridView
                demoType = DemoConfig.DemoType.Only_GridView;
                break;
            case R.id.btn_mix_innerscroller:
                // 混合，不可下拉刷新
                demoType = DemoConfig.DemoType.Not_Pullable_Mixed;
                break;
            case R.id.btn_pull_to_add_inner_header:
                // Mixed，pull to add an inner header
                demoType = DemoConfig.DemoType.Pull_to_add_Inner_Header_Mixed;
                break;
            case R.id.btn_pull_to_add_magic_header:
                // Mixed，pull to add an outer header (content in Magic Header)
                demoType = DemoConfig.DemoType.Pull_to_add_Magic_Header_Mixed;
                break;
            case R.id.btn_pull_to_add_magic_header_complicated_header:
                // Mixed，pull to add an outer header (content in Magic Header); header default to complicated layout
                demoType = DemoConfig.DemoType.Pull_to_add_Magic_Header_Mixed_Complicated_header;
                break;
            default:
                //nothing
                break;
        }
        startActivity(new Intent(this, DemoActivity.class));
    }
}
