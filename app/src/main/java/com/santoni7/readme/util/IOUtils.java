package com.santoni7.readme.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class IOUtils {
    public static void saveImage(Context context, Bitmap b, String imageName) throws IOException {
        FileOutputStream foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
        b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
        foStream.close();
    }

    public static Bitmap loadImage(Context context, String imageName) throws IOException {
        FileInputStream fiStream = context.openFileInput(imageName);
        Bitmap bitmap = BitmapFactory.decodeStream(fiStream);
        fiStream.close();
        return bitmap;
    }

    public static boolean fileExists(Context context, String fileName) {
        return Arrays.asList(context.fileList()).contains(fileName);
    }
}
