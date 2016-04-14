
package com.github.monkey.runner.scheduler;

import com.android.ddmlib.IDevice;

import java.util.HashMap;
import java.util.Map;

public class MonkeyTestDeviceFactory {
    //以serialNum作为设备索引
    public static Map<String, MonkeyTestDevice> mBuffer = new HashMap<String, MonkeyTestDevice>();

    public static MonkeyTestDevice newDevice(IDevice device) {
        if (!mBuffer.containsKey(device.getSerialNumber())) {
            mBuffer.put(device.getSerialNumber(), new MonkeyTestDevice(device));
        }
        return mBuffer.get(device.getSerialNumber());
    }
}
