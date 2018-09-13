package com.santoni7.readme.data.datasource;

import android.util.Log;

import com.santoni7.readme.async.ImageDownloadAsyncTask;
import com.santoni7.readme.async.WikiFileUrlAsyncTask;
import com.santoni7.readme.data.Person;
import com.santoni7.readme.util.TextUtils;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.ReplaySubject;

public class RemoteImageDataSource implements ImageDataSource {
    private static final String TAG = RemoteImageDataSource.class.getSimpleName();
    private CompositeDisposable disposables = new CompositeDisposable();
    private ReplaySubject<Person> failedObjects = ReplaySubject.create();

    @Override
    public Observable<Person> populateWithImages(Observable<Person> people) {
        return people.flatMap(this::transformUrl)
                .flatMap(this::loadBitmap);
    }

    @Override
    public Observable<Person> getFailedObjects() {
        return failedObjects;
    }

    private Observable<Person> loadBitmap(Person person) {
        return Observable.create(emitter -> {
            ImageDownloadAsyncTask task = new ImageDownloadAsyncTask();
            task.execute(person.getAvatarUrl());

            disposables.add(task.getResultSource().subscribe(
                    bmp -> {
                        person.setImageSource(Observable.just(bmp));
                        emitter.onNext(person);
                        emitter.onComplete();
                        Log.d(TAG, "Bitmap successfully downloaded for Person" + person.toString());
                    },
                    err -> failedObjects.onNext(person)
            ));
        });
    }

    private Observable<Person> transformUrl(Person person) {
        return Observable.create(emitter -> {
            String avatarUrl = person.getAvatarUrl();

            if (TextUtils.isWikiPageUrl(avatarUrl)) { // Real url must be requested from Wiki Api
                String wikiFile = person.getImageFileName();
                Log.d(TAG, "\t Image url is WikiFile: " + wikiFile);
                WikiFileUrlAsyncTask task = new WikiFileUrlAsyncTask();
                task.execute(wikiFile);
                disposables.add(task.getResultSource()
                        .subscribe(url -> {
                            person.setAvatarUrl(url);
                            person.setImageFileName(wikiFile);
                            emitter.onNext(person);
                            emitter.onComplete();
                        }, err -> {
                            failedObjects.onNext(person);
                            Log.e(TAG, "Could not receive image url from WikiApi: " + err.toString());
                        }));
            } else {
                person.setImageFileName(TextUtils.getImageFileName(avatarUrl));
                emitter.onNext(person);
                emitter.onComplete();
            }
        });
    }

    @Override
    public void dispose() {
        disposables.clear();
    }
}
