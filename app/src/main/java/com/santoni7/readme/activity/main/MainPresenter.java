package com.santoni7.readme.activity.main;

import android.util.Log;

import com.santoni7.readme.async.WikipediaFileUrlAsyncTask;
import com.santoni7.readme.common.PresenterBase;
import com.santoni7.readme.data.ImageRepository;
import com.santoni7.readme.data.Person;
import com.santoni7.readme.data.PersonJsonParser;
import com.santoni7.readme.util.Logger;
import com.santoni7.readme.util.TextUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MainPresenter extends PresenterBase<MainContract.View> implements MainContract.Presenter {
    private static final String TAG = MainPresenter.class.getSimpleName();
    @Override
    public void viewReady() {
        getView().showProgressOverlay();
        readData();
        getView().hideProgressOverlay();
    }

    private void readData(){
        // todo check if view is attached
        try {
            String json = TextUtils.readStringFromStream(getView().openAssetFile("data.json"));
            List<Person> people = PersonJsonParser.parseEmployees(json);

            getView().displayPersonList(people);
        }
        catch (IOException | JSONException e){
            e.fillInStackTrace();
        }
    }

    @Override
    public void onListItemClicked(Person p) {
        getView().showSnackbar(p.getFirstName());
    }
}
