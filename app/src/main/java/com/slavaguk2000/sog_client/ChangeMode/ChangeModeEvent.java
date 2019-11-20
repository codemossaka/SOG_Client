package com.slavaguk2000.sog_client.ChangeMode;

import java.util.EventObject;

public class ChangeModeEvent extends EventObject {

    private int mode;

    public ChangeModeEvent(Object source, int mode) {
        super(source);
        this.mode = mode;
    }
    public int getMode(){
        return mode;
    }
}
