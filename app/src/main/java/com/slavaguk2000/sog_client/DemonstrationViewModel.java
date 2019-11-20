package com.slavaguk2000.sog_client;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

public class DemonstrationViewModel {
    private final Handler mHideHandler = new Handler();


    private DemonstrationView parent;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private boolean buttonHidden = false;

    public DemonstrationViewModel(DemonstrationView parent) {
        this.parent = parent;
    }

    public void onStart() {
        delayedHide();
    }

    public void onCloseButtonClick() {
        if (!buttonHidden) parent.finish();
        else setButtonVisible(true);
    }

    public void onMainFieldClick() {
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
    public void setImage(Bitmap image){
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

    private void setButtonVisible(boolean flag){
        parent.setButtonVisible(flag);
        buttonHidden = !flag;
    }
}
