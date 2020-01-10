package com.slavaguk2000.sog_client;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import static android.text.InputType.TYPE_CLASS_NUMBER;

public class ConnectionView extends AppCompatActivity{

    private Spinner modeSpinner;
    private EditText ipAddressText;
    private Button connectButton;
    private View mainContent;
    private ConnectionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        initControlElements();
        setupControlElements();
    }

    private void initControlElements() {
        modeSpinner = findViewById(R.id.connectedSpinner);
        connectButton = findViewById(R.id.button3);
        ipAddressText = findViewById(R.id.editText4);
        mainContent = findViewById(R.id.fullscreen_content);
        viewModel = new ConnectionViewModel(this);
    }

    private AdapterView.OnItemSelectedListener getOnItemSelectedSpinnerListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setMode(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private AdapterView.OnTouchListener getOnTouchSpinnerListener() {
        return new AdapterView.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewModel.spinnerTouched(v);
                return false;
            }
        };
    }

    private void setupSpinner() {
        modeSpinner.setOnItemSelectedListener(getOnItemSelectedSpinnerListener());
        modeSpinner.setOnTouchListener(getOnTouchSpinnerListener());
    }



    private TextWatcher getIpAddressTextWatcher(final EditText ipAddressField) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                viewModel.savePreviousTextFieldInstance();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.processIpAddressChanging();
            }
        };
    }

    private void setupIpAddressEditText() {
        ipAddressText.setInputType(TYPE_CLASS_NUMBER);
        ipAddressText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        ipAddressText.addTextChangedListener(getIpAddressTextWatcher(ipAddressText));
    }

    private View.OnClickListener getMainClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.clickMainContent(v);
            }
        };
    }

    private void setupButton() {
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.connect();
            }
        });
    }

    private void setupControlElements() {
        setupSpinner();
        setupIpAddressEditText();
        setupButton();
        mainContent.setOnClickListener(getMainClickListener());
    }

    public String getIpAddress(){
        return ipAddressText.getText().toString();
    }

    public int getCursorPosition(){
        return  ipAddressText.getSelectionStart();
    }

    public void setIpAddress(String ipAddress){
        ipAddressText.setText(ipAddress);
    }

    public void setCursorPosition(int position){
        ipAddressText.setSelection(position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void createErrorConnectionToast(){
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_connection), Toast.LENGTH_LONG).show();
    }

    public void setMode(int selection){
        modeSpinner.setSelection(selection);
    }
    public int getMode(){
        return modeSpinner.getSelectedItemPosition();
    }
}
