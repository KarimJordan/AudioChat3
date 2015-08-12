package com.audio.sample.audiochat3.Sockets;

import android.os.Handler;

import java.io.IOException;
import java.net.ServerSocket;


/**
 * Created by Decoder on 8/10/2015.
 */
public class Server {
    ServerSocket serverSocket = null;
    Thread thread = null;

    public Server(Handler handler){
        thread = new Thread(new ServerThread(serverSocket));
        thread.start();
    }

    public void teardown(){
        thread.interrupt();
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
