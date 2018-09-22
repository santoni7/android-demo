package com.santoni7.readme.data.datasource;

import android.util.Log;

import com.santoni7.readme.data.Person;
import com.santoni7.readme.util.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class PersonDataSource {
    private static String TAG = PersonDataSource.class.getSimpleName();

    public PersonDataSource() {
    }

    /**
     * Parse a sequence of Person objects from json string
     */
    public Observable<Person> parsePeople(InputStream inputStream){
        return Observable.<Person>create(emitter -> {
            try {
                String json = TextUtils.readStringFromStream(inputStream);
                JSONObject employees = new JSONObject(json).getJSONObject("employees");
                JSONArray ids = employees.names();

                for (int i = 0; i < ids.length(); ++i) {
                    String id = ids.getString(i);
                    JSONObject personObj = employees.getJSONObject(id);
                    Person person = jsonToPerson(id, personObj);
                    person.setImageFileName(TextUtils.getImageFileName(person.getAvatarUrl()));

                    emitter.onNext(person);
                    Log.d(TAG, "Next person parsed and passed to observable: " + person.toString());
                }
                emitter.onComplete();
            } catch (JSONException | IOException e) {
                emitter.onError(e);
                Log.e(TAG, "Error occured in parsePeople: " + e.toString());
                e.fillInStackTrace();
            } finally {
                inputStream.close();
            }
        }).subscribeOn(Schedulers.io())
                .replay()
                .autoConnect();
    }

    private static Person jsonToPerson(String id, JSONObject personObject) throws JSONException {
        Person p = new Person();
        p.setId(id);
        p.setFirstName(personObject.getString("firstName"));
        p.setSecondName(personObject.getString("secondName"));
        p.setAge(personObject.getInt("age"));
        p.setAvatarUrl(personObject.getString("avatar"));
        return p;
    }
}
