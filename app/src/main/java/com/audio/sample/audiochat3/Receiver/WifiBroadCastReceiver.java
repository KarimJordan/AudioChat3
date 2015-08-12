package com.audio.sample.audiochat3.Receiver;

import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.DrawFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import com.audio.sample.audiochat3.Adapters.DeviceListAdapters;
import com.audio.sample.audiochat3.Fragments.DeviceList;
import com.audio.sample.audiochat3.MainActivity;
import com.audio.sample.audiochat3.R;

import java.net.InetAddress;

/**
 * Created by Decoder on 8/9/2015.
 */
public class WifiBroadCastReceiver extends BroadcastReceiver{

    private static final String LOGTAG = "BroadCast Receiver";

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;


    public WifiBroadCastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
             int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
             if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                 activity.setWifiP2PEnabled(true);
                 Toast.makeText(activity, "Wifi Device is Supported", Toast.LENGTH_LONG).show();
             }else{
                   activity.setWifiP2PEnabled(false);
                 Toast.makeText(activity, "Wifi Device not Supported", Toast.LENGTH_LONG).show();
             }
            Log.v(LOGTAG, "P2P state changed!");
        }else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(manager != null){
                //request peers
                manager.requestPeers(channel, (WifiP2pManager.PeerListListener) activity.getFragmentManager().findFragmentById(R.id.device_frag_list));
            }
            Log.v(LOGTAG, "P2P peers changed!");
        }else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            Log.v(LOGTAG, "P2P Connection Changed");
            if(manager == null){
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            Log.v(LOGTAG, String.valueOf(networkInfo.isConnected()));
            if(networkInfo.isConnected()){
                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        InetAddress groupOwnerAddress = info.groupOwnerAddress;
                        Log.v(LOGTAG, "Group Formed: " + String.valueOf(info.groupFormed));
                        Log.v(LOGTAG, "IS Group Owner: " + String.valueOf(info.isGroupOwner));
                    }
                });
            }

        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
            DeviceList fragment = (DeviceList) activity.getFragmentManager().findFragmentById(R.id.device_frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
            Log.v(LOGTAG, "P2P This Device Changed!");
        }
    }
}
