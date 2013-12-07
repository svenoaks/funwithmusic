package com.afollestad.silk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Base64;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Various convenience methods.
 *
 * @author Aidan Follestad (afollestad)
 */
public class Silk {

    /**
     * Checks if the device is currently online, works for both wifi and mobile networks.
     */
    public static boolean isOnline(Context context) {
        if (context == null)
            return false;
        boolean state = false;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null)
            state = wifiNetwork.isConnectedOrConnecting();
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null)
            state = mobileNetwork.isConnectedOrConnecting();
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null)
            state = activeNetwork.isConnectedOrConnecting();
        return state;
    }

    /**
     * Checks whether or not the calling app has permission to access the internet.
     */
    public static boolean hasInternetPermission(Context context) {
        int res = context.checkCallingOrSelfPermission(Manifest.permission.INTERNET);
        return res == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Checks if there's any apps available to open a certain intent on the current device.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * Detects whether or not the device is a tablet.
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Clears out preferences and files persisted for Silk.
     */
    public static void clearPersistence(Context context) {
        context.getSharedPreferences("feed_last_update", 0).edit().clear().commit();
        context.getSharedPreferences("[silk-cache-expirations]", 0).edit().clear().commit();
        context.getSharedPreferences("[silk-cache-limiters]", 0).edit().clear().commit();
        File cacheDir = new File(Environment.getExternalStorageDirectory(), ".silk_cache");
        File[] files = cacheDir.listFiles();
        if (files != null && files.length > 0) {
            for (File fi : files) fi.delete();
        }
        cacheDir.delete();
    }

    public static Object deserializeObject(String str, Class<?> cls) {
        try {
            Kryo kryo = new Kryo();
            byte[] data = Base64.decode(str, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            Input input = new Input(bais);
            Object obj = kryo.readObject(input, cls);
            input.close();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String serializeObject(Serializable tweet, Class<?> cls) {
        try {
            Kryo kryo = new Kryo();
            kryo.register(cls);
            Output output = new Output(new ByteArrayOutputStream());
            kryo.writeObject(output, tweet);
            String str = Base64.encodeToString(output.toBytes(), Base64.DEFAULT);
            output.close();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}