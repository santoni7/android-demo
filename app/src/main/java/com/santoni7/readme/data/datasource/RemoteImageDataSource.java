package com.santoni7.readme.data.datasource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.santoni7.readme.Constants;
import com.santoni7.readme.data.Person;
import com.santoni7.readme.data.WikiUrlProvider;
import com.santoni7.readme.util.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.ReplaySubject;

public class RemoteImageDataSource implements ImageDataSource {
    private static final String TAG = RemoteImageDataSource.class.getSimpleName();
    private CompositeDisposable disposables = new CompositeDisposable();
    private ReplaySubject<Person> failedObjects = ReplaySubject.create();

    @Override
    public Observable<Person> populateWithImages(Observable<Person> people) {
        return people
                .concatMapDelayError(this::transformUrl)
                .concatMapDelayError(this::populatePersonWithBitmap)
                .replay().autoConnect()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Person> getFailedObjects() {
        return failedObjects;
    }

    private Observable<Person> populatePersonWithBitmap(Person person) {
        return downloadBitmap(person.getAvatarUrl())
                .timeout(Constants.NETWORK_TIMEOUT_MS, TimeUnit.MILLISECONDS) // TODO: Set default (error) img on timeout
                .map(bmp -> {
                    person.setImage(bmp);
                    return person;
                });
    }

    private Observable<Bitmap> downloadBitmap(String urlString) {
        return Observable.<Bitmap>create(emitter -> {
            InputStream inputStream = null;
            try {
                URL url = new URL(urlString);
                inputStream = url.openConnection().getInputStream();
                Bitmap img = BitmapFactory.decodeStream(inputStream);

                Log.d(TAG, "Bitmap successfully downloaded");
                emitter.onNext(img);
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
                Log.e(TAG, "Exception occurred during execution: " + e);
                e.printStackTrace();
            } finally {
                if (inputStream != null)
                    inputStream.close();
            }
        }).subscribeOn(Schedulers.io());
    }

    private Observable<Person> transformUrl(Person person) {
        return Observable.create(emitter -> {
            String avatarUrl = person.getAvatarUrl();

            if (TextUtils.isWikiPageUrl(avatarUrl)) { // Real url must be requested from Wiki Api
                Disposable d = WikiUrlProvider.mapToWikiUrl(person.getImageFileName())
                        .timeout(Constants.NETWORK_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                        .subscribe(url -> {
                            person.setAvatarUrl(url);
                            emitter.onNext(person);
                            emitter.onComplete();
                        }, err -> {
                            failedObjects.onNext(person);
                            Log.e(TAG, "Could not receive image url from WikiApi: " + err.toString());
                        });
                disposables.add(d);
            } else {
                emitter.onNext(person);
                emitter.onComplete();
            }
        });
    }


    @Override
    public void savePersonImages(Observable<Person> people) {
        // throw new Exception("Method savePersonImages() not implemented for RemoteImageDataSource");

        // NOT IMPLEMENTED FOR THIS DATA SOURCE
    }

    @Override
    public void dispose() {
        disposables.clear();
    }
}
