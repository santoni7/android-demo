package com.santoni7.readme.data;

import android.util.Log;

import com.santoni7.readme.data.datasource.RemoteImageDataSource;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.ReplaySubject;

public class ImageRepository implements Disposable {
    private static final String TAG = ImageRepository.class.getSimpleName();
    private static ImageRepository _instance = null;

    public static ImageRepository instance() {
        if (_instance == null) {
            _instance = new ImageRepository();
        }
        return _instance;
    }

    private CompositeDisposable disposables = new CompositeDisposable();


    private ReplaySubject<Throwable> errorSubject = ReplaySubject.create();
    private ReplaySubject<Person> personSubject = ReplaySubject.create();

    private List<Person> personList = new ArrayList<>();

    private RemoteImageDataSource remoteDataSource = new RemoteImageDataSource();
    //todo local image data source

    // Private constructor in a singleton class
    private ImageRepository() {
        Log.d(TAG, "ImageRepository singleton instantiated");
    }


    /**
     * For every person in sequence, load and save an image from specified avatar url
     * @param personObservable sequence of people to load images for
     * @return sequence of people, with images ready
     */
    public Observable<Person> populateWithImages(Observable<Person> personObservable) {
        ConnectableObservable<Person> resultObservable = remoteDataSource.populateWithImages(personObservable).publish();
        resultObservable.subscribe(personSubject);
        Disposable d = resultObservable.subscribe(personList::add, errorSubject::onNext);
        disposables.add(d);
        disposables.add(resultObservable.connect());

        return personSubject;
    }

    public Single<Person> findPersonById(String personId) {
        return personSubject.filter(p -> p.getId().equals(personId)).firstOrError();
    }

    @Override
    public void dispose() {
        disposables.clear();
    }

    @Override
    public boolean isDisposed() {
        return false;
    }
}
