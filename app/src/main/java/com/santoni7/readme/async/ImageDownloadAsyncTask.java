package com.santoni7.readme.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.AsyncSubject;

/**
 * Downloads an image from specified url
 * Parameter: image url; Progress: void; Result: Bitmap
 */
public class ImageDownloadAsyncTask extends AsyncTask<String, Void, Bitmap> implements ReactiveAsyncTask<Bitmap>{
    private final String TAG = ImageDownloadAsyncTask.class.getSimpleName();

    private AsyncSubject<Bitmap> source = AsyncSubject.create();
    private Throwable exception;

    /**
     * Get result bitmap observable, which will be notified when image is successfully downloaded,
     * or an error occurred
     */

    @Override
    public AsyncSubject<Bitmap> getResultSource() {
        return source;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String urlString = strings[0];
        Bitmap img = null;
        InputStream inputStream;
        try {
            URL url = new URL(urlString);
            inputStream = url.openConnection().getInputStream();
            img = BitmapFactory.decodeStream(inputStream);

            Log.e(TAG, "Bitmap successfully downloaded");
            return img;
        } catch (IOException e) {
            exception = e;
            Log.e(TAG, "Exception occurred during execution: " + e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(bitmap != null) {
            source.onNext(bitmap);
            source.onComplete();
        } else {
            source.onError(exception);
        }
    }

}
