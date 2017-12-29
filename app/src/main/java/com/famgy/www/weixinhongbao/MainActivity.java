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

    public static boolean m_replay = false;
    Button bt_replay;

    private AccessibilityManager mAccessibilityManager;
    private static final String ServerName = "com.famgy.www.weixinhongbao/.RobService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        mAccessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);

        if (false == checkEnabledAccessibilityService()) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        bt_replay = (Button)findViewById(R.id.bt_replay);
        bt_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (false == m_replay) {
                    m_replay = true;
                    bt_replay.setText("关闭回复");
                } else {
                    m_replay = false;
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

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
