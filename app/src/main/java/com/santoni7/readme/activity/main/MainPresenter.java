package com.santoni7.readme.activity.main;

import android.util.Log;

import com.santoni7.readme.common.PresenterBase;
import com.santoni7.readme.data.ImageRepository;
import com.santoni7.readme.data.Person;
import com.santoni7.readme.data.PersonRepository;
import com.santoni7.readme.util.TextUtils;

import java.io.IOException;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainPresenter extends PresenterBase<MainContract.View> implements MainContract.Presenter {
    private static final String TAG = MainPresenter.class.getSimpleName();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void viewReady() {
        getView().showProgressOverlay();
        readData();
        getView().hideProgressOverlay();
    }


    private void readData() {
        if(!isViewAttached())
            return;
        final PersonRepository personRepo = PersonRepository.instance();
        final ImageRepository imageRepo = ImageRepository.instance();
        try {
            getView().showProgressOverlay();
            String json = TextUtils.readStringFromStream(getView().openAssetFile("data.json"));
            if (json.isEmpty()) {
                getView().hideProgressOverlay();
                return; //todo error reporting
            }

            Disposable d = personRepo.updateData(json).subscribe(
                    // onNext
                    person -> {
                        imageRepo.fetchPersonImage(person);
                        getView().addPerson(person);
                    },
                    // onError
                    err -> Log.e(TAG, "Error while parsing data: " + err),
                    // onComplete
                    () -> {
                        getView().hideProgressOverlay();
                    }
            );
            compositeDisposable.add(d);

            d = imageRepo.getRealUrlSource()
                    .subscribe(personStringPair ->
                            getView().updatePerson(personStringPair.first)
                    );
            compositeDisposable.add(d);

        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onListItemClicked(Person p) {
        getView().openDetailsScreen(p);
    }
}
