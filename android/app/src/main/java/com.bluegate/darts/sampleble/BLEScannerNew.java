package com.bluegate.darts.sampleble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BLEScannerNew implements BLEScanner {
    private Listener mListener;
    private BluetoothLeScanner mLeScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private Context mContext;
    private static final String TAG = "BLEScannerNew";

    BLEScannerNew(Context context) {
        mContext = context;
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            byte[] mScanRecord = Objects.requireNonNull(result.getScanRecord()).getBytes();
            Log.d(TAG, "Got new scan record!");
            if (mListener != null){mListener.matchScanResultsToDb(mContext, result.getDevice(), result.getRssi(), mScanRecord);}
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.d(TAG, sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "Error Code: " + errorCode);
        }
    };

    public BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            final BluetoothManager mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager != null){
                mBluetoothAdapter = mBluetoothManager.getAdapter();
            }
        }
        return mBluetoothAdapter;
    }

    private BluetoothLeScanner getLeScanner() {
        if (mLeScanner == null) {
            mLeScanner = getBluetoothAdapter().getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<>();
        }
        return mLeScanner;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void startScan() {
        Log.d(TAG, "starting scan");
        if (getBluetoothAdapter() != null && getBluetoothAdapter().isEnabled()) {
            try {
                getLeScanner().startScan(filters, settings, mScanCallback);
            }
            catch (IllegalStateException e) {
                e.printStackTrace();
            }

        }
    }

    public void stopScan() {
        Log.d(TAG, "stopping scan");
        try {
            if (getBluetoothAdapter() != null && getBluetoothAdapter().isEnabled()){
                getLeScanner().stopScan(mScanCallback);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
