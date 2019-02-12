package com.barvius.diagnostic.ui;

import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarTools {
    public static void setStatusBarColor(Window window, int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }
}
