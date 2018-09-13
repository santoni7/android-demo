package com.santoni7.readme.async;

import android.os.AsyncTask;
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

import io.reactivex.subjects.AsyncSubject;

/**
 * An async task, used to get real image URL by Wiki File Name with Wikipedia API
 */
public class WikiFileUrlAsyncTask extends AsyncTask<String, Void, String> implements ReactiveAsyncTask<String> {
    private static final String TAG = WikiFileUrlAsyncTask.class.getSimpleName();
    private static final String API_QUERY_FORMAT = "https://en.wikipedia.org/w/api.php?action=query&titles=File:%s&prop=imageinfo&iiprop=url&format=json";

    private AsyncSubject<String> urlSource = AsyncSubject.create();

    /**
     * Save exception, thrown during execution, in order to pass it to observers
     */
    private Throwable thrownException = null;

    @Override
    public AsyncSubject<String> getResultSource() {
        return urlSource;
    }

    @Override
    protected String doInBackground(String... strings) {
        String wikiFileName = strings[0];
        Log.d(TAG, "Started task in background for wikiFileName=" + wikiFileName);
        if (wikiFileName == null) return null;
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
            return fileUrl;

        } catch (IOException | JSONException e) {
            thrownException = e;
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result == null || result.isEmpty()){
            urlSource.onError(thrownException);
        } else {
            urlSource.onNext(result);
            urlSource.onComplete();
        }
    }


    private URL makeQueryURL(String fileName) throws MalformedURLException {
        return new URL(String.format(API_QUERY_FORMAT, fileName));
    }

    private String getFileUrl(String jsonResponse) throws JSONException {
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
