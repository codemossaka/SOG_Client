package com.slavaguk2000.sog_client;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import com.slavaguk2000.sog_client.ChangeMode.ChangeModeEvent;
import com.slavaguk2000.sog_client.ChangeMode.ChangeModeListener;

public class DemonstrationViewModel implements ChangeModeListener {
    private final Handler mHideHandler = new Handler();

    private CoreModel core;
    private DemonstrationView parent;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private boolean buttonHidden = false;

    DemonstrationViewModel(DemonstrationView parent) {
        this.parent = parent;
        core = CoreModel.getInstance(parent.getMode());
        selectMode(core.getMode());
        core.addChangeModeListener(this);
        core.setContentDemonstrator(this);
    }


    private void selectMode(int mode) {
        int myMode = parent.getMode();
        if (myMode != mode) parent.setMode(mode);
    }

    void onStart() {
        delayedHide();
    }

    void onCloseButtonClick() {
        if (!buttonHidden) parent.finish();
        else setButtonVisible(true);
    }

    void onMainFieldClick() {
        setButtonVisible(true);
        delayedHide();
    }

    private Bitmap getBlack() {
        Bitmap blackBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
        blackBitmap.eraseColor(Color.BLACK);
        return blackBitmap;
    }

    public void setText(String textString, String titleString) {
        if (!textString.isEmpty() || !titleString.isEmpty()) setImage(null);
        parent.setText(textString, titleString);
    }

    public void setImage(Bitmap image) {
        if (image == null) image = getBlack();
        else setText("", "");
        final Bitmap finalImage = Bitmap.createBitmap(image);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                parent.setImage(finalImage);
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

    private void setButtonVisible(boolean flag) {
        parent.setButtonVisible(flag);
        buttonHidden = !flag;
    }

    void setMode(int position) {
        core.setMode(position);
    }

    @Override
    public void onChangeMode(ChangeModeEvent event) {
        selectMode(event.getMode());
    }

    void disconnect() {
        core.removeChangeModeListener(this);
        core.disconnect();
    }
}
