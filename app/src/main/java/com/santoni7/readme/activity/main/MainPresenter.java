package com.santoni7.readme.activity.main;

import android.util.Log;

import com.santoni7.readme.Constants;
import com.santoni7.readme.common.PresenterBase;
import com.santoni7.readme.dagger.MyComponent;
import com.santoni7.readme.data.ImageRepository;
import com.santoni7.readme.data.Person;
import com.santoni7.readme.data.datasource.PersonDataSource;
import com.santoni7.readme.util.TextUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.ReplaySubject;

public class MainPresenter extends PresenterBase<MainContract.View> implements MainContract.Presenter {
    private static final String TAG = MainPresenter.class.getSimpleName();
    private CompositeDisposable disposables = new CompositeDisposable();

    private ReplaySubject<Throwable> errors = ReplaySubject.create();

    @Inject
    PersonDataSource personDataSource;
    @Inject
    ImageRepository imageRepository;


    @Override
    public void init(MyComponent component) {
        component.inject(this);
    }

    @Override
    public void viewReady() {
//        todo: Is this really needed?
//        if (personDataSource == null || imageRepository == null) {
//            throw new IllegalStateException("Presenter must be initialized at first!");
//        }
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

        requestDataUpdate(ImageRepository.SourceStrategy.LocalFirst);
    }

    private void requestDataUpdate(ImageRepository.SourceStrategy sourceStrategy) {
        try {
            getView().hideProgress();
//            String json = TextUtils.readStringFromStream(getView().openAssetFile(Constants.DATA_ASSET_FILENAME));
//            if (json.isEmpty()) {
//                errors.onNext(new IllegalArgumentException("Json file is empty!"));
//                return;
//            }

            ConnectableObservable<Person> personObservable = personDataSource
                    .parsePeople(getView().openAssetFile(Constants.DATA_ASSET_FILENAME))
                    .observeOn(AndroidSchedulers.mainThread())
                    .replay();

            disposables.add(  // Add people without images to the view as is
                    personObservable.subscribe(getView()::addPerson, errors::onNext)
            );

            Observable<Person> resultPeople = imageRepository.populateWithImages(personObservable, sourceStrategy)
                    .observeOn(AndroidSchedulers.mainThread());

            disposables.add(
                    resultPeople.subscribe(getView()::updatePerson, errors::onNext)
            );

            personObservable.connect();
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onRefreshRequested() {
        Log.d(TAG, "onRefreshRequested");
        getView().clearPeopleList();
        requestDataUpdate(ImageRepository.SourceStrategy.RemoteFirst);
    }

    @Override
    public void onListItemClicked(Person p) {
        getView().openDetailsScreen(p);
    }

    @Override
    public void onAboutClicked() {
        Log.d(TAG, "onAboutClicked");
        getView().showAboutDialog();
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        errors.cleanupBuffer();
        imageRepository.dispose();
    }
}
