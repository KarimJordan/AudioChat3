package com.audio.sample.audiochat3.Connection;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.audio.sample.audiochat3.Interface.ConnectionListener;
import com.audio.sample.audiochat3.Sockets.Server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Decoder on 8/10/2015.
 */
public class ChatConnection {

    private Handler updateHandler;
    private Server chatServer;
    private Context context;
    private List<ConnectionListener> listener = new ArrayList<ConnectionListener>();


    public Socket socket;
    public int port = -1;
    public boolean isReady = false;

    public ChatConnection(Handler handler, Context context){
        updateHandler = handler;
        chatServer = new Server(handler);
        this.context = context;
    }

    public void tearDown(){
        chatServer.teardown();

        isReady = false;
        context = null;
    }


    public void registerListener(ConnectionListener listener){
        if(!this.listener.contains(listener)){
            this.listener.add(listener);
        }
    }

    public void unregisterListener(ConnectionListener listener){
        if(this.listener.contains(listener)){
            this.listener.remove(listener);
        }
    }

    public void onConnectionReady(){
        for(ConnectionListener listener: this.listener){
            listener.onConnectionReady();
        }
    }

    public void onConnectionDown(){
        for(ConnectionListener listener: this.listener){
            listener.onConnectionDown();
        }
    }

    public void setHandler(Handler handler){
       updateHandler = handler;
    }

    public void setLocalPort(int port){
        this.port = port;
    }

    public int getLocalPort(){
        return this.port;
    }

    public boolean isReady(){
        return isReady;
    }

    public synchronized void updateMessages(String messages, boolean local){
        if(local){
            messages = "ME: " + messages;
        }else{
            messages = "THEN: " + messages;
        }

        Bundle messageBundle = new Bundle();
        messageBundle.putString("msg", messages);

        Message message = new Message();
        message.setData(messageBundle);
        if(updateHandler != null){
            updateHandler.sendMessage(message);
        }
    }
}
