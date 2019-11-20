package com.slavaguk2000.sog_client;

import android.graphics.Bitmap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Transceiver extends Thread {
    private static int port = 8536;
    private static final int maxRetryCount = 10;
    private int retryCount = 0;

    private final CoreModel core;
    private Socket mainSocket;
    private String ipAddress;
    public Transceiver(String ipAddress, CoreModel core){
        super();
        this.ipAddress = ipAddress;
        this.core = core;
        start();
    }
    private void connect() throws IOException {
        mainSocket = new Socket(ipAddress, port);
    }
    public void run(){
        try{
            connect();
            core.createDemonstrator();
            supportConnection();
        } catch (Exception e) {
            core.disconnect();
        }
    }

    private void supportConnection() {
        try {
            networkCommunication();
        } catch (IOException e) {
            retry();
        }
    }

    private void networkCommunication() throws IOException {
        DataInputStream reader = new DataInputStream(mainSocket.getInputStream());
        DataOutputStream writer = new DataOutputStream(mainSocket.getOutputStream());
        int width = 0, height = 0;
        int size = width * height, tmp;
        int color;
        while (width != -1) {
            width = reader.readInt();
            if (width > 0) {
                height = reader.readInt();
                int oldSize = size;
                size = height*width;
                int[] input = new int[size+3/4];
                for (int i = 0; i < size; i+=4)
                    input[i/4] = reader.readUnsignedByte();

                    int arraySize = size;
                    if (size%4 != 0) arraySize+=4;
                    int[] colors = new int[arraySize];
                for (int i = 0; i < size; i+=4) {
                    color = input[i/4];
                    for (int j = 3; j >= 0; j--) {
                        tmp = (color % 4)*85;
                        colors[i + j] = tmp*16843009;
                        color /= 4;
                    }
                }
                setImage(colors, width, height);
            }
            else core.setText(" ","");
        }
    }

    private void retry() {
        while (retryCount++ < maxRetryCount){
            try {
                sleep(1000*retryCount);
            } catch (InterruptedException ex) {
                try {
                    connect();
                    retryCount = 0;
                    supportConnection();
                } catch (IOException exc) {}
            }
        }
        core.createToastFromResourceString(R.string.conectionLost);
    }

    public void closeSocket()
    {
        try {
            mainSocket.close();
        }catch(Exception ex){}
    }
    public void setImage(int[] colors, final int width, final int height)
    {
        final Bitmap btm = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
        core.setImage(btm);
    }

}
