package com.santoni7.readme.activity.main;

import android.util.Log;

import com.santoni7.readme.Constants;
import com.santoni7.readme.common.InjectablePresenter;
import com.santoni7.readme.common.PresenterBase;
import com.santoni7.readme.dagger.MyComponent;
import com.santoni7.readme.data.ImageRepository;
import com.santoni7.readme.data.ImageRepositoryImpl;
import com.santoni7.readme.data.Person;
import com.santoni7.readme.data.datasource.PersonDataSource;
import com.santoni7.readme.util.TextUtils;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.ReplaySubject;

public class MainPresenter extends PresenterBase<MainContract.View> implements MainContract.Presenter {
    private static final String TAG = MainPresenter.class.getSimpleName();
    private CompositeDisposable disposables = new CompositeDisposable();

    private ReplaySubject<Throwable> errors = ReplaySubject.create();

    @Inject PersonDataSource personDataSource;
    @Inject ImageRepository imageRepository;


    @Override
    public void init(MyComponent component) {
        component.inject(this);
    }

    @Override
    public void viewReady() {
        if(personDataSource == null || imageRepository == null){
            throw new IllegalStateException("Presenter must be initialized at first!");
        }
        disposables.add(errors.subscribe(
                err -> {
                    Log.e(TAG, "Error occured: " + err.toString() + "\nCaused by: " + err.getCause());
                    err.printStackTrace();
                }
        ));

        if (!isViewAttached()) {
            errors.onNext(new IllegalStateException("viewReady() was called, but view is not attached!"));
            return;
        }

        readData();
    }

    private void readData() {
        try {
            String json = TextUtils.readStringFromStream(getView().openAssetFile(Constants.DATA_ASSET_FILENAME));
            if (json.isEmpty()) {
                errors.onNext(new IllegalArgumentException("Json file is empty!"));
                return;
            }

            ConnectableObservable<Person> personObservable = personDataSource.parsePeople(json).replay();


            disposables.add(  // Add people without images yet to view
                    personObservable.subscribe(getView()::addPerson, errors::onNext)
            );

            disposables.add(  // Load images, update view
                    imageRepository.populateWithImages(personObservable)
                            .subscribe(getView()::updatePerson, errors::onNext)
            );

            personObservable.connect();
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onListItemClicked(Person p) {
        getView().openDetailsScreen(p);
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        errors.cleanupBuffer();
        imageRepository.dispose();
    }
}
