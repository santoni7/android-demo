package com.santoni7.readme.data.datasource;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.santoni7.readme.data.Person;
import com.santoni7.readme.util.IOUtils;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.ReplaySubject;

public class LocalImageDataSource implements ImageDataSource {

    private static final String TAG = LocalImageDataSource.class.getSimpleName();
    private CompositeDisposable disposables = new CompositeDisposable();

    private WeakReference<Context> contextWeakReference;

    private ReplaySubject<Person> failedObjects = ReplaySubject.create();

    public LocalImageDataSource(Context applicationContext) {
        contextWeakReference = new WeakReference<>(applicationContext);
    }

    @Override
    public Observable<Person> populateWithImages(Observable<Person> people) {
        failedObjects.cleanupBuffer();
        disposables.clear();
        return people.flatMap(this::loadBitmap);
    }

    @Override
    public Observable<Person> getFailedObjects() {
        return failedObjects;
    }

    private Observable<Person> loadBitmap(final Person person) {
        return Observable.create(emitter -> {
            Context ctx = contextWeakReference.get();
            if (ctx == null) {
                Log.e(TAG, "WeakReference to context contains NULL");
                failedObjects.onNext(person);
                return;
            }
            try {
                Bitmap bitmap = IOUtils.loadImage(ctx, person.getImageFileName());
                person.setImageSource(Observable.just(bitmap));
                emitter.onNext(person);
                Log.d(TAG, "Image read and passed to emitter");
            } catch (Throwable e) {
                failedObjects.onNext(person);
                Log.d(TAG, "Image could not be read, added to failedObjects");
            }
            emitter.onComplete();
        });
    }


    @Override
    public void dispose() {
        disposables.clear();
    }

}
