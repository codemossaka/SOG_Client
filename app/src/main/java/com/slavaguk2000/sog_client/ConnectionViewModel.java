package com.slavaguk2000.sog_client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.slavaguk2000.sog_client.Events.ChangeModeEvent;
import com.slavaguk2000.sog_client.Events.ModelEventListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.EventObject;

public class ConnectionViewModel implements ModelEventListener {

    private ConnectionView parent;
    private String previousAddress;
    private int previousPosition;
    private CoreModel core;
    private int mode;
    private boolean activityPaused = false;

    ConnectionViewModel(ConnectionView parent) {
        this.parent = parent;
        core = CoreModel.getInstance(parent.getMode());
        core.addChangeModeListener(this);
    }

    void spinnerTouched(View v) {
        hideKeyboard(v);
    }

    void setMode(int position) {
        mode = position;
        core.setMode(position);
    }

    void savePreviousTextFieldInstance() {
        this.previousAddress = parent.getIpAddress();
        this.previousPosition = parent.getCursorPosition();
    }

    void processIpAddressChanging() {
        if (!inChanged) editIpAddressField();
    }

    private boolean inChanged = false;

    private void smartClearAddress(String ipAddress) {

        if (previousAddress.endsWith(".") && ipAddress.length() > 0)
            setTextWithSaveCursor(ipAddress.substring(0, ipAddress.length() - 1), -1);
    }

    private void smartWriteAddress(String ipAddress) {
        String[] domains = ipAddress.split("\\.");
        int dl = domains.length;
        if (dl > 0 && dl < 4 && domains[dl - 1].length() == 3 && !ipAddress.endsWith(".")) {
            setTextWithSaveCursor(ipAddress + ".", 1);
        }
    }

    private boolean checkMatchingIpStructure(String ipAddress) {
        return ipAddress.matches("(\\d{1,3}\\.){0,3}\\d{0,3}");
    }

    private void setTextWithSaveCursor(String ipAddress, int offset) {
        int position = parent.getCursorPosition();
        safeWriteIpAddress(ipAddress);
        if (position + offset == ipAddress.length()) position += offset;
        if (position <= ipAddress.length()) parent.setCursorPosition(position);
    }

    private void safeWriteIpAddress(String address) {
        inChanged = true;
        parent.setIpAddress(address);
        inChanged = false;
    }

    private void editIpAddressField() {
        try {
            String ipAddress = parent.getIpAddress();
            if (checkMatchingIpStructure(ipAddress)) {
                if (ipAddress.length() < previousAddress.length())
                    smartClearAddress(ipAddress);
                else smartWriteAddress(ipAddress);
            } else {
                safeWriteIpAddress(previousAddress);
                parent.setCursorPosition(previousPosition - 1);
            }
        } catch (Exception ignored) { }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) parent.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    void clickMainContent(View v) {
        hideKeyboard(v);
    }

    void connect() {
        core.connect(parent, parent.getIpAddress());
    }
    void onPause(){
        activityPaused = true;
    }
    void onResume() {
        if (parent.getIpAddress().isEmpty()) setLocalIp();
        activityPaused = false;
        if(parent.getMode()!= mode) parent.setMode(mode);
    }

    private String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ignored) { }
        return null;
    }

    private void setLocalIp() {
        @SuppressLint("StaticFieldLeak")
        final AsyncTask<Void, Void, Void> getLocalIp = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                final String finalIP = getLocalIp();
                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalIP == null)
                            parent.createErrorConnectionToast();
                        else {
                            String myIP = finalIP.substring(0, finalIP.lastIndexOf(".") + 1);
                            parent.setIpAddress(myIP);
                            parent.setCursorPosition(myIP.length());
                        }
                    }
                });
                return null;
            }
        };
        getLocalIp.execute();
    }
    private void selectMode(int mode) {
        if (this.mode != mode) {
            this.mode = mode;
            if(!activityPaused) parent.setMode(mode);
        }
    }

    @Override
    public void onModelEvent(EventObject event) {
        if (event.getClass() == ChangeModeEvent.class) selectMode(((ChangeModeEvent)event).getMode());
    }
}
