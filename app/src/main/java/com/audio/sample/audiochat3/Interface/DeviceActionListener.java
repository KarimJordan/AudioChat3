package com.audio.sample.audiochat3.Interface;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by Decoder on 8/9/2015.
 */
public interface DeviceActionListener {

    void showDetails(WifiP2pDevice device);
    void channelDisconnect();
    void connect(WifiP2pConfig config);
    void disconnect();

}
