package com.santoni7.readme.data.datasource;

import com.santoni7.readme.data.Person;

import io.reactivex.Observable;

public interface ImageDataSource {
    Observable<Person> populateWithImages(Observable<Person> personObservable);

    Observable<Person> getFailedObjects();

    void dispose();
}
