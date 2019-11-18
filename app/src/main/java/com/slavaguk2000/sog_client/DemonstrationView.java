package com.slavaguk2000.sog_client;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Thread.sleep;

public class DemonstrationView extends AppCompatActivity {
    private final Handler mHideHandler = new Handler();
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_HIDE = 300;
    private static final int UI_ANIMATION_SHOW = 100;

    private boolean buttonHidden = false;

    private TextView mainTextView;
    private TextView titleView;
    private ImageView mainImageView;
    private Button disconnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_mode_curtain);
        mainTextView = findViewById(R.id.demonstrationTextView);
        titleView = findViewById(R.id.demonstrationTitleView);
        mainImageView = findViewById(R.id.imageView);
        disconnectButton = findViewById(R.id.disconnectButton);
        setupButton();
    }

    private void setupButton(){
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonHidden) finish();
                else setButtonVisible(true);
            }
        });
        delayedHide();
        mainImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonVisible(true);
                delayedHide();
            }
        });
    }

    private Bitmap getBlack(){
        Bitmap blackBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
        blackBitmap.eraseColor(Color.BLACK);
        return blackBitmap;
    }
    public void setText(String textString, String titleString) {
        if (!textString.isEmpty() || !titleString.isEmpty()) setImage(null);
        mainTextView.setText(textString);
        titleView.setText(titleString);
    }

    public void setImage(Bitmap image){
        if (image == null) image = getBlack();
        else setText("", "");
        final Bitmap finalImage = Bitmap.createBitmap(image);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                mainImageView.setImageBitmap(finalImage);
            }
        });
    }

    private final Runnable hideButtonRunnable = new Runnable() {
        @Override
        public void run() {
            setButtonVisible(false);
        }
    };

    private void delayedHide() {
        mHideHandler.removeCallbacks(hideButtonRunnable);
        mHideHandler.postDelayed(hideButtonRunnable, AUTO_HIDE_DELAY_MILLIS);
    }

    private void setButtonVisible(boolean flag){
        disconnectButton.animate().setDuration(flag?UI_ANIMATION_SHOW:UI_ANIMATION_HIDE).alpha(flag?1:0);
        buttonHidden = !flag;
    }
}
