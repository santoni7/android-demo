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
import io.reactivex.observers.DefaultObserver;
import io.reactivex.subjects.ReplaySubject;

public class PersonRepository {
    private static String TAG = PersonRepository.class.getSimpleName();
    private static PersonRepository _instance = null;
    public static PersonRepository instance(){
        if(_instance == null){
            _instance = new PersonRepository();
        }
        return _instance;
    }
    private PersonRepository() {    }

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private List<Person> personList = new ArrayList<>();

    public void updateData(String json, @Nullable Observer<Person> observer){
        ReplaySubject<Person> personSubject = ReplaySubject.create();
        Observer<Person> personSubjectObserver = new DefaultObserver<Person>() {
            @Override
            public void onNext(Person person) {
                Log.d(TAG, "personSubjectObserver.onNext: person.id=" + person.getId());
                personList.add(person);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "personSubjectObserver.onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "personSubjectObserver.onComplete");
            }
        };


        personSubject.subscribe(personSubjectObserver);
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

    public void onDestroy(){
        compositeDisposable.clear();
    }



}
