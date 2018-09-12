package com.santoni7.readme.activity.details;

import com.santoni7.readme.common.PresenterBase;
import com.santoni7.readme.data.ImageRepository;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.ReplaySubject;

public class DetailsPresenter extends PresenterBase<DetailsContract.View> implements DetailsContract.Presenter {
    private CompositeDisposable disposables = new CompositeDisposable();

    private ReplaySubject<Throwable> errors = ReplaySubject.create();
    private ImageRepository repository = ImageRepository.instance();

    @Override
    public void viewReady() {
        Disposable d = getView().getPersonIdExtra()
                .flatMap(repository::findPersonById)
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
