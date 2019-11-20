package com.slavaguk2000.sog_client;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import static java.lang.Thread.sleep;

public class DemonstrationView extends AppCompatActivity {
    private static final int UI_ANIMATION_HIDE = 300;
    private static final int UI_ANIMATION_SHOW = 100;

    private TextView mainTextView;
    private TextView titleView;
    private ImageView mainImageView;
    private Button disconnectButton;
    private Spinner modeSpinner;
    private DemonstrationViewModel viewModel;

    private void initFields(){
        mainTextView = findViewById(R.id.demonstrationTextView);
        titleView = findViewById(R.id.demonstrationTitleView);
        mainImageView = findViewById(R.id.imageView);
        disconnectButton = findViewById(R.id.disconnectButton);
        modeSpinner = findViewById(R.id.curtainSpinner);
        viewModel = new DemonstrationViewModel(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_mode_curtain);
        initFields();
        setListeners();
    }

    private void setListeners(){
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.onCloseButtonClick();
            }
        });
        mainImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.onMainFieldClick();
            }
        });
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setMode(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.onStart();
    }

    public void setText(String textString, String titleString) {
        mainTextView.setText(textString);
        titleView.setText(titleString);
    }
    public void setImage(Bitmap image){
        mainImageView.setImageBitmap(image);
    }

    public void setButtonVisible(boolean flag){
        disconnectButton.animate().setDuration(flag?UI_ANIMATION_SHOW:UI_ANIMATION_HIDE).alpha(flag?1:0);
    }

    public void setMode(int selection){
        modeSpinner.setSelection(selection);
    }
    public int getMode(){
        return modeSpinner.getSelectedItemPosition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.disconnect();
    }
}
