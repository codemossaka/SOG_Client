package com.slavaguk2000.sog_client;

import android.annotation.SuppressLint;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Context;
import android.content.res.Configuration;
import android.icu.lang.UCharacterEnums;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;

public class FullscreenActivity extends AppCompatActivity {
    private static final int UI_ANIMATION_DELAY = 100;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
/*
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(//View.SYSTEM_UI_FLAG_LOW_PROFILE
                    //|
                    View.SYSTEM_UI_FLAG_FULLSCREEN//!!
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE//!!
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                   // | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    );
        }
    };
    View.OnClickListener getClickListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
        };
    }
*/

    private void hideKeyboard(View view) {
        //if(view.getClass() == AppCompatEditText.class) return;
        InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mContentView = findViewById(R.id.fullscreen_content);
        //hide();
        setupControlElements();
        //mContentView.setOnClickListener(getClickListener());
    }

    private AdapterView.OnItemSelectedListener getOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private AdapterView.OnTouchListener getOnTouchListener() {
        return new AdapterView.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return false;
            }
        };
    }

    private void setupSpinner() {
        Spinner modeSpinner = findViewById(R.id.spinner);
        modeSpinner.setOnItemSelectedListener(getOnItemSelectedListener());
        modeSpinner.setOnTouchListener(getOnTouchListener());
    }

    String previousAddress;
    int previousPosition;
    String holdAddress;

    private TextWatcher getIpAddressTextWatcher(final EditText ipAddressField) {
        return new TextWatcher() {
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
        };
    }


    private View.OnKeyListener getEditTextClearOnKeyListener() {
        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EditText ipAddressField = (EditText) v;
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    holdAddress = ipAddressField.getText().toString();
                if (event.getAction() == KeyEvent.ACTION_UP)
                    if (holdAddress.length() - ipAddressField.getText().toString().length() > 1)
                        ipAddressField.setText("");
                return false;
            }
        };
    }

    private void setupIpAddressEditText() {
        final EditText ipAddressField = findViewById(R.id.editText4);
        ipAddressField.setInputType(TYPE_NUMBER_FLAG_DECIMAL | TYPE_CLASS_NUMBER);
        ipAddressField.addTextChangedListener(getIpAddressTextWatcher(ipAddressField));
        ipAddressField.setOnKeyListener(getEditTextClearOnKeyListener());
    }

    private View.OnClickListener getHideKeyboardClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        };
    }

    private void setupControlElements() {
        setupSpinner();
        setupIpAddressEditText();
        findViewById(R.id.fullscreen_content).setOnClickListener(getHideKeyboardClickListener());
    }

    private void setTextWithSaveCursor(EditText ipAddressField, String ipAddress, int offset) {
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
            setTextWithSaveCursor(ipAddressField, ipAddress.substring(0, ipAddress.length() - 1), -1);
    }

    private void smartWriteAddress(EditText ipAddressField, String ipAddress) {
        String[] domains = ipAddress.split("\\.");
        int dl = domains.length;
        if (dl > 0 && dl < 4 && domains[dl - 1].length() == 3 && !ipAddress.endsWith(".")) {
            setTextWithSaveCursor(ipAddressField, ipAddress + ".", 1);
        }
    }
    public boolean checkMatchingIpStructure(String ipAddress)
    {
        return ipAddress.matches("(\\d{1,3}\\.){0,3}\\d{0,3}");
    }

    private void editIpAddressField(EditText ipAddressField) {
        try {
            String ipAddress = ipAddressField.getText().toString();
            if (checkMatchingIpStructure(ipAddress)) {
                if (ipAddress.length() < previousAddress.length())
                    smartClearAddress(ipAddressField, ipAddress);
                else smartWriteAddress(ipAddressField, ipAddress);
            } else {
                inChanged = true;
                ipAddressField.setText(previousAddress);
                ipAddressField.setSelection(previousPosition - 1);
                inChanged = false;
            }
        } catch (Exception ignored) {
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //hide();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //hide();
    }

//    private void hide() {
//        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
//    }

    public void onClick(View v) {
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
