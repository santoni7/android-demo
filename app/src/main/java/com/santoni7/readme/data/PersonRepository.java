package com.santoni7.readme.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.subjects.ReplaySubject;

public class PersonRepository implements Disposable {
    private static String TAG = PersonRepository.class.getSimpleName();
    private static PersonRepository _instance = null;
    public static PersonRepository instance(){
        if(_instance == null){
            _instance = new PersonRepository();
        }
        return _instance;
    }
    private PersonRepository() {    }

    private boolean isDisposed = false;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private List<Person> personList;

    public void updateData(String json, @Nullable Observer<Person> observer){
        personList = new ArrayList<>();
        isDisposed = false;
        ReplaySubject<Person> personSubject = ReplaySubject.create();

        Disposable d = personSubject.subscribe(
                person -> personList.add(person),
                e -> Log.d(TAG, "PersonSubject error: " + e)
        );
        compositeDisposable.add(d);

        if(observer != null){
            personSubject.subscribe(observer);
        }

        try {
            JSONObject employees = new JSONObject(json).getJSONObject("employees");
            JSONArray ids = employees.names();

            for (int i = 0; i < ids.length(); ++i) {
                String id = ids.getString(i);
                JSONObject personObj = employees.getJSONObject(id);
                Person person = PersonJsonParser.jsonToPerson(id, personObj);

                personSubject.onNext(person);
            }
            personSubject.onComplete();
        } catch (JSONException e) {
            personSubject.onError(e);
        }

    }

    public List<Person> getPersonList() {
        return personList;
    }



    @Override
    public void dispose() {
        isDisposed = true;
        compositeDisposable.clear();
        personList = null;
    }

    @Override
    public boolean isDisposed() {
        return isDisposed;
    }
}
