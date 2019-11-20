package com.slavaguk2000.sog_client;

import android.content.Intent;
import android.graphics.Bitmap;

import com.slavaguk2000.sog_client.ChangeMode.ChangeModeEvent;
import com.slavaguk2000.sog_client.ChangeMode.ChangeModeListener;

import java.util.ArrayList;

final class CoreModel {
    private static CoreModel instance;
    private DemonstrationViewModel contentDemonstrator;

    private CoreModel(int mode) {
        this.mode = mode;
    }

    static CoreModel getInstance(int mode) {
        if (instance == null) {
            instance = new CoreModel(mode);
        }
        return instance;
    }

    private int mode = 0;

    private ArrayList<ChangeModeListener> changeModeListeners = new ArrayList<>();

    void addChangeModeListener(ChangeModeListener listener) {
        changeModeListeners.add(listener);
    }

    void removeChangeModeListener(ChangeModeListener listener) {
        changeModeListeners.remove(listener);
    }

    int getMode() {
        return mode;
    }

    void setMode(int mode) {
        if (this.mode == mode) return;
        this.mode = mode;
        ///Transiver...
        for (ChangeModeListener listener : changeModeListeners) {
            listener.onChangeMode(new ChangeModeEvent(this, this.mode));
        }
    }

    void setContentDemonstrator(DemonstrationViewModel demonstrator) {
        contentDemonstrator = demonstrator;
    }

    public void setImage(Bitmap image){
        contentDemonstrator.setImage(image);
    }

    public void setText(String text, String title){
        contentDemonstrator.setText(text, title);
    }
    private Transceiver transceiver;
    public void connect(ConnectionView parent, String ipAddress){
         transceiver = new Transceiver(ipAddress, this);
        this.parent = parent;
    }
    public void disconnect(){
        transceiver.closeSocket();
        setContentDemonstrator(null);
    }
    private ConnectionView parent;
    public void createDemonstrator() {
        Intent demonstrationViewIntent = new Intent(parent, DemonstrationView.class);
        parent.startActivity(demonstrationViewIntent);
    }
}
