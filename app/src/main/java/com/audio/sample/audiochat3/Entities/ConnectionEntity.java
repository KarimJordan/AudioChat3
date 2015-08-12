package com.audio.sample.audiochat3.Entities;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Decoder on 8/10/2015.
 */
public class ConnectionEntity {

    private static final String LOGTAG = "Connection Entity";

    public Socket socket;

    public synchronized void setSocket(Socket socket){
        Log.v(LOGTAG, "set socket called");
        if(socket != null){
            if(socket.isConnected()){
                try{
                    socket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
}
