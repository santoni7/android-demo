package com.santoni7.readme.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PersonJsonParser {
    public static List<Person> parseEmployees(String json) throws JSONException {
        JSONObject employees = new JSONObject(json).getJSONObject("employees");
        JSONArray ids = employees.names();

        List<Person> people = new ArrayList<>(ids.length());

        for (int i = 0; i < ids.length(); ++i) {
            String id = ids.getString(i);
            JSONObject personObj = employees.getJSONObject(id);
            Person person = jsonToPerson(id, personObj);

            people.add(person);
        }

        return people;
    }

    public static Person jsonToPerson(String id, JSONObject personObject) throws JSONException {
        Person p = new Person();
        p.setId(id);
        p.setFirstName(personObject.getString("firstName"));
        p.setSecondName(personObject.getString("secondName"));
        p.setAge(personObject.getInt("age"));
        p.setAvatarUrl(personObject.getString("avatar"));
        return p;
    }
}
