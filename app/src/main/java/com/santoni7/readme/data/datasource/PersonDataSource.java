package com.santoni7.readme.data.datasource;

import com.santoni7.readme.data.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.ReplaySubject;

public class PersonDataSource implements Disposable {
    private static String TAG = PersonDataSource.class.getSimpleName();

    public PersonDataSource() {
    }

    private boolean isDisposed = false;
    private CompositeDisposable disposables = new CompositeDisposable();

    private ReplaySubject<Person> personSubject = ReplaySubject.create();
    private ReplaySubject<Throwable> errors = ReplaySubject.create();


    /**
     * Parse a sequence of Person objects from json string
     * @return
     */
    public Observable<Person> parseData(String json){
        if (personSubject != null) {
            personSubject.cleanupBuffer();
            disposables.clear();
        }
        else {
            personSubject = ReplaySubject.create();
        }
        Observable<Person> source = Observable.create(emitter -> {
            try {
                JSONObject employees = new JSONObject(json).getJSONObject("employees");
                JSONArray ids = employees.names();

                for (int i = 0; i < ids.length(); ++i) {
                    String id = ids.getString(i);
                    JSONObject personObj = employees.getJSONObject(id);
                    Person person = jsonToPerson(id, personObj);

                    emitter.onNext(person);
                }
                emitter.onComplete();
            } catch (JSONException e) {
                emitter.onError(e);
            }
        });
        source.subscribe(personSubject);
        return source;
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
        disposables.clear();
        isDisposed = true;
        personSubject.cleanupBuffer();
        errors.cleanupBuffer();
        personSubject = null;
        errors = null;
    }

    @Override
    public boolean isDisposed() {
        return isDisposed;
    }
}
