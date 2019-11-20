package com.slavaguk2000.sog_client;

import android.graphics.Bitmap;
import android.os.Looper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Handler;

public class Transceiver extends Thread {
    private static int port = 8536;
    private static int tryCount = 10;

    private final CoreModel core;
    private Socket mainSocket;
    private String ipAddress;
    public Transceiver(String ipAddress, CoreModel core){
        super();
        this.ipAddress = ipAddress;
        this.core = core;
        start();
    }
    public void run(){
        try{
            mainSocket = new Socket(ipAddress, port);
            core.createDemonstrator();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < tryCount; i++)
        {
            networkCommunicate();
        }
    }

    private void networkCommunicate() {
        try {
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
                    int[] input = new int[size/4+1];
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
                else core.setText("","");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeSocket()
    {
        try {
            mainSocket.close();
        }catch(IOException ex){}
    }
    public void setImage(int[] colors, final int width, final int height)
    {
        final Bitmap btm = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
        core.setImage(btm);
    }

}
