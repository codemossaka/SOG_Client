package com.slavaguk2000.sog_client;

import android.graphics.Bitmap;

public class Decoder {
    private Decoder(){}
    private static int[] createColorsArray(int size){
        if (size % 4 != 0) size += 4;
        return new int[size];
    }
    public static Bitmap decodeImage(int height, int width, int[] encodedArray){
        int size = height*width;
        int[] colors = createColorsArray(size);
        for (int i = 0; i < size; i += 4) {
            int color = encodedArray[i / 4];
            for (int j = 3; j >= 0; j--) {
                colors[i + j] = (color % 4) * (255/3)* 16843009;//(1+16+256+4096+65536+‭1048576‬+‭16777216‬)
                color /= 4;
            }
        }
        return Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
    }

}
