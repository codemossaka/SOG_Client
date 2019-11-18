package com.slavaguk2000.sog_client;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Thread.sleep;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DemonstrationView extends AppCompatActivity {
    private TextView mainTextView;
    private TextView titleView;
    private ImageView mainImageView;
    private Button disconnectButton;
    int verce = 0;
    String[] texts = {
            "1. In the beginning God created the heavens and the earth.",
            "2. Now the earth was formless and empty, darkness was over the surface of the deep, and the Spirit of God was hovering over the waters.",
            "3. And God said, “Let there be light,” and there was light.",
            "4. God saw that the light was good, and he separated the light from the darkness.",
            "5. God called the light “day,” and the darkness he called “night.” And there was evening, and there was morning—the first day.",
            "6. And God said, “Let there be a vault between the waters to separate water from water.”",
            "7. So God made the vault and separated the water under the vault from the water above it. And it was so.",
            "8. God called the vault “sky.” And there was evening, and there was morning—the second day.",
            "9 And God said, “Let the water under the sky be gathered to one place, and let dry ground appear.” And it was so." ,
            "10 God called the dry ground “land,” and the gathered waters he called “seas.” And God saw that it was good."
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demonstration_view);
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
                finish();
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
}
