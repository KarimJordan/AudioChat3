package com.audio.sample.audiochat3.Connection;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.audio.sample.audiochat3.Interface.ConnectionListener;
import com.audio.sample.audiochat3.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Decoder on 8/11/2015.
 */
public class ConnectionChat {

    private Handler mUpdateHandler;
    private ChatServer mChatServer;
    private ChatClient mChatClient;
    private Context mContext;
    private List<ConnectionListener> mlistener = new ArrayList<ConnectionListener>();

    private static final String LOGTAG = "ConnectionChat";

    private Socket mSocket;
    private int mPort = -1;
    private boolean isReady = false;

    public ConnectionChat(Handler handler, Context context){
        mUpdateHandler = handler;
        mChatServer = new ChatServer(handler);
        mContext = context;
    }

    public void tearDown(){
        mChatServer.tearDown();
        mChatClient.tearDown();
        isReady = false;
        mContext = null;
    }

    public void registerListener(ConnectionListener listener){
        if(!mlistener.contains(listener)){
            mlistener.add(listener);
        }
    }

    public void unregisterListener(ConnectionListener listener){
        if(mlistener.contains(listener)){
            mlistener.remove(listener);
        }
    }

    private void onConnectionReady(){
        for(ConnectionListener listener: mlistener){
            listener.onConnectionReady();
        }
    }

    private void onConnectionDown(){
        for(ConnectionListener listener: mlistener){
            listener.onConnectionDown();
        }
    }

    public void setHandler(Handler handler){
        mUpdateHandler = handler;
    }

    public void connecToServer(InetAddress address, int port){
        mChatClient = new ChatClient(address, port);
    }

    public void sendMessage(String msg){
        if(mChatClient != null){
            mChatClient.sendMessage(msg);
        }
    }

    public int getLocalPort(){
        return mPort;
    }

    public void setLocalPort(int port){
        mPort = port;
    }

    public boolean isReady(){
        return  isReady;
    }

    public synchronized void updateMessage(String msg, boolean local){
        if(local){
            msg = "ME: " + msg;
        }else{
            msg = "THEM: " + msg;
        }

        Bundle messageBundle = new Bundle();
        messageBundle.putString("msg", msg);

        Message message = new Message();
        message.setData(messageBundle);

        if(mUpdateHandler != null){
            mUpdateHandler.sendMessage(message);
        }
    }

    private synchronized void setSocket(Socket socket){
        if(socket == null){
            Log.v(LOGTAG, "Setting a null Socket...");
        }

        if(mSocket != null){
            if(mSocket.isConnected()){
                try{
                    mSocket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        mSocket = socket;
    }

    private Socket getSocket(){
        return mSocket;
    }

    private class ChatServer{
        ServerSocket mServersocket = null;
        Thread mThread = null;

        public ChatServer(Handler handler){
            mThread = new Thread(new ServerThread());
            mThread.start();
        }

        public void tearDown(){
            mThread.interrupt();
            try{
                if(mServersocket != null){
                    mServersocket.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        class ServerThread implements Runnable {
            @Override
            public void run() {
                try{
                   mServersocket = new ServerSocket(MainActivity.SERVER_PORT);
                   setLocalPort(mServersocket.getLocalPort());

                    while(!Thread.currentThread().isInterrupted()){
                        Log.v(LOGTAG, "Socket Created.. Awaiting Connections");
                        setSocket(mServersocket.accept());
                        Log.v(LOGTAG, "Connected");
                        if(mChatClient == null){
                            int port = mSocket.getPort();
                            InetAddress address = mSocket.getInetAddress();
                            connecToServer(address, port);
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }


    private class ChatClient{
        private InetAddress mAddress;
        private int PORT;

        private static final String LOGTAG = "ChatClient";

        private Thread mSendThread;
        private Thread mRecThread;

        public ChatClient(InetAddress address, int port){
            Log.v(LOGTAG, "Creating Chat Client");
            this.mAddress = address;
            this.PORT = port;

            mSendThread = new Thread(new SendingThread());
            mSendThread.start();
        }

        class SendingThread implements  Runnable{

            BlockingQueue<String> mMessageQueue;
            private int QUEUE_CAPACITY = 10;

            public  SendingThread(){
                mMessageQueue = new ArrayBlockingQueue<String>(QUEUE_CAPACITY);
            }

            @Override
            public void run() {
                try{
                    if(getSocket() == null){
                        setSocket(new Socket(mAddress, PORT));
                        Log.v(LOGTAG, "Client Side Socket Initialized");
                    }else{
                        Log.v(LOGTAG, "Socket already initialized..");
                    }

                    mRecThread = new Thread(new ReceivingThread());
                    mRecThread.start();

                    onConnectionReady();
                    isReady = true;
                }catch (IOException e){
                    e.printStackTrace();
                }

                while (true){
                    try{
                        String msg = mMessageQueue.take();
                        sendMessage(msg);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }

        class ReceivingThread implements Runnable{


            @Override
            public void run() {
                BufferedReader input;
                try{
                    input = new BufferedReader(new InputStreamReader(
                            mSocket.getInputStream()
                    ));

                    while(!Thread.currentThread().isInterrupted()){
                        String messageStr = null;
                        messageStr = input.readLine();
                        if(messageStr != null){
                            Log.v(LOGTAG, "Read from the Stream");
                            updateMessage(messageStr, false);
                        }else {
                            break;
                        }
                    }
                    input.close();
                    onConnectionDown();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        public void tearDown(){
            try{
                Socket socket = getSocket();
                if(socket != null){
                    socket.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        public void sendMessage(String msg){
            try{
                Socket socket = getSocket();
                if(socket == null){
                    Log.v(LOGTAG, "Socket is null");
                }else if(socket.getOutputStream() == null){
                    Log.v(LOGTAG, "Socket output stream is null");
                }

                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(getSocket().getOutputStream())
                        ), true
                );

                out.println(msg);
                out.flush();
                updateMessage(msg, true);
            }catch (UnknownHostException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}
