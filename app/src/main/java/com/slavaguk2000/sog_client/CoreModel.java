package com.slavaguk2000.sog_client;

import android.content.Intent;
import android.graphics.Bitmap;

import com.slavaguk2000.sog_client.ChangeMode.ChangeModeEvent;
import com.slavaguk2000.sog_client.ChangeMode.ChangeModeListener;

import java.util.ArrayList;

final class CoreModel {
    private static CoreModel instance;
    private DemonstrationViewModel contentDemostrator;

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
        contentDemostrator = demonstrator;
    }

    public void setImage(Bitmap image){
        contentDemostrator.setImage(image);
    }

    public void setText(String text, String title){
        contentDemostrator.setText(text, title);
    }

    public void connect(ConnectionView parent, String ipAddress){
        //Trans
        Intent demonstrationViewIntent = new Intent(parent, DemonstrationView.class);
        parent.startActivity(demonstrationViewIntent);
    }
    public void disconnect(){

        setContentDemonstrator(null);
    }
}
