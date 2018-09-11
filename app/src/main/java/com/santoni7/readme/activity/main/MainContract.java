package com.santoni7.readme.activity.main;

import com.santoni7.readme.common.MvpPresenter;
import com.santoni7.readme.common.MvpView;
import com.santoni7.readme.data.Person;

import java.io.IOException;
import java.io.InputStream;

public interface MainContract {
    interface View extends MvpView {
        void showProgressOverlay();

        void hideProgressOverlay();

        void addPerson(Person person);

        void updatePerson(Person person);

        void openDetailsScreen(Person person);

        InputStream openAssetFile(String fileName) throws IOException;
    }

    interface Presenter extends MvpPresenter<View> {
        void onListItemClicked(Person p);
        void onDestroy();
    }
}
