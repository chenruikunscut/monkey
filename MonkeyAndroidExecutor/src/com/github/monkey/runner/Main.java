package com.github.monkey.runner;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.monkey.runner.helper.DeviceConnectHelper;
import com.github.monkey.runner.helper.LocationHelper;
import com.github.monkey.runner.scheduler.Console;
import com.github.monkey.runner.scheduler.MonkeyTestDevice;
import com.github.monkey.runner.scheduler.MonkeyTestDeviceFactory;
import com.github.monkey.runner.scheduler.MonkeyTestSeries;
import com.github.monkey.runner.scheduler.MonkeyTestSeriesFactory;

import java.io.File;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String initFileName = "";
        String rawCommand = "monkey --throttle 300 -c android.intent.category.MONKEY -c android.intent.category.LAUNCHER -c android.intent.category.DEFAULT --monitor-native-crashes --kill-process-after-error --pct-touch 60 --pct-motion 30 --pct-majornav 5 --pct-syskeys 5 -v -v -v 100000";

        CLIParser cli = new CLIParser();
        boolean success = cli.parse(args);
        if (!success)
            return;

        //取初始化的设备号
        if (cli.deivcesId==null){
            AndroidDebugBridge.init(false);
            AndroidDebugBridge bridge = AndroidDebugBridge.createBridge(getADB(),false);
            waitDeviceList(bridge);
            IDevice device = bridge.getDevices()[0];
            System.out.println("SerialNum:"+device.getSerialNumber());
            cli.deivcesId = new String[]{device.getSerialNumber()};
        }
        if (cli.pkgName == null){
            cli.pkgName = "com.jd.wxsq.app";
        }
        if (cli.pkgPath ==null){
            try {
                cli.pkgPath = LocationHelper.getHomeLocation()+File.separator+"app-yyb-release.apk";
            } catch (LocationHelper.FileLocationException e) {
                e.printStackTrace();
            }
        }
        if (cli.user == null){
            cli.user = "chenruikun";
        }
        if (cli.singleDuration == null){
            cli.singleDuration = "1.5";
        }
        if (cli.seriesDuration == null){
            cli.seriesDuration = "2.5";
        }
//
//        DeviceConnectHelper.init(getADB());
        try {
            ArrayList<MonkeyTestSeries> serieslist = new ArrayList<MonkeyTestSeries>();
            DeviceConnectHelper deviceHelper = new DeviceConnectHelper();

            for (String serialNumber : cli.deivcesId) {
                IDevice device = deviceHelper.getConnecedDevice(serialNumber);
                if (device == null)
                    device = deviceHelper.waitForDeviceConnected(serialNumber, 20000);

                if (device == null) {
                    Console.printLogMessage(serialNumber, "device " + serialNumber + "cannot be connected");
                    continue;
                }
//            IDevice device = AndroidDebugBridge.getBridge().getDevices()[0];

                //安装指定目录下的apk
                MonkeyTestDevice monkeyTestDevice = MonkeyTestDeviceFactory
                        .newDevice(device);
//                monkeyTestDevice.install(cli.pkgPath);

                //启动一个monkey进程
                //传入这些参数是传入里面创建MonkeyTestSeries用
                MonkeyTestSeries series = MonkeyTestSeriesFactory.newSeries(
                        monkeyTestDevice,
                        cli.pkgName,
                        cli.pkgVersion,
                        cli.pkgPath,
                        rawCommand,
                        cli.user,
                        initFileName,
                        (long) (1000 * 3600 * Float.parseFloat(cli.seriesDuration)),
                        (long) (1000 * 3600 * Float.parseFloat(cli.singleDuration)));
                //开始启动monkey进程
                series.start();
                serieslist.add(series);
            }

            // Wait until the monkey series completed!
            for (MonkeyTestSeries series: serieslist)
                series.mExecutor.join();

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            AndroidDebugBridge.terminate();
        }

        System.exit(0);
    }

    private static void waitDeviceList(AndroidDebugBridge bridge) {
        int count = 0;
        while (!bridge.hasInitialDeviceList()){
            try {
                Thread.sleep(1000);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (count>10){
                System.out.println("ADB Connected Timeout");
                break;
            }
        }
    }

    static boolean isMac() {
        return getPlatform().startsWith("Mac OS");
    }

    static boolean isWindows() {
        return getPlatform().startsWith("Windows");
    }

    static boolean isLinux() {
        return getPlatform().startsWith("Linux");
    }

    private static String getPlatform() {
        return System.getProperty("os.name");
    }

    private static String getADB() {
        if (isWindows()) {
            return "." + File.separator + "adb" + File.separator + getPlatform().split(" ")[0] + File.separator + "adb.exe";
        } else if (isMac() || isLinux()){
            return new File("").getAbsolutePath() + File.separator + "adb" + File.separator + getPlatform().split(" ")[0] + File.separator + "adb";
        } else {
            throw new RuntimeException("not yet implement");
        }
    }
}
