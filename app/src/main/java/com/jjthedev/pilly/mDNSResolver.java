package com.jjthedev.pilly;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
public class mDNSResolver {
    private static final String TAG = "MdnsResolver";

    public static String resolve(Context context, String hostname) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock lock = wifi.createMulticastLock("mdnsLock");
        lock.setReferenceCounted(true);
        lock.acquire();

        try {
            InetAddress addr = InetAddress.getByName(hostname);
            String ip = addr.getHostAddress();
            Log.d(TAG, "Resolved " + hostname + " -> " + ip);
            return ip;
        } catch (Exception e) {
            Log.e(TAG, "Resolution failed", e);
        } finally {
            lock.release();
        }
        return null;
    }
}
