package com.santoni7.readme.data;

import android.util.Log;

import com.santoni7.readme.util.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public class WikiUrlProvider {
    private static final String TAG = WikiUrlProvider.class.getSimpleName();
    private static final String API_QUERY_FORMAT = "https://en.wikipedia.org/w/api.php?action=query&titles=File:%s&prop=imageinfo&iiprop=url&format=json";

    public static Observable<String> mapToWikiUrl(@NonNull final String wikiFileName) {
        return Observable.<String>create(emitter -> {
            if (wikiFileName == null) {
                emitter.onError(new IllegalArgumentException("File name cannot be null"));
            }
            HttpURLConnection connection = null;
            try {
                URL url = makeQueryURL(wikiFileName);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                Log.d(TAG, "Opened connection to url: " + url.toString());

                InputStream inputStream = connection.getInputStream();
                String json = TextUtils.readStringFromStream(inputStream);
                Log.d(TAG, "Got json response: " + json);

                String fileUrl = getFileUrl(json);
                Log.d(TAG, "Got fileUrl: " + fileUrl);
                emitter.onNext(fileUrl);
                emitter.onComplete();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                emitter.onError(e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    private static URL makeQueryURL(String fileName) throws MalformedURLException {
        return new URL(String.format(API_QUERY_FORMAT, fileName));
    }

    private static String getFileUrl(String jsonResponse) throws JSONException {
        JSONObject pages = new JSONObject(jsonResponse).getJSONObject("query").getJSONObject("pages");
        JSONArray pagesNames = pages.names();
        if (pagesNames.length() > 0) {
            JSONObject imageInfo = pages
                    .getJSONObject(pagesNames.getString(0))
                    .getJSONArray("imageinfo")
                    .getJSONObject(0);
            return imageInfo.getString("url");
        }
        return null;
    }
}
