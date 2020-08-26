package com.bluegate.darts.sampleble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

public interface BLEScanner {
    void startScan();
    void stopScan();
    void setListener(Listener listener);
    BluetoothAdapter getBluetoothAdapter();

    interface Listener {
        void matchScanResultsToDb(Context context, final BluetoothDevice device, int rssi, byte[] scanRecord);
    }
}
