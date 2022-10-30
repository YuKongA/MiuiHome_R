package com.yuk.miuiHomeR.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;

import com.yuk.miuiHomeR.BuildConfig;

import java.io.File;

public class Helpers {

    public static final String mAppModulePkg = BuildConfig.APPLICATION_ID;

    public static synchronized Context getProtectedContext(Context context) {
        return getProtectedContext(context, null);
    }

    public static synchronized Context getProtectedContext(Context context, Configuration config) {
        try {
            Context mContext = context.isDeviceProtectedStorage() ? context : context.createDeviceProtectedStorageContext();
            return config == null ? mContext : mContext.createConfigurationContext(config);
        } catch (Throwable t) {
            return context;
        }
    }

    @SuppressLint({"SetWorldReadable", "SetWorldWritable"})
    public static void fixPermissionsAsync(Context context) {
        AsyncTask.execute(() -> {
            try {
                Thread.sleep(500);
            } catch (Throwable ignore) {
            }
            File pkgFolder = context.getDataDir();
            if (pkgFolder.exists()) {
                pkgFolder.setExecutable(true, false);
                pkgFolder.setReadable(true, false);
                pkgFolder.setWritable(true, false);
            }
            File sharedPrefsFolder = new File(PrefsUtils.getSharedPrefsPath());
            if (sharedPrefsFolder.exists()) {
                sharedPrefsFolder.setExecutable(true, false);
                sharedPrefsFolder.setReadable(true, false);
                sharedPrefsFolder.setWritable(true, false);
            }
            File sharedPrefsFile = new File(PrefsUtils.getSharedPrefsFile());
            if (sharedPrefsFile.exists()) {
                sharedPrefsFile.setReadable(true, false);
                sharedPrefsFile.setExecutable(true, false);
                sharedPrefsFile.setWritable(true, false);
            }
        });
    }
}
