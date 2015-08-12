package com.audio.sample.audiochat3.Util;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by Decoder on 8/10/2015.
 */
public class WifiStatusUtil {

    private static final String LOGTAG = "Wifi Util";

    public static String getDeviceStatus(int deviceStatus){
        switch (deviceStatus){
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }

}
