package com.slavaguk2000.sog_client;

import android.graphics.Bitmap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Transceiver extends Thread {
    private final int port = 8536;
    private final int changeModeCommandCode = 100;
    private final int commandLength = 2;
    private final int retryCount = 10;
    private boolean work = true;
    private final CoreModel core;
    private Socket socket;
    private String ipAddress;
    private boolean isChords;
    private DataInputStream reader;
    private DataOutputStream writer;

    Transceiver(String ipAddress, CoreModel core, boolean isChords) {
        super();
        this.ipAddress = ipAddress;
        this.core = core;
        this.isChords = isChords;
        start();
    }

    private void connect() throws IOException {
        socket = new Socket(ipAddress, port);
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
            retry(retryCount);
        }
    }

    private void networkCommunication() throws IOException {
        reader = new DataInputStream(socket.getInputStream());
        writer = new DataOutputStream(socket.getOutputStream());
        sendCommandData();
        while (work) {
            if (reader.readInt() == 0) getImage();
            else getText();
        }
    }

    private String getUtfString() throws IOException {
        int length = reader.readInt();
        byte[] data = new byte[length];
        reader.read(data, 0, length);
        return new String(data, "UTF-8");
    }

    private void getText() throws IOException {
        String text = getUtfString(), title = getUtfString();
        core.setText(text, title);
    }

    private void getImage() throws IOException {
        int width = reader.readInt();
        if (width > 0) {
            int height = reader.readInt();
            int size = height * width;
            int[] input = new int[size + 3 / 4];
            for (int i = 0; i < size; i += 4)
                input[i / 4] = reader.readUnsignedByte();
            setImage(Decoder.decodeImage(height, width, input));
        } else core.setText(" ", "");
    }

    private void retry(int times) {
        core.createToastFromResourceString(R.string.connectionLost);
        try {
            connect();
            sendCommandData();
            core.createToastFromResourceString(R.string.connectionRestored);
            supportConnection();
        } catch (IOException ignored) {
            if (times > 0 ) retry(times - 1);
            else core.createToastFromResourceString(R.string.serverNotFounded);
        }
    }

    private void sendCommandData() throws IOException {
        byte[] command = new byte[commandLength];
        command[0] = changeModeCommandCode;
        command[1] = (byte) (isChords ? 1 : 0);
        writer.write(command);
    }

    void sendCommand(final boolean isChords) {
        if (this.isChords == isChords) return;
        this.isChords = isChords;
        Thread sendCommandThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendCommandData();
                } catch (IOException ignored) {
                    retry(retryCount);
                }
            }
        });
        sendCommandThread.start();

    }

    void disconnect() {
        work = false;
        try {
            socket.close();
        } catch (Exception ignored) {
        }
    }

    private void setImage(final Bitmap image) {
        core.setImage(image);
    }

}
