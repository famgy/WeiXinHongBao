package com.famgy.www.weixinhongbao;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by famgy on 17-12-27.
 */

public class RobService extends AccessibilityService {

    private boolean isOpenedPacket = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String className = null;
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Log.e("===RobService===", "TYPE_NOTIFICATION_STATE_CHANGED");
                handleNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.e("===RobService===", "TYPE_WINDOW_STATE_CHANGED");

                className = event.getClassName().toString();
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                    Log.e("===RobService===", "com.tencent.mm.ui.LauncherUI");
                    if (true == isOpenedPacket) {
                        if (true == MainActivity.m_replay) {
                            replayMsg();
                        }
                        isOpenedPacket = false;
                        performGlobalAction(GLOBAL_ACTION_BACK);
                    }
                    getPacket();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                    Log.e("===RobService===", "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI");
                    openPacket();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                    Log.e("===RobService===", "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI");
                    if (true == isOpenedPacket) {
                        closePacket();
                    }
                }

                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.e("===RobService===", "TYPE_WINDOW_CONTENT_CHANGED");

                if (true == CurViewIsList()) {
                    discoveryMoney();
                }

                getPacket();

                break;
            default:
                break;
        }
    }

    private void handleNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                Log.i("===RobService===", "content : " + content);

                if (content.contains("[微信红包]")) {
                    Log.i("===RobService===", "收到[微信红包]");
                    if (event.getParcelableData() != null &&
                            event.getParcelableData() instanceof Notification)
                    {
                        Notification notification = (Notification)event.getParcelableData();
                        PendingIntent pendingIntent = notification.contentIntent;
                        try {
                            Log.i("===RobService===", "打开对话窗口");
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void getPacket() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();

        AccessibilityNodeInfo node = myRecycle(rootNode);
        if (null == node) {
            return;
        }

        if (node.isClickable()) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            AccessibilityNodeInfo parent = node.getParent();
            while (parent != null) {
                if (parent.isClickable()) {
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
                parent = parent.getParent();
            }
        }
    }

    private AccessibilityNodeInfo myRecycle(AccessibilityNodeInfo node) {
        AccessibilityNodeInfo nodeTmp = null;
        if (null == node) {
            return null;
        }

        if (node.getChildCount() == 0) {
            Log.i("===RobService===", "is 0");
            if (node.getText() != null) {
                Log.i("===RobService===", node.getText().toString());
                if ("领取红包".equals(node.getText().toString()) || node.getText().toString().contains("[微信红包]")) {
                    Log.i("===RobService===", "find node, return");
                    return node;
                }
            }
            return null;
        } else {
            Log.i("===RobService===", "is not 0");
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    nodeTmp = myRecycle(node.getChild(i));
                    if (null != nodeTmp) {
                        break;
                    }
                }
            }
        }
        return nodeTmp;
    }

    private void openPacket() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c22");
            if (null != infos && !infos.isEmpty())
            {
                nodeInfo.recycle();
                for (AccessibilityNodeInfo item : infos) {
                    Log.i("===RobService===", "open money！");
                    item.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                    isOpenedPacket = true;
                }
            }
        }
    }

    private void closePacket() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ho");
            if (null != infos && !infos.isEmpty()) {
                nodeInfo.recycle();
                for (AccessibilityNodeInfo item : infos) {
                    Log.i("===RobService===", "Editor content！");
                    item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }

    private void closeDialog() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/text1");
            if (null != infos && !infos.isEmpty()) {
                nodeInfo.recycle();
                for (AccessibilityNodeInfo item : infos) {
                    Log.i("===RobService===", "Editor content！");
                    item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }

    private void replayMsg() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> infos = null;
        if (nodeInfo != null) {

            /* Fill editText */
            infos = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a_z");
            if (null != infos && !infos.isEmpty()) {
                for (AccessibilityNodeInfo item : infos) {
                    Log.i("===RobService===", "close money！");
                    item.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    ClipData clip = ClipData.newPlainText("label", "谢谢老板！");
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(clip);
                    item.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                }
            }

            /* Send msg */
            infos = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aa5");
            if (null != infos && !infos.isEmpty()) {
                for (AccessibilityNodeInfo item : infos) {
                    Log.i("===RobService===", "close money！");
                    item.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    ClipData clip = ClipData.newPlainText("label", "谢谢老板！");
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(clip);
                    item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }

            nodeInfo.recycle();
        }
    }

    private boolean CurViewIsList() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b27");
            if (null != infos && !infos.isEmpty())
            {
                nodeInfo.recycle();
                Log.i("===RobService===", "find list view");
                return true;
            }
        }

        return false;
    }

    private void discoveryMoney() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null)
        {
            return;
        }

        List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/apf");
        if (null != infos && !infos.isEmpty()) {
            for (AccessibilityNodeInfo item : infos) {
                Log.i("===RobService===", "discoveryMoney！");
                if (item.getText().toString().contains("[微信红包]"))
                {
                    if (item.isClickable()) {
                        item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    } else {
                        AccessibilityNodeInfo parent = item.getParent();
                        while (parent != null) {
                            if (parent.isClickable()) {
                                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                break;
                            }
                            parent = parent.getParent();
                        }
                    }
                }
            }
        }

        nodeInfo.recycle();

    }

    @Override
    public void onInterrupt() {

    }
}
