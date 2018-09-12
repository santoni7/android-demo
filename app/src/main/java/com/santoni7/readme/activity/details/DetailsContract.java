package com.santoni7.readme.activity.details;

import com.santoni7.readme.common.MvpPresenter;
import com.santoni7.readme.common.MvpView;
import com.santoni7.readme.data.Person;

import io.reactivex.Single;

public interface DetailsContract {
    interface View extends MvpView {
        Single<String> getPersonIdExtra();

        void finish();

        void displayPerson(Person person);
    }
    interface Presenter extends MvpPresenter<View>{
        void onStop();
        void onDestroy();
    }
}
