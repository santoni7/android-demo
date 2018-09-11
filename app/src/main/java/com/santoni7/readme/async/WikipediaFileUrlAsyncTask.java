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

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;

public class WikipediaFileUrlAsyncTask extends AsyncTask<String, Void, Observable<String>> {
    private static final String TAG = WikipediaFileUrlAsyncTask.class.getSimpleName();
    private static final String API_BASE = "https://en.wikipedia.org/w/api.php?";
    private static final String QUERY_FORMAT = "action=query&titles=File:%s&prop=imageinfo&iiprop=url&format=json";

//    private ResultListener listener;
//
    Observer<String> observer;
    public WikipediaFileUrlAsyncTask(@NonNull Observer<String> observer){
        this.observer = observer;
    }

    @Override
    protected Observable<String> doInBackground(String... strings) {
        String wikiFileName = strings[0];
        Log.d(TAG, "Started task in background for wikiFileName="+wikiFileName);
        if(wikiFileName == null) return null;
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
            if(fileUrl == null)
                return Observable.error(new Exception("Could not get file url."));
            return Observable.just(fileUrl);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return Observable.error(new Exception("Could not get file url."));
    }

//    @Override
//    protected void onPostExecute(String result) {
//        super.onPostExecute(result);
//        if(result == null || result.isEmpty()){
//            listener.onError();
//        } else {
//            listener.onSuccess(result);
//        }
//    }


    @Override
    protected void onPostExecute(Observable<String> stringObservable) {
        super.onPostExecute(stringObservable);
        stringObservable.subscribe(observer);
    }

    private URL makeQueryURL(String fileName) throws MalformedURLException {
        return new URL(API_BASE + String.format(QUERY_FORMAT, fileName));
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

//    public interface ResultListener {
//        void onSuccess(String fileURL);
//        void onError();
//    }
}
