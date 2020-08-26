package com.bluegate.darts.sampleble;

import android.content.Context;
import android.os.Build;
import android.util.Log;

/**
 * Created by nissimpardo on 27/02/2017.
 */

public class BLEScannerFactory {
    public static BLEScanner createScanner(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d("BLEScannerFactory", "[BleScan][createScanner] Creating new scanner instance");
            return new BLEScannerNew(context);

        }
        Log.d("BLEScannerFactory", "[BleScan][createScanner] Creating old scanner instance");
        return new BLEScannerOld(context);
    }
}