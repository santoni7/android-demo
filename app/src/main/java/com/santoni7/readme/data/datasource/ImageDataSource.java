package com.santoni7.readme.data.datasource;

import com.santoni7.readme.data.Person;

import io.reactivex.Observable;

public interface ImageDataSource {
    Observable<Person> populateWithImages(Observable<Person> people);

    Observable<Person> getFailedObjects();

    void savePersonImages(Observable<Person> people);

    void dispose();
}
