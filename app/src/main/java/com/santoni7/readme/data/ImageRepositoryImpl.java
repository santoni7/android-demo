package com.santoni7.readme.data;

import android.util.Log;

import com.santoni7.readme.data.datasource.ImageDataSource;

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
    public Observable<Person> populateWithImages(Observable<Person> people, SourceStrategy sourceStrategy) {
        Log.d(TAG, "populateWithImages(people=" + people + ", sourceStrategy=" + sourceStrategy.toString());
        disposables.clear();
        peopleWithImageSubject.cleanupBuffer();

        Observable<Person> result;
        switch (sourceStrategy) {
            case LocalFirst:
            default:
                result = runLocalFirst(people);
                break;
            case RemoteFirst:
                result = runRemoteFirst(people);
                break;
        }

        result.subscribe(peopleWithImageSubject);

        subscribeToLog(peopleWithImageSubject, "RESULT");
        return peopleWithImageSubject;
    }

    private Observable<Person> runLocalFirst(Observable<Person> people) {
        ImagePopulationResult result = run(people, localDataSource, remoteDataSource);
        localDataSource.savePersonImages(result.failedFromFirst);
        return result.successful;
    }


    private Observable<Person> runRemoteFirst(Observable<Person> people) {
        ImagePopulationResult result = run(people, remoteDataSource, localDataSource);
        localDataSource.savePersonImages(result.successful);
        return result.successful;
    }

    // Get all available images from first source, then try get remaining from second
    private ImagePopulationResult run(Observable<Person> people, ImageDataSource firstSource, ImageDataSource secondSource) {
        Observable<Person> fromFirst = firstSource.populateWithImages(people);
        Observable<Person> fromSecond = secondSource.populateWithImages(firstSource.getFailedObjects());

        Observable<Person> successful = Observable.mergeDelayError(fromFirst, fromSecond);
        return new ImagePopulationResult(successful, firstSource.getFailedObjects(), secondSource.getFailedObjects());
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

    private class ImagePopulationResult {
        Observable<Person> successful, failedFromFirst, failedFromSecond;

        ImagePopulationResult(Observable<Person> successful, Observable<Person> failedFromFirst, Observable<Person> failedFromSecond) {
            this.successful = successful;
            this.failedFromFirst = failedFromFirst;
            this.failedFromSecond = failedFromSecond;
        }
    }
}
