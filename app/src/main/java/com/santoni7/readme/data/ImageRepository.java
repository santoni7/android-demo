package com.santoni7.readme.data;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface ImageRepository {

    /**
     * Populate every Person from Observable with avatar using specified strategy
     */
    Observable<Person> populateWithImages(Observable<Person> people, SourceStrategy sourceStrategy);


    /**
     * Find a person with specified id in a last result of populateWithImages(...)
     */
    Single<Person> findPersonById(String id);

    /**
     * Dispose all resources and Rx subscriptions
     */
    void dispose();

    /**
     * How to look for images: at remote source first, and then, if unsuccessfully, in local,
     * or vice versa.
     */
    enum SourceStrategy{
        RemoteFirst, LocalFirst
    }
}
