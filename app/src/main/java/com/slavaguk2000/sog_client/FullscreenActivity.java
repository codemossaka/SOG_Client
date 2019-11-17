package com.slavaguk2000.sog_client;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class FullscreenActivity extends AppCompatActivity {
    private static final int UI_ANIMATION_DELAY = 100;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mContentView = findViewById(R.id.fullscreen_content);
        hide();
        setListners();
    }

    private void setSpinnersListners() {
        ((Spinner) findViewById(R.id.spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    String previousAddress;
    int previousPosition;

    private void setEditTextListners() {
        final EditText ipAddressField = findViewById(R.id.editText4);
        ipAddressField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousAddress = ipAddressField.getText().toString();
                previousPosition = ipAddressField.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!inChanged)
                    editIpAddressField(ipAddressField);
            }
        });
    }

    private void setListners() {
        setSpinnersListners();
        setEditTextListners();
    }

    private void setTextWithSaveCoursor(EditText ipAddressField, String ipAddress, int offset) {
        inChanged = true;
        int position = ipAddressField.getSelectionStart();
        ipAddressField.setText(ipAddress);
        if (position + offset == ipAddress.length()) position += offset;
        if (position <= ipAddress.length()) ipAddressField.setSelection(position);
        inChanged = false;
    }

    boolean inChanged = false;

    private void smartClearAddress(EditText ipAddressField, String ipAddress) {
        if (previousAddress.endsWith(".") && ipAddress.length() > 0)
            setTextWithSaveCoursor(ipAddressField, ipAddress.substring(0, ipAddress.length() - 1), -1);
    }

    private void smartWriteAddress(EditText ipAddressField, String ipAddress) {
        String[] domains = ipAddress.split("\\.");
        int dl = domains.length;
        if (dl > 0 && dl < 4 && domains[dl - 1].length() == 3 && !ipAddress.endsWith(".")) {
            setTextWithSaveCoursor(ipAddressField, ipAddress + ".", 1);
        }
    }

    private void editIpAddressField(EditText ipAddressField) {
        try {
            String ipAddress = ipAddressField.getText().toString();
            if (!ipAddress.matches("\\d{1,3}(\\.\\d{0,3}){0,3}") && !ipAddress.isEmpty()) {
                inChanged = true;
                ipAddressField.setText(previousAddress);
                ipAddressField.setSelection(previousPosition - 1);
                inChanged = false;
                return;
            }
            if (ipAddress.length() < previousAddress.length())
                smartClearAddress(ipAddressField, ipAddress);
            else smartWriteAddress(ipAddressField, ipAddress);
        }
        catch(Exception ex){}
    }


    @Override
    protected void onResume() {
        super.onResume();
        hide();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hide();
    }

    private void hide() {
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    public void onClick(View v) {
        hide();
    }

    private void setLocalIp() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> getLocalIp = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo connectionInfo = null;
                if (wm != null) {
                    connectionInfo = wm.getConnectionInfo();
                }
                int ipAddress = 0;
                if (connectionInfo != null) {
                    ipAddress = connectionInfo.getIpAddress();
                }
                final String finalMyIp = Formatter.formatIpAddress(ipAddress);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((EditText) findViewById(R.id.editText4)).setText(finalMyIp);
                    }
                });
                return null;
            }
        };
        getLocalIp.execute();
    }
}
