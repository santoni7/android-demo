package com.santoni7.readme.data;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.santoni7.readme.data.datasource.ImageDataSource;
import com.santoni7.readme.util.IOUtils;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.ReplaySubject;

public class ImageRepositoryImpl implements ImageRepository {
    private final static String TAG = ImageRepositoryImpl.class.getSimpleName();

    private CompositeDisposable disposables = new CompositeDisposable();

    private ImageDataSource localDataSource, remoteDataSource;

    private ReplaySubject<Person> peopleWithImageSubject = ReplaySubject.create();

    public ImageRepositoryImpl(ImageDataSource localImageDataSource, ImageDataSource remoteImageDataSource) {
        this.localDataSource = localImageDataSource;
        this.remoteDataSource = remoteImageDataSource;
    }


    @Override
    public Observable<Person> populateWithImages(Observable<Person> people) {
        Log.d(TAG, "populateWithImages called: people=" + people);
        disposables.clear();
        peopleWithImageSubject.cleanupBuffer();


        // Try to load images from local storage,
        // then load from remote source those that are not found
        Observable<Person> peopleFromLocal = localDataSource.populateWithImages(people);
        Observable<Person> failedFromLocal = localDataSource.getFailedObjects();

        Observable<Person> peopleFromRemote = remoteDataSource.populateWithImages(failedFromLocal)
                .replay().autoConnect();

        // TODO: Run in a new thread
        localDataSource.savePersonImages(peopleFromRemote);

        // Merge from two sources and subscribe subject to result
        Observable.mergeDelayError(peopleFromLocal, peopleFromRemote)
                .subscribe(peopleWithImageSubject);

        subscribeToLog(peopleWithImageSubject, "RESULT");
        return peopleWithImageSubject;
    }

    private void subscribeToLog(Observable<Person> people, String tag) {
        disposables.add(
                people.subscribe(person ->
                                Log.d(TAG, "[" + tag + "] onNext() -> Person = " + person.toString()),
                        err -> Log.d(TAG, "[" + tag + "] onError() -> " + err.toString()),
                        () -> Log.d(TAG, "[" + tag + "] onCompleted()")
                )
        );
    }

    @Override
    public void dispose() {
        disposables.clear();
        localDataSource.dispose();
        remoteDataSource.dispose();
    }

    @Override
    public Single<Person> findPersonById(String id) {
        return peopleWithImageSubject
                .filter(person -> person.getId().equals(id))
                .firstOrError();
    }
}
