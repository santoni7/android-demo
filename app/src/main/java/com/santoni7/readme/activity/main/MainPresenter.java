package com.santoni7.readme.activity.main;

import com.santoni7.readme.common.PresenterBase;
import com.santoni7.readme.data.ImageRepository;
import com.santoni7.readme.data.Person;
import com.santoni7.readme.data.PersonRepository;
import com.santoni7.readme.util.TextUtils;

import java.io.IOException;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DefaultObserver;

public class MainPresenter extends PresenterBase<MainContract.View> implements MainContract.Presenter {
    private static final String TAG = MainPresenter.class.getSimpleName();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void viewReady() {
        getView().showProgressOverlay();
        readData();
        getView().hideProgressOverlay();
    }

    private List<Person> people;

    private void readData() {
        // todo check if view is attached
        final PersonRepository personRepo = PersonRepository.instance();
        final ImageRepository imageRepo = ImageRepository.instance();
        try {
            String json = TextUtils.readStringFromStream(getView().openAssetFile("data.json"));
            if (json.isEmpty()) return;

            personRepo.updateData(json, new DefaultObserver<Person>() {
                @Override
                public void onNext(Person person) {
                    imageRepo.addPersonImage(person);
                    getView().addPerson(person);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
                    people = personRepo.getPersonList();
                }
            });

            Disposable d = imageRepo.getFixedUrlSubject()
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
        getView().showSnackbar(p.getFirstName());
    }
}
