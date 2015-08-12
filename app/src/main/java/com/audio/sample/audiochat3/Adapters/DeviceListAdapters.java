package com.audio.sample.audiochat3.Adapters;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.audio.sample.audiochat3.R;
import com.audio.sample.audiochat3.Util.WifiStatusUtil;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Decoder on 8/9/2015.
 */
public class DeviceListAdapters extends ArrayAdapter<WifiP2pDevice> {

    private List<WifiP2pDevice> items;
    private WifiStatusUtil util = new WifiStatusUtil();


    public DeviceListAdapters(Context context, int resource, List<WifiP2pDevice> items) {
        super(context, resource, items);
        this.items = items;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.device_row, null);
        WifiP2pDevice device = items.get(position);

        if(device != null){
            TextView deviceList = (TextView) v.findViewById(R.id.device_name);
            TextView deviceAddress = (TextView) v.findViewById(R.id.device_address);
            TextView deviceStatus = (TextView) v.findViewById(R.id.device_status);
            if(deviceList != null){
                deviceList.setText(device.deviceName);
            }
            if(deviceAddress != null){
                deviceAddress.setText(device.deviceAddress);
            }
            if(deviceStatus != null){
                deviceStatus.setText(util.getDeviceStatus(device.status));
            }

        }

        return v;
    }
}
