
package com.github.monkey.runner.scheduler;

import java.util.HashMap;
import java.util.Map;

public class MonkeyTestSeriesFactory {
    //adb连接的device和测试任务映射
    public static Map<MonkeyTestDevice, MonkeyTestSeries> mBuffer = new HashMap<MonkeyTestDevice, MonkeyTestSeries>();

    public static MonkeyTestSeries newSeries(MonkeyTestDevice device, String pkgName,
            String pkgVersion, String pkgFilePath,
            String rawCommand, String userName, String initFileName,
            long seriesDuration, long singleDuration) {
        mBuffer.put(device, new MonkeyTestSeries(device, pkgName, pkgVersion, pkgFilePath,
                rawCommand, userName, initFileName,
                seriesDuration,
                singleDuration));
        return mBuffer.get(device);
    }

    public static MonkeyTestSeries getSeries(MonkeyTestDevice device) {
        return mBuffer.get(device);
    }
}
