package com.audio.sample.audiochat3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.audio.sample.audiochat3.Fragments.DeviceList;
import com.audio.sample.audiochat3.Interface.DeviceActionListener;
import com.audio.sample.audiochat3.Receiver.WifiBroadCastReceiver;

import java.io.IOException;


public class MainActivity extends Activity implements WifiP2pManager.ChannelListener, DeviceActionListener{

    private static final String LOGTAG = "MAIN ACTIVITY";
    public static final int SERVER_PORT = 10086;
    private WifiP2pManager manager;
    private boolean isWifiP2PEnabled = false;
    private boolean retryChannel;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;

    public void setWifiP2PEnabled(boolean isWifiP2PEnabled) {
        this.isWifiP2PEnabled = isWifiP2PEnabled;
    }

    private void init(){
        manager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();


    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WifiBroadCastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void resetData(){
        DeviceList fragmentList = (DeviceList) getFragmentManager().findFragmentById(R.id.device_frag_list);
        if(fragmentList != null){
            fragmentList.clearPeers();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.menu_discover){
            if(!isWifiP2PEnabled){
                return true;
            }

            final DeviceList fragment = (DeviceList) getFragmentManager().findFragmentById(R.id.device_frag_list);
            fragment.onInitiateDiscovery();
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.v(LOGTAG, "Discovery Initiated");
                }

                @Override
                public void onFailure(int reason) {
                    Log.v(LOGTAG, "Discivery Failed");
                }
            });
            return true;
        }else if(id == R.id.gotoSettings){
            gotoSettings();
            return true;
        }else
        return super.onOptionsItemSelected(item);
    }

    private void gotoSettings(){
        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
    }

    @Override
    public void onChannelDisconnected() {
        if(manager != null && !retryChannel){
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), null);
        }else{

        }
    }

    @Override
    public void showDetails(WifiP2pDevice device) {

    }

    @Override
    public void channelDisconnect() {

    }

    @Override
    public void connect(WifiP2pConfig config) {

    }

    @Override
    public void disconnect() {

    }


}
