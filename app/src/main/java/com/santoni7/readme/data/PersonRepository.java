package com.santoni7.readme.data;

import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.ReplaySubject;

public class PersonRepository implements Disposable {
    private static String TAG = PersonRepository.class.getSimpleName();
    private static PersonRepository _instance = null;

    public static PersonRepository instance() {
        if (_instance == null) {
            _instance = new PersonRepository();
        }
        return _instance;
    }

    private PersonRepository() {
    }

    private boolean isDisposed = false;
    private CompositeDisposable personSubjectSubscriptions = new CompositeDisposable();

    private List<Person> personList = new ArrayList<>();
    private ReplaySubject<Person> personSubject;

    public List<Person> getPersonList() {
        return personList;
    }

//    public Person findPersonById(String personId) {
//        for (Person p : personList) {
//            if (personId.equals(p.getId())) {
//                return p;
//            }
//        }
//        return null;
//    }

    public Single<Person> findPersonById(String personId) {
        return personSubject.filter(p -> p.getId().equals(personId)).firstOrError();
    }


    public Observable<Person> updateData(String json) {
        if (personSubject != null) {
            personSubject.cleanupBuffer();
            personSubjectSubscriptions.clear();
        }
        else {
            personSubject = ReplaySubject.create();
        }

        Disposable d = personSubject.subscribe(
                person -> personList.add(person),
                e -> Log.d(TAG, "PersonSubject error: " + e)
        );
        personSubjectSubscriptions.add(d);

        Handler h = new Handler();
        h.post(
                () -> parseData(json, personSubject)
        );

        return personSubject;
    }


    private void parseData(String json, ReplaySubject<Person> target) {
        try {
            JSONObject employees = new JSONObject(json).getJSONObject("employees");
            JSONArray ids = employees.names();

            for (int i = 0; i < ids.length(); ++i) {
                String id = ids.getString(i);
                JSONObject personObj = employees.getJSONObject(id);
                Person person = PersonJsonParser.jsonToPerson(id, personObj);

                target.onNext(person);
            }
            target.onComplete();
        } catch (JSONException e) {
            target.onError(e);
        }
    }


    @Override
    public void dispose() {
        personSubjectSubscriptions.clear();
        isDisposed = true;
        personList = null;
    }

    @Override
    public boolean isDisposed() {
        return isDisposed;
    }
}
