package com.santoni7.readme.activity.main;

import com.santoni7.readme.common.MvpPresenter;
import com.santoni7.readme.common.MvpView;
import com.santoni7.readme.data.Person;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface MainContract {
    interface View extends MvpView {
        void showProgressOverlay();

        void hideProgressOverlay();

        void displayPersonList(List<Person> people);

        void addPerson(Person person);

        void updatePerson(Person person);

        void openDetailsScreen(Person person);

        //TODO: Remove, used for debug only
        void makeToast(String text);

        InputStream openAssetFile(String fileName) throws IOException;
    }

    interface Presenter extends MvpPresenter<View> {
        void onListItemClicked(Person p);
    }
}
