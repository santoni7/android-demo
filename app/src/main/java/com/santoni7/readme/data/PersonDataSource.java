package com.santoni7.readme.data;

import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.ReplaySubject;

public class PersonDataSource implements Disposable {
    private static String TAG = PersonDataSource.class.getSimpleName();

    public PersonDataSource() {
    }

    private boolean isDisposed = false;
    private CompositeDisposable personSubjectSubscriptions = new CompositeDisposable();

    private ReplaySubject<Person> personSubject = ReplaySubject.create();
    private ReplaySubject<Throwable> errorSubject = ReplaySubject.create();

    public Observable<Person> parseData(String json) {
        if (personSubject != null) {
            personSubject.cleanupBuffer();
            personSubjectSubscriptions.clear();
        }
        else {
            personSubject = ReplaySubject.create();
        }

        Handler h = new Handler();
        h.post(() -> parseData(json, personSubject));

        return personSubject;
    }


    /**
     * Parse people from json, sending result one by one to target subject
     * @param json Json to parse people data from
     * @param target Target subject, which will receive every person parsed
     */
    private void parseData(String json, @NonNull ReplaySubject<Person> target) {
        try {
            JSONObject employees = new JSONObject(json).getJSONObject("employees");
            JSONArray ids = employees.names();

            for (int i = 0; i < ids.length(); ++i) {
                String id = ids.getString(i);
                JSONObject personObj = employees.getJSONObject(id);
                Person person = jsonToPerson(id, personObj);

                target.onNext(person);
            }
            target.onComplete();
        } catch (JSONException e) {
            target.onError(e);
        }
    }

    private Person jsonToPerson(String id, JSONObject personObject) throws JSONException {
        Person p = new Person();
        p.setId(id);
        p.setFirstName(personObject.getString("firstName"));
        p.setSecondName(personObject.getString("secondName"));
        p.setAge(personObject.getInt("age"));
        p.setAvatarUrl(personObject.getString("avatar"));
        return p;
    }

    @Override
    public void dispose() {
        personSubjectSubscriptions.clear();
        isDisposed = true;
        personSubject.cleanupBuffer();
        errorSubject.cleanupBuffer();
        personSubject = null;
        errorSubject = null;
    }

    @Override
    public boolean isDisposed() {
        return isDisposed;
    }
}
