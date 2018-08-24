package com.longrise.pczhaojue.bluetoothdemo;

import android.os.ParcelUuid;

/**
 * @author PCzhaojue
 * @name BlueToothDemo
 * @class name：com.longrise.pczhaojue.bluetoothdemo
 * @class describe
 * @time 2018/8/14 下午2:59
 * @change
 * @chang time
 * @class describe
 */
public class BlueToothDevice
{
    private String deviceName;
    private String deviceMacAddr;
    private  int  deviceBondState;
    private  int type;


    private ParcelUuid[] uuids;
    private String signal;

    public String getSignal()
    {
        return signal;
    }

    public void setSignal(String signal)
    {
        this.signal = signal;
    }

    public BlueToothDevice(ParcelUuid[] uuids, String deviceName, String deviceMacAddr, int deviceBondState, int type, String signal)
    {
        this.deviceName = deviceName;
        this.deviceMacAddr = deviceMacAddr;
        this.deviceBondState = deviceBondState;
        this.type = type;
        this.uuids = uuids;
        this.signal = signal;
    }

    public ParcelUuid[] getUuids()
    {
        return uuids;
    }

    public void setUuids(ParcelUuid[] uuids)
    {
        this.uuids = uuids;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }

    public String getDeviceMacAddr()
    {
        return deviceMacAddr;
    }

    public void setDeviceMacAddr(String deviceMacAddr)
    {
        this.deviceMacAddr = deviceMacAddr;
    }

    public int getDeviceBondState()
    {
        return deviceBondState;
    }

    public void setDeviceBondState(int deviceBondState)
    {
        this.deviceBondState = deviceBondState;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }
}
