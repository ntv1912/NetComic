package com.example.netcomic.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class InternetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo connect = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = connect != null && connect.isConnectedOrConnecting();
        if (isConnected) {
            Toast.makeText(context.getApplicationContext(), "Đã kết nối Internet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context.getApplicationContext(), "Mất kết nối Internet", Toast.LENGTH_SHORT).show();
        }
    }
}
