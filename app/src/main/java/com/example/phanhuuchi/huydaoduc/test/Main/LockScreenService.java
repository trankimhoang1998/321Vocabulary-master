package com.example.phanhuuchi.huydaoduc.test.Main;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * Created by Chi on 12/20/2018.
 */

public class LockScreenService extends Service {

    // ta cần bắt ACTION_SCREEN_OFF ngay cả khi app k chạy mà để làm đc việc đó thì ta cần declare ở manifest
    // nhưng android không hỗ trợ cho việc regis receiver để intent ACTION_SCREEN_OFF khi đăng kí ở manifest mà chỉ hỗ trợ nhận
    // intent này khi ta regis dynamic trong code nên ta phải tạo
    // một background service, bind receiver với service và regis receiver dynamic bên trong code
    MyBroadcastReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate() {
        // đặt bộ lọc
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        // đăng kí receiver
        receiver = MyBroadcastReceiver.getInstance();
        registerReceiver(receiver, filter);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}