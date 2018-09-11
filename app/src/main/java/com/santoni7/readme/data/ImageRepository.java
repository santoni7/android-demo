package com.santoni7.readme.data;

import com.santoni7.readme.async.ImageDownloadAsyncTask;
import com.santoni7.readme.async.WikipediaFileUrlAsyncTask;
import com.santoni7.readme.util.TextUtils;

import java.util.HashMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.ReplaySubject;

/**
 * A singleton class which is responsible for getting real image URL (instead of Wikipedia file page),
 * and saving it to device.
 */
public class ImageRepository implements Disposable {
    private static ImageRepository _instance = null;

    public static ImageRepository instance() {
        if (_instance == null) {
            _instance = new ImageRepository();
        }
        return _instance;
    }

    /**
     * CompositeDisposable of all subscriptions, in order to dispose them (in dispose() method)
     */
    private CompositeDisposable subscriptions = new CompositeDisposable();

//    /**
//     * A map of Person's avatar file URL, after getting it from Wikipedia API
//     * Key: Person ID;  Value: Real avatar URL
//     */
//    private HashMap<String /*Person ID*/, String /*Avatar URL*/> fileUrlByPerson = new HashMap<>();

    /**
     * A source of pair Person-Avatar URL as Avatar URL is gotten from WikiApi
     */
//    private ReplaySubject<Pair<Person, String /* Avatar URL */>> realUrlSource = ReplaySubject.create();

    private ReplaySubject<ImageData> imageDataSource = ReplaySubject.create();
    private ReplaySubject<Throwable> errorSource = ReplaySubject.create();

    private ReplaySubject<PersonWithImage> personWithImageSource = ReplaySubject.create();


    private ImageRepository() {

        Disposable d = imageDataSource
                .flatMap(this::loadBitmap)
                .subscribe(personWithImageSource::onNext, personWithImageSource::onError);
        subscriptions.add(d);
    }

    private Observable<PersonWithImage> loadBitmap(ImageData imageData) {
        ImageDownloadAsyncTask task = new ImageDownloadAsyncTask();
        task.execute(imageData.url);

        return Observable.just(new PersonWithImage(imageData, task.getResultSource()));
    }


//    public String getImageUrl(String personID) {
//        return fileUrlByPerson.get(personID);
//    }

    /**
     * Check if Person's Avatar URL points to Wikimedia Page instead of plain image:
     * If so, real file URL is fetched from Wikipedia API, else it remains unmodified.
     *
     * Then, image is downloaded and stored in PersonWithImage objects.
     * Else, if URL is already a file URL, it gets to `realUrlSource` unmodified
     */
    public void fetchPersonImage(final Person person) {
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
                    .subscribe(imageDataSource::onNext,
                            errorSource::onNext);

            subscriptions.add(d);

            task.execute(wikiFile);
        } else {
            imageDataSource.onNext(new ImageData(avatarUrl, TextUtils.getFileNameFromURL(avatarUrl), person));
        }
    }


//    public ReplaySubject<Pair<Person, String>> getRealUrlSource() {
//        return realUrlSource;
//    }

    @Override
    public void dispose() {
        subscriptions.clear();
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    public static class ImageData{
        String url;
        String fileName;
        Person owner;

        ImageData(String url, String fileName, Person owner){
            this.url = url;
            this.fileName = fileName;
            this.owner = owner;
        }
    }
}
