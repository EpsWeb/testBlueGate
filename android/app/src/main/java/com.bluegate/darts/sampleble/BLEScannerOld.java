package com.bluegate.darts.sampleble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

@SuppressWarnings("deprecation")
public class BLEScannerOld implements BLEScanner {
    private Listener mListener;
    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private static final String TAG = "BLEScannerOld";


    BLEScannerOld(Context context) {
        mContext = context;
    }


    public BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            final BluetoothManager mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager != null){
                mBluetoothAdapter = mBluetoothManager.getAdapter();
            }

        }
        return mBluetoothAdapter;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    Log.d(TAG, scanRecord.toString());
                }
            };

    public void startScan() {
        if (getBluetoothAdapter() != null && getBluetoothAdapter().isEnabled()) {
            getBluetoothAdapter().startLeScan(mLeScanCallback);
        }
    }

    public void stopScan() {
        try {
            if (getBluetoothAdapter() != null && getBluetoothAdapter().isEnabled()) {
                getBluetoothAdapter().stopLeScan(mLeScanCallback);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
