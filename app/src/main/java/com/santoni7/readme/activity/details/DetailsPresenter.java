package com.santoni7.readme.activity.details;

import com.santoni7.readme.common.PresenterBase;
import com.santoni7.readme.dagger.MyComponent;
import com.santoni7.readme.data.ImageRepository;
import com.santoni7.readme.data.ImageRepositoryImpl;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.ReplaySubject;

public class DetailsPresenter extends PresenterBase<DetailsContract.View> implements DetailsContract.Presenter {
    private CompositeDisposable disposables = new CompositeDisposable();

    private ReplaySubject<Throwable> errors = ReplaySubject.create();

    @Inject ImageRepository imageRepository;

    @Override
    public void init(MyComponent component) {
        component.inject(this);
    }

    @Override
    public void viewReady() {
        if(imageRepository == null){
            throw new IllegalStateException("Presenter must be initialized at first!");
        }

        Disposable d = getView().getPersonIdExtra()
                .flatMap(imageRepository::findPersonById)
                .subscribe(getView()::displayPerson, e -> {
                    errors.onNext(e);
                    getView().finish();
                });
        disposables.add(d);
    }

    @Override
    public void onStop() {
        disposables.clear();
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        errors.cleanupBuffer();
    }
}
