package com.santoni7.readme.data;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface ImageRepository {
    /**
     * Populate every Person from Observable with avatar
     * bitmap (from local, or if not available, remote source)
     *
     * @return Observable with all of successfully populated Person objects
     * Note: order in returning sequence may be different than in source sequence
     */
    Observable<Person> populateWithImages(Observable<Person> people);

    Single<Person> findPersonById(String id);

    void dispose();

}
