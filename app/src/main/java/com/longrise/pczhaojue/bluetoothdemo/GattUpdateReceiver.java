package com.longrise.pczhaojue.bluetoothdemo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class GattUpdateReceiver extends BroadcastReceiver
{

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
           switch (msg.what){
               case 0: // 连接成功
                   if (mBluetoothConnetListen != null) {
                       mBluetoothConnetListen.successConnect();
                   }
                   break;
               case 1: // 链接中断
                   if (mBluetoothConnetListen != null) {
                       mBluetoothConnetListen.cancelConnect();
                   }
                   break;
               case 2: // 可以进行数据通信
//                   CoushionApplication.isconnect = true;
                   if (mBluetoothConnetListen != null) {
                       mBluetoothConnetListen.onDoThing();
                   }
                   break;
               case 3: // 接受到数据

                   if (mBluetoothConnetListen != null) {
                       mBluetoothConnetListen.receiveData(data);
                   }
                   break;

           }
        }
    };

    private BlueToothConnectListen mBluetoothConnetListen;
    public interface  BlueToothConnectListen{
        void successConnect();
        void cancelConnect();
        void onDoThing();
        void receiveData(String data);
    }
    public void setOnBlueToothConnectListen(BlueToothConnectListen listener){
        this.mBluetoothConnetListen = listener;
    }

    private String data;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final String action = intent.getAction();
        Log.d(TAG, "Action==" + action);
        if (BlueToothService.ACTION_GATT_CONNECTED.equals(action)) { //连接成功
            Log.e(TAG, "Only gatt, just wait");

            mHandler.sendEmptyMessage(0);

        } else if (BlueToothService.ACTION_GATT_DISCONNECTED.equals(action)) { //连接失败
            mHandler.sendEmptyMessage(1);

        } else if (BlueToothService.ACTION_GATT_SERVICES_DISCOVERED
                .equals(action)) //可以通信
        {
            mHandler.sendEmptyMessage(2);

        } else if (BlueToothService.ACTION_DATA_AVAILABLE.equals(action)) { //接受到数据
            Log.e(TAG, "RECV DATA");

            data = intent.getStringExtra(BlueToothService.EXTRA_DATA);
            mHandler.sendEmptyMessage(3);
        }
    }
}
