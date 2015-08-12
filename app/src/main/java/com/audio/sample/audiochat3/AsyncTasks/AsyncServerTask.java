package com.audio.sample.audiochat3.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Decoder on 8/10/2015.
 */
public class AsyncServerTask extends AsyncTask<Void, Void, String>{

    private Context context;
    private TextView statusText;

    private static final int SERVER_SOCKET_PORT = 8988;
    private static final String LOGTAG = "AsyncTask";


    public AsyncServerTask(Context context, TextView statusText) {
        this.context = context;
        this.statusText = (TextView) statusText;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_SOCKET_PORT);
            Log.v(LOGTAG, "Server Socket opened");
            Socket client = serverSocket.accept();
            InputStream inputStream = client.getInputStream();
            BufferedInputStream buffer = new BufferedInputStream(inputStream);
            //needed to add function for message sending
            serverSocket.close();

        }catch(IOException e) {
            e.printStackTrace();

        }
        return null;
    }
}
