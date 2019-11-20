package com.slavaguk2000.sog_client;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.slavaguk2000.sog_client.Events.ChangeModeEvent;
import com.slavaguk2000.sog_client.Events.ModelEventListener;

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

    private ArrayList<ModelEventListener> changeModeListeners = new ArrayList<>();

    void addChangeModeListener(ModelEventListener listener) {
        changeModeListeners.add(listener);
    }

    void removeChangeModeListener(ModelEventListener listener) {
        changeModeListeners.remove(listener);
    }

    int getMode() {
        return mode;
    }

    void setMode(int mode) {
        if (this.mode == mode) return;
        this.mode = mode;
        ///Transiver...
        for (ModelEventListener listener : changeModeListeners) {
            listener.onModelEvent(new ChangeModeEvent(this, this.mode));
        }
    }

    void setContentDemonstrator(DemonstrationViewModel demonstrator) {
        contentDemonstrator = demonstrator;
    }

    public void setImage(Bitmap image){
        if (contentDemonstrator == null) return;
        contentDemonstrator.setImage(image);
    }

    public void setText(String text, String title){
        if (contentDemonstrator == null) return;
        contentDemonstrator.setText(text, title);
    }
    private Transceiver transceiver;
    public void connect(ConnectionView parent, String ipAddress){
        if (transceiver != null) return;
         transceiver = new Transceiver(ipAddress, this);
        this.parent = parent;
    }
    public void disconnect(){
        transceiver.closeSocket();
        setContentDemonstrator(null);
        transceiver = null;
    }
    private ConnectionView parent;
    public void createDemonstrator() {
        Intent demonstrationViewIntent = new Intent(parent, DemonstrationView.class);
        parent.startActivity(demonstrationViewIntent);
    }

    public void createToast(String message) {
        if (contentDemonstrator == null) return;
        contentDemonstrator.createToast(message);
    }

    public void createToastFromResourceString(int id) {
        if (contentDemonstrator == null) return;
        contentDemonstrator.createToastFromResourceString(id);
    }
}
