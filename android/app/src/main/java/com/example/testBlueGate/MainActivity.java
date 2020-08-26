package com.example.testBlueGate;

//import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

import com.bluegate.darts.sampleble.BLEScanner;
import com.bluegate.darts.sampleble.BLEScannerFactory;


public class MainActivity extends FlutterActivity {
    private static final String CHANNEL_BATTERY = "samples.flutter.dev/battery";
    private static final String CHANNEL_SCANNER = "samples.flutter.dev/scanner";
    private BLEScanner scanner;
    private boolean isScannerCreated = false;

    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL_BATTERY)
                .setMethodCallHandler(
                        (call, result) -> {
                            // Note: this method is invoked on the main thread.
                            if (call.method.equals("getBatteryLevel")) {
                                int batteryLevel = getBatteryLevel();

                                if (batteryLevel != -1) {
                                    result.success(batteryLevel);
                                } else {
                                    result.error("UNAVAILABLE", "Battery level not available.", null);
                                }
                            } else {
                                result.notImplemented();
                            }
                        }
                );
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL_SCANNER)
                .setMethodCallHandler(
                        (call, result) -> {
                            // Note: this method is invoked on the main thread.

                            if (!isScannerCreated) {
                                scanner = BLEScannerFactory.createScanner(new ContextWrapper(getApplicationContext()));
                            }
                            isScannerCreated = true;
                            if (call.method.equals("startScanning")) {
                                scanner.startScan();
                                result.success(1);
                            } else if (call.method.equals("stopScanning")) {
                                scanner.stopScan();
                                result.success(-1);
                            } else {
                                result.notImplemented();
                            }
                        }
                );
    }

    private int getBatteryLevel() {
        int batteryLevel = -1;
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            Intent intent = new ContextWrapper(getApplicationContext()).
                    registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            batteryLevel = (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) /
                    intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }
        return batteryLevel;
    }

}
