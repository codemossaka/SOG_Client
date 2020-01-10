package com.slavaguk2000.sog_client;

import android.content.Intent;
import android.graphics.Bitmap;

import com.slavaguk2000.sog_client.Events.ChangeModeEvent;
import com.slavaguk2000.sog_client.Events.ModelEventListener;

import java.util.ArrayList;

final class CoreModel {
    private static CoreModel instance;
    private DemonstrationViewModel contentDemonstrator;
    private ConnectionView parent;
    private Transceiver transceiver;
    private int mode = 0;
    private ArrayList<ModelEventListener> changeModeListeners = new ArrayList<>();

    private CoreModel(int mode) {
        this.mode = mode;
    }

    static CoreModel getInstance(int mode) {
        if (instance == null) {
            instance = new CoreModel(mode);
        }
        return instance;
    }

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
        if (transceiver != null) transceiver.sendCommand(mode==0);
            for (ModelEventListener listener : changeModeListeners) {
                listener.onModelEvent(new ChangeModeEvent(this, this.mode));
            }
    }

    void setContentDemonstrator(DemonstrationViewModel demonstrator) {
        contentDemonstrator = demonstrator;
    }

    void setImage(Bitmap image) {
        if (contentDemonstrator == null) return;
        contentDemonstrator.setImage(image);
    }

    void setText(String text, String title) {
        if (contentDemonstrator == null) return;
        contentDemonstrator.setText(text, title);
    }

    void connect(ConnectionView parent, String ipAddress) {
        if (transceiver != null) return;
        transceiver = new Transceiver(ipAddress, this, parent.getMode() == 0);
        this.parent = parent;
    }

    void disconnect() {
        transceiver.disconnect();
        setContentDemonstrator(null);
        transceiver = null;
    }

    void createDemonstrator() {
        Intent demonstrationViewIntent = new Intent(parent, DemonstrationView.class);
        parent.startActivity(demonstrationViewIntent);
    }

    void createToastFromResourceString(int id) {
        if (contentDemonstrator == null) return;
        contentDemonstrator.createToastFromResourceString(id);
    }
}
