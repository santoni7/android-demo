package com.santoni7.readme.activity.main;

import com.santoni7.readme.Constants;
import com.santoni7.readme.common.PresenterBase;
import com.santoni7.readme.data.ImageRepository;
import com.santoni7.readme.data.Person;
import com.santoni7.readme.data.datasource.PersonDataSource;
import com.santoni7.readme.util.TextUtils;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.ReplaySubject;

public class MainPresenter extends PresenterBase<MainContract.View> implements MainContract.Presenter {
    private static final String TAG = MainPresenter.class.getSimpleName();
    private CompositeDisposable disposables = new CompositeDisposable();

    private ReplaySubject<Throwable> errors = ReplaySubject.create();

    @Override
    public void viewReady() {
        if (!isViewAttached()) {
            errors.onNext(new IllegalStateException("viewReady() was called, but view is not attached!"));
            return;
        }
        readData();
    }

//    @Override
//    public void onStart() {
//        Disposable d = ImageRepository.instance().populateWithImages(Observable.fromIterable(personList))
//                // As soon as image is loaded for a person, update corresponding item in view's list
//                .subscribe(getView()::updatePerson, errors::onNext, getView()::hideProgressOverlay);
//        disposables.add(d);
//    }

    private void readData() {
        final PersonDataSource personDataSource = new PersonDataSource();
        final ImageRepository imageRepo = ImageRepository.instance();
        try {

            // Read json from asset file
            String json = TextUtils.readStringFromStream(getView().openAssetFile(Constants.DATA_ASSET_FILENAME));
            if (json.isEmpty()) {
                errors.onNext(new IllegalArgumentException("Json file is empty!"));
                return;
            }

            // Parse all people from json file
            Observable<Person> personObservable = personDataSource.parseData(json);

            // Add every next person to view's recyclerView,
            // if error occurs pass it to errors observable
            Disposable d = personObservable.subscribe(getView()::addPerson, errors::onNext);
            disposables.add(d);

//            d = personObservable.toList().subscribe(list -> personList = list);
//            disposables.add(d);

            // Immediately request images to be loaded
            d = imageRepo.populateWithImages(personObservable)
                    // As soon as image is loaded for a person, update corresponding item in view's list
                    .subscribe(getView()::updatePerson, errors::onNext);
            disposables.add(d);


        } catch (IOException e) {
            e.fillInStackTrace();
        } finally {
            personDataSource.dispose();
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
    }
}
