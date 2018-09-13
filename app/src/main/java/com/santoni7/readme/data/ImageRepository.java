package com.santoni7.readme.data;

import android.content.Context;
import android.util.Log;

import com.santoni7.readme.data.datasource.ImageDataSource;
import com.santoni7.readme.data.datasource.LocalImageDataSource;
import com.santoni7.readme.data.datasource.RemoteImageDataSource;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.ReplaySubject;

public class ImageRepository {
    private final static String TAG = ImageRepository.class.getSimpleName();

    private static ImageRepository _instance = null;

    public static ImageRepository instance() {
        if (_instance == null) {
            _instance = new ImageRepository();
        }
        return _instance;
    }

    private CompositeDisposable disposables = new CompositeDisposable();

    private ImageDataSource localDataSource;
    private ImageDataSource remoteDataSource;

    private ReplaySubject<Person> peopleWithImageSubject = ReplaySubject.create();

    private boolean isInitialized = false;

    public void initialize(Context applicationContext) {
        localDataSource = new LocalImageDataSource(applicationContext);
        remoteDataSource = new RemoteImageDataSource();
        isInitialized = true;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public Observable<Person> populateWithImages(Observable<Person> people) {
        Log.d(TAG, "populateWithImages called: people=" + people);
        if (!isInitialized()) {
            Log.e(TAG, "populateWithImages(): ImageRepository not initialized");
            return Observable.error(new IllegalAccessException("ImageRepository not initialized"));
        }
        peopleWithImageSubject.cleanupBuffer();


        // Try to load images from local storage,
        // then load from remote source those that are not found
        Observable<Person> peopleFromLocal = localDataSource.populateWithImages(people);
        Observable<Person> failedFromLocal = localDataSource.getFailedObjects();

        Observable<Person> peopleFromRemote = remoteDataSource.populateWithImages(failedFromLocal);


        subscribeToLog(peopleFromLocal, "PeopleFromLocal");
        subscribeToLog(failedFromLocal, "FailedFromLocal");
        subscribeToLog(peopleFromRemote, "PeopleFromRemote");


        Observable<Person> peopleWithImages = Observable.mergeDelayError(peopleFromLocal, peopleFromRemote);
        peopleWithImages.subscribe(peopleWithImageSubject);
        subscribeToLog(peopleWithImageSubject, "RESULT");
        return peopleWithImageSubject;
    }

    private void subscribeToLog(Observable<Person> people, String tag) {
        disposables.add(
                people.subscribe(person ->
                        Log.d(TAG, "[" +tag + "] onNext() -> Person = " + person.toString()),
                        err -> Log.d(TAG, "[" +tag + "] onError() -> " + err.toString()),
                        () -> Log.d(TAG, "[" +tag + "] onCompleted()")
                )
        );
    }

    public void dispose() {
        disposables.clear();
        localDataSource.dispose();
        remoteDataSource.dispose();
    }

    public Single<Person> findPersonById(String id) {
        return peopleWithImageSubject
                .filter(person -> person.getId().equals(id))
                .firstOrError();
    }
}
