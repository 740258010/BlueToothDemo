package com.longrise.pczhaojue.bluetoothdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PCzhaojue
 * @time 2018/8/14  下
 * @params
 */
public class MainActivity extends AppCompatActivity
{
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;
    private static final String TAG = "MainActivity";
    private BluetoothAdapter mBluetoothAdapter;
    //是否正在搜索
    private boolean mScanning;
    //15秒搜索时间
    private static final long SCAN_PERIOD = 15000;
    private static final long SCAN_CYCLE = 25000;
    private static final int MESSAGE_WHAT = 100;
    private List<BlueToothDevice> list = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (msg.what == 100)
            {
                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        startScan();
                    }
                }, SCAN_CYCLE);
            }
        }
    };
    private GattUpdateReceiver mGattUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1.判断设备是否支持BLE
        judgeDevice();
        //2.申请所需要的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            initPermission();
            // 初始化蓝牙适配器
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        initView();
        startScan();

        //连接设备和通信
        mGattUpdateReceiver = new GattUpdateReceiver();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        initEvent();

        //连接服务
        ServiceConnection conn = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder)
            {

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName)
            {

            }
        };
        Intent intent = new Intent();
        bindService(intent,conn,BIND_AUTO_CREATE);
    }

    public static IntentFilter makeGattUpdateIntentFilter()
    { // 注册接收的事件
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BlueToothService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BlueToothService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BlueToothService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BlueToothService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }

    private void initEvent()
    {
        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onItemClick(String name, String address, String signal)
            {
                scanLeDevice(false);
                Log.d(TAG, "name：（" + name + "）>>>" + "address：（" + address + "）<<<<<" + "signal：（" + signal + "）");
            }
        });

        mGattUpdateReceiver.setOnBlueToothConnectListen(new GattUpdateReceiver.BlueToothConnectListen()
        {
            @Override
            public void successConnect()
            {

            }

            @Override
            public void cancelConnect()
            {

            }

            @Override
            public void onDoThing()
            {

            }

            @Override
            public void receiveData(String data)
            {

            }
        });
    }

    private void initView()
    {
        //通过findViewById拿到RecyclerView实例
        mRecyclerView = findViewById(R.id.rcv);
        //设置RecyclerView管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //初始化适配器
        mAdapter = new MyAdapter(list);
        //设置添加或删除item时的动画，这里使用默认动画
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置适配器
        mRecyclerView.setAdapter(mAdapter);
    }

    private void startScan()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //扫描设备
                scanLeDevice(true);
                mHandler.sendEmptyMessage(MESSAGE_WHAT);
            }
        }).start();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scanLeDevice(final boolean enable)
    {
        if (enable)
        {//true
            //10秒后停止搜索
            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                }
            }, SCAN_PERIOD);
            mScanning = true;
            Log.d(TAG, "开始搜索");
            mBluetoothAdapter.startLeScan(mLeScanCallback); //开始搜索
        } else
        {
            //false
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord)
        {
            Log.d(TAG, "进入回掉");
            runOnUiThread(new Runnable()
            {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public void run()
                {
                    ParcelUuid[] uuids = device.getUuids();
                    BlueToothDevice blueToothDevice = new BlueToothDevice(uuids, device.getName(), device.getAddress(), device.getBondState(), device.getType(), "" + rssi);
                    if (device.getName() == null)
                    {
                        blueToothDevice.setDeviceName(BleUtil.parseAdertisedData(scanRecord));
                    }

                    //如果MAC地址不同则添加，相同则更新信号
                    if (list.size() <= 0)
                    {
                        list.add(blueToothDevice);
                        mAdapter.notifyDataSetChanged();
                    } else
                    {
                        int b = judgeDeviceExist(blueToothDevice, device, rssi);
                        if (b == -1)
                        {
                            list.add(blueToothDevice);
                            mAdapter.notifyDataSetChanged();
                        } else
                        {
                            list.get(b).setSignal("" + rssi);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

//                        initView();

                    //在这里可以把搜索到的设备保存起来
                    //device.getName();获取蓝牙设备名字
                    //device.getAddress();获取蓝牙设备mac地址
                    //这里的rssi即信号强度，即手机与设备之间的信号强度。
                }
            });
        }

        private int judgeDeviceExist(BlueToothDevice blueToothDevice, BluetoothDevice device, int rssi)
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (device.getAddress().equals(list.get(i).getDeviceMacAddr()))
                {
                    return i;
                }
            }
            return -1;
        }
    };

    private void judgeDevice()
    {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(this, "您的设备不支持蓝牙BLE，将关闭", Toast.LENGTH_SHORT).show();
            finish();
        }
        Log.d(TAG, "支持ble");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //用户允许改权限，0表示允许，-1表示拒绝 PERMISSION_GRANTED = 0， PERMISSION_DENIED = -1
                //这里进行授权被允许的处理
                //可以弹个Toast，感谢用户爸爸允许了。
                Toast.makeText(MainActivity.this, "谢谢爸爸", Toast.LENGTH_SHORT).show();
            } else
            {
                //这里进行权限被拒绝的处理，就跳转到本应用的程序管理器
                Toast.makeText(MainActivity.this, "请开启位置权限", Toast.LENGTH_SHORT).show();
                Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");

                String pkg = "com.android.settings";
                String cls = "com.android.settings.applications.InstalledAppDetails";

                i.setComponent(new ComponentName(pkg, cls));
                i.setData(Uri.parse("package:" + getPackageName()));
                startActivity(i);
            }
        } else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initPermission()
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            //判断是否需要向用户解释为何要此权限
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CONTACTS))
            {
                showMessageOKCancel("你必须允许这个权限，否则无法搜索到BLE设备", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                    }
                });
                return;
            }
            //请求权限
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener)
    {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
    
}
