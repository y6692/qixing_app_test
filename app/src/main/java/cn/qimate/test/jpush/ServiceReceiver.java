package cn.qimate.test.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.qimate.test.service.MyService;
import cn.qimate.test.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator1 on 2017/2/23.
 */

public class ServiceReceiver  extends BroadcastReceiver {

    private static final int MODE_PRIVATE = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("MY_SERVICE")){
             Intent Intentservice = new Intent(context, MyService.class);  // 要启动的Activity
             Intentservice.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             Intentservice.putExtra("oid",intent.getExtras().getString("oid"));
            Intentservice.putExtra("osn",intent.getExtras().getString("osn"));
             context.startService(Intentservice);
        }
    }
}
