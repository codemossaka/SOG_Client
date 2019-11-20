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
    private boolean work = true;
    private final CoreModel core;
    private Socket mainSocket;
    private String ipAddress;

    public Transceiver(String ipAddress, CoreModel core) {
        super();
        this.ipAddress = ipAddress;
        this.core = core;
        start();
    }

    private void connect() throws IOException {
        mainSocket = new Socket(ipAddress, port);
    }

    public void run() {
        try {
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
        getImages(reader);
    }

    private void getImages(DataInputStream reader) throws IOException {
        int height, width = 0;
        while (width != -1) {
            width = reader.readInt();
            if (width > 0) {
                height = reader.readInt();
                int size = height * width;
                int[] input = new int[size + 3 / 4];
                for (int i = 0; i < size; i += 4)
                    input[i / 4] = reader.readUnsignedByte();
                setImage(Decoder.decodeImage(height,width,input));
            } else core.setText(" ", "");
        }
    }

    private void retry() {
        core.createToastFromResourceString(R.string.connectionLost);
        while (retryCount++ < maxRetryCount && work) {
            try {
                connect();
                core.createToastFromResourceString(R.string.connectionRestored);
                retryCount = 0;
                supportConnection();
                return;
            } catch (IOException ignored) { }
        }
        core.createToastFromResourceString(R.string.serverNotFounded);
    }

    public void closeSocket() {
        work = false;
        try {
            mainSocket.close();
        } catch (Exception ex) {
        }
    }

    public void setImage(final Bitmap image) {
        core.setImage(image);
    }

}
