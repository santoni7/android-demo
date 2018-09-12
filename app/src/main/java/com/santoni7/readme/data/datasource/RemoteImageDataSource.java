package com.santoni7.readme.data.datasource;

import com.santoni7.readme.async.ImageDownloadAsyncTask;
import com.santoni7.readme.async.WikipediaFileUrlAsyncTask;
import com.santoni7.readme.data.ImageData;
import com.santoni7.readme.data.Person;
import com.santoni7.readme.util.TextUtils;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.ReplaySubject;

public class RemoteImageDataSource {


    private CompositeDisposable disposables = new CompositeDisposable();

    private ReplaySubject<ImageData> imageDataSubject = ReplaySubject.create();
    private ReplaySubject<Person> personWithImageSubject = ReplaySubject.create();
    private ReplaySubject<Throwable> errorSubject = ReplaySubject.create();

    public RemoteImageDataSource() {
        // As soon as image url is ready, start loading it.
        // After loaded, add it to personWithImageSubject
        Disposable d = imageDataSubject
                .flatMap(this::startLoadingBitmap)
                .subscribe(personWithImageSubject::onNext, errorSubject::onNext);

        disposables.add(d);
    }

    /**
     * For every person in sequence, load and save an image from specified avatar url
     * @param personObservable sequence of people to load images for
     * @return sequence of people, with images ready
     */
    public Observable<Person> populateWithImages(Observable<Person> personObservable){
        Disposable d = personObservable.subscribe(this::fetchImageData, errorSubject::onNext);
        disposables.add(d);
        return personWithImageSubject;
    }

    /**
     * Starts new ImageDownloadAsyncTask and immediately returns observable result
     */
    Observable<Person> startLoadingBitmap(ImageData imageData) {
        ImageDownloadAsyncTask task = new ImageDownloadAsyncTask();
        task.execute(imageData.getUrl());

        Person p = imageData.getOwner();
        p.setAvatarUrl(imageData.getUrl());
        p.setImageFileName(imageData.getFileName());
        p.setImageSource(task.getResultSource());

//        Disposable d = task.getResultSource().subscribe(bmp -> savedImages.add(bmp));
//        disposables.add(d);

        return Observable.just(p);
    }



    /**
     * Process next person in sequence:
     * Check if Person's Avatar URL points to Wikipedia File Page instead of plain image:
     * If so, real file URL is fetched from Wikipedia API, else it remains unmodified.
     * <p>
     * After that, ImageData object is passed to imageDataSubject
     * (see constructor for further actions).
     */
    void fetchImageData(Person person) {
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
                            errorSubject::onNext);

            disposables.add(d);

            task.execute(wikiFile);
        } else {
            imageDataSubject.onNext(new ImageData(avatarUrl, TextUtils.getFileNameFromURL(avatarUrl), person));
        }
    }
}