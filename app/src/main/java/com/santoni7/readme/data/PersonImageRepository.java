package com.santoni7.readme.data;

import com.santoni7.readme.async.ImageDownloadAsyncTask;
import com.santoni7.readme.async.WikipediaFileUrlAsyncTask;
import com.santoni7.readme.util.TextUtils;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.ReplaySubject;

/**
 * A singleton class which is responsible for getting real image URLs
 * (instead of Wikipedia file page), downloading and storing them.
 */
public class PersonImageRepository implements Disposable {
    private static PersonImageRepository _instance = null;
    public static PersonImageRepository instance() {
        if (_instance == null) {
            _instance = new PersonImageRepository();
        }
        return _instance;
    }


    // CompositeDisposable of all subscriptions, in order to dispose them (in dispose() method)
    private CompositeDisposable subscriptions = new CompositeDisposable();


    private ReplaySubject<ImageData> imageDataSubject = ReplaySubject.create();
    private ReplaySubject<Throwable> errorSubject = ReplaySubject.create();

    private ReplaySubject<Person> personWithImageSource = ReplaySubject.create();

    // Number of people, passed to 'populateWithImages' method
    private long totalExpected = Integer.MAX_VALUE;
    // Number of people, for whom image is ready
    private long totalProcessed = 0;


    // Private constructor in a singleton class
    private PersonImageRepository() {
        // As soon as image url is ready, start loading it.
        // After loaded, add it to personWithImageSource
        Disposable d = imageDataSubject
                .flatMap(this::startLoadingBitmap)
                .subscribe(personWithImageSource::onNext, errorSubject::onNext);

        subscriptions.add(d);
    }

    /**
     * Starts new ImageDownloadAsyncTask and immediately returns observable result
     */
    private Observable<Person> startLoadingBitmap(ImageData imageData) {
        ImageDownloadAsyncTask task = new ImageDownloadAsyncTask();
        task.execute(imageData.url);

        Person p = imageData.owner;
        p.setAvatarUrl(imageData.url);
        p.setImageFileName(imageData.fileName);
        p.setImageSource(task.getResultSource());

        return Observable.just(p);
    }

    /**
     * For every person in sequence, load and save an image from specified avatar url
     * @param personObservable sequence of people to load images for
     * @return sequence of people, with images ready
     */
    public Observable<Person> populateWithImages(Observable<Person> personObservable) {
        Disposable d = personObservable.subscribe(this::nextPerson, errorSubject::onNext);
        subscriptions.add(d);

        // Save total number of people requested
        d = personObservable.count().subscribe(i -> totalExpected = i);
        subscriptions.add(d);

        // Count every next processed person,
        // if their number equals to total number, send onComplete event
        d = personWithImageSource.subscribe(p -> {
            totalProcessed++;
            if(totalProcessed == totalExpected){
                personWithImageSource.onComplete();
            }
        });

        subscriptions.add(d);
        return personWithImageSource;
    }



    /**
     * Process next person in sequence:
     * Check if Person's Avatar URL points to Wikipedia File Page instead of plain image:
     * If so, real file URL is fetched from Wikipedia API, else it remains unmodified.
     * <p>
     * After that, ImageData object is passed to imageDataSubject
     * (see constructor for further actions).
     */
    private void nextPerson(Person person) {
        String avatarUrl = person.getAvatarUrl();
        Pattern p = Pattern.compile("https?://.+\\.wikipedia.org/wiki/File:(.+)");
        Matcher m = p.matcher(avatarUrl);
        if (m.matches()) {
            MatchResult res = m.toMatchResult();
            final String wikiFile = res.group(1); // Name of file at WikiMedia, used in API-call

            // Start AsyncTask to get final image url from Wiki API
            WikipediaFileUrlAsyncTask task = new WikipediaFileUrlAsyncTask();

            Disposable d = task.getResultSource()
                    .map(url -> new ImageData(url, wikiFile, person))
                    .subscribe(imageDataSubject::onNext,
                            getErrorSubject()::onNext);

            subscriptions.add(d);

            task.execute(wikiFile);
        } else {
            imageDataSubject.onNext(new ImageData(avatarUrl, TextUtils.getFileNameFromURL(avatarUrl), person));
        }
    }

    public Single<Person> findPersonById(String personId) {
        return personWithImageSource.filter(p -> p.getId().equals(personId)).firstOrError();
    }

    @Override
    public void dispose() {
        subscriptions.clear();
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    public ReplaySubject<Throwable> getErrorSubject() {
        return errorSubject;
    }

    public ReplaySubject<Person> getPersonWithImageSource() {
        return personWithImageSource;
    }


    public static class ImageData {
        String url;
        String fileName;
        Person owner;

        ImageData(String url, String fileName, Person owner) {
            this.url = url;
            this.fileName = fileName;
            this.owner = owner;
        }
    }
}
