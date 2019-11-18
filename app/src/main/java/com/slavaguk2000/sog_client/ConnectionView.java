package com.slavaguk2000.sog_client;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.InetAddresses;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import static android.text.InputType.TYPE_CLASS_NUMBER;

public class ConnectionView extends AppCompatActivity {
    private static final int HOLD_TIME = 50;

    private void hideKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        setupControlElements();
        setLocalIp();
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
    long holdStartTime;

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

    boolean pressed = false;

    private View.OnKeyListener getEditTextClearOnKeyListener() {
        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EditText ipAddressField = (EditText) v;
                if (inChanged) return true;
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && !pressed) {
                        pressed = true;
                        holdStartTime = System.currentTimeMillis();
                    }
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        pressed = false;
                        if (System.currentTimeMillis() - holdStartTime > HOLD_TIME)
                            ipAddressField.setText("");
                    }
                }
                return false;
            }
        };
    }

    private void setupIpAddressEditText() {
        final EditText ipAddressField = findViewById(R.id.editText4);
        ipAddressField.setInputType(TYPE_CLASS_NUMBER);
        ipAddressField.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
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

    private void setupButton() {
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent demonstrationViewIntent = new Intent(ConnectionView.this, DemonstrationView.class);
                startActivity(demonstrationViewIntent);
            }
        });
    }

    private void setupControlElements() {
        setupSpinner();
        setupIpAddressEditText();
        setupButton();
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
    int clearOffset = 0;

    private void smartClearAddress(EditText ipAddressField, String ipAddress) {
        if (previousAddress.endsWith(".") && ipAddress.length() > 0) {
            setTextWithSaveCursor(ipAddressField, ipAddress.substring(0, ipAddress.length() - 1), -1);
            clearOffset = 1;
        } else clearOffset = 0;
    }

    private void smartWriteAddress(EditText ipAddressField, String ipAddress) {
        String[] domains = ipAddress.split("\\.");
        int dl = domains.length;
        if (dl > 0 && dl < 4 && domains[dl - 1].length() == 3 && !ipAddress.endsWith(".")) {
            setTextWithSaveCursor(ipAddressField, ipAddress + ".", 1);
        }
    }

    private boolean checkMatchingIpStructure(String ipAddress) {
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
                inChanged = false;
                ipAddressField.setSelection(previousPosition - 1);
            }
        } catch (Exception ignored) {
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onClick(View v) {
    }

    private String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ignored) {
        }
        return null;
    }

    private void setLocalIp() {
        @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> getLocalIp = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                final String finalMyIp = getLocalIp();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalMyIp == null)
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                        else {
                            EditText ipEditText = findViewById(R.id.editText4);
                            ipEditText.setText(finalMyIp.substring(0, finalMyIp.lastIndexOf(".") + 1));
                            ipEditText.setSelection(ipEditText.getText().toString().length());
                        }
                    }
                });
                return null;
            }
        };
        getLocalIp.execute();
    }
}
