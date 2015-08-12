package com.audio.sample.audiochat3.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.audio.sample.audiochat3.Adapters.DeviceListAdapters;
import com.audio.sample.audiochat3.Interface.DeviceActionListener;
import com.audio.sample.audiochat3.R;
import com.audio.sample.audiochat3.Util.WifiStatusUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Decoder on 8/9/2015.
 */
public class DeviceList extends ListFragment implements WifiP2pManager.PeerListListener{

    private static final String LOGTAG = "DeviceList";
    private WifiStatusUtil util = new WifiStatusUtil();

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog = null;
    public View mContentView = null;
    private WifiP2pDevice device;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new DeviceListAdapters(this.getActivity(), R.layout.device_row, peers));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_list, null);
        return mContentView;
    }

    public WifiP2pDevice getDevice(){
        return  device;
    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        //get current activity
        ((DeviceActionListener)getActivity()).showDetails(device);

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        ((DeviceListAdapters) getListAdapter()).notifyDataSetChanged();
        if(peers.size() == 0){
            Log.v(LOGTAG, "No Device Found");
            Toast.makeText(getActivity(), "No Device Found", Toast.LENGTH_SHORT).show();
            return;
        }
    }



    public void updateThisDevice(WifiP2pDevice device){
        this.device = device;
        TextView view = (TextView)mContentView.findViewById(R.id.self_status);
        view.setText(util.getDeviceStatus(device.status));
    }

    public void clearPeers(){
        peers.clear();
        ((DeviceListAdapters)getListAdapter()).notifyDataSetChanged();
    }

    public void onInitiateDiscovery(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }

        progressDialog = ProgressDialog.show(getActivity(), "Press Back to Cancel", "Finding Peers", true,
                true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
    }


}
