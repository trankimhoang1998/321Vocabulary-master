package com.example.phanhuuchi.huydaoduc.test.Main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.phanhuuchi.huydaoduc.test.ExamActivity.Exam_Activity;

/**
 * Created by Chi on 12/20/2018.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    private static MyBroadcastReceiver myBroadcastReceiver;

    static public MyBroadcastReceiver getInstance()
    {
        if(myBroadcastReceiver == null)
        {
            myBroadcastReceiver = new MyBroadcastReceiver();
            return myBroadcastReceiver;
        }
        else
            return myBroadcastReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // bắt Screen Off action. Nếu bắt được thì chạy Exam Activity. Để sau khi unlock thì nó hiện lên sẵn rồi
        if(action.equals(Intent.ACTION_SCREEN_OFF))
        {
            Intent i = new Intent(context, Exam_Activity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);             // thay FLAG_ACTIVITY_NEW_TASK bằng FLAG_ACTIVITY_CLEAR_TOP để tránh việc tạo lại nhiều activity
            context.startActivity(i);
        }
    }

}
