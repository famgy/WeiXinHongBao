package com.famgy.www.weixinhongbao;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    Button bt_replay;
    private Button bt_service_switch;

    private AccessibilityManager mAccessibilityManager;
    private static final String ServerName = "com.famgy.www.weixinhongbao/.RobService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        bt_service_switch = (Button)findViewById(R.id.bt_service_switch);
        bt_service_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        bt_replay = (Button)findViewById(R.id.bt_replay);
        bt_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (false == RobService.m_replay) {
                    RobService.m_replay = true;
                    bt_replay.setText("关闭回复");
                } else {
                    RobService.m_replay = false;
                    bt_replay.setText("启动回复");
                }
            }
        });

    }

    private boolean checkEnabledAccessibilityService() {
        List<AccessibilityServiceInfo> accessibilityServiceInfoList =
                mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServiceInfoList) {
            if (info.getId().equals(ServerName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        bt_service_switch = (Button)findViewById(R.id.bt_service_switch);
        mAccessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        if (false == checkEnabledAccessibilityService()) {
            bt_service_switch.setText("开启服务");
        } else {
            bt_service_switch.setText("关闭服务");
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
