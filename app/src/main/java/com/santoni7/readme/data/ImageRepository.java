package com.santoni7.readme.data;

import android.util.Pair;

import com.santoni7.readme.async.WikipediaFileUrlAsyncTask;

import java.util.HashMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

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

    /**
     * A map of Person's avatar file URL, after getting it from Wikipedia API
     * Key: Person ID;  Value: Real avatar URL
     */
    private HashMap<String, String> fileUrlByPerson = new HashMap<>();

    /**
     * A source of pair Person-Avatar URL as Avatar URL is gotten from WikiApi
     * Pair first: Person ID; Pair second: Avatar URL
     */
    private ReplaySubject<Pair<Person, String /* Avatar URL */>> realUrlSource = ReplaySubject.create();

    private ImageRepository() {
        Disposable d = realUrlSource.subscribe(pair ->
                // Save
                fileUrlByPerson.put(pair.first.getId(), pair.second)
        );
        subscriptions.add(d);
    }


    public String getImageUrl(String personID) {
        return fileUrlByPerson.get(personID);
    }

    /**
     * Check if Person's Avatar URL points to Wikimedia Page instead of plain image:
     * If so, real file URL is fetched from Wikipedia API and `realUrlSource` observable gets
     * notified about result;
     * Else, if URL is already a file URL, it gets to `realUrlSource` unmodified
     *
     * @param person
     */
    public void fetchPersonImage(final Person person) {
        String avatarUrl = person.getAvatarUrl();
        Pattern p = Pattern.compile("https?://.+\\.wikipedia.org/wiki/File:(.+)");
        Matcher m = p.matcher(avatarUrl);
        if (m.matches()) {
            MatchResult res = m.toMatchResult();
            String wikiFile = res.group(1); // Name of file at WikiMedia, used in API-call

            // Start AsyncTask to get final image url from Wiki API
            new WikipediaFileUrlAsyncTask(new WikipediaFileUrlAsyncTask.ResultListener() {
                @Override
                public void onSuccess(String fileURL) {
                    realUrlSource.onNext(new Pair<>(person, fileURL));
                }

                @Override
                public void onError() {
                    realUrlSource.onNext(new Pair<>(person, "ERROR"));
                    //todo error reporting
                }
            }).execute(wikiFile);

        } else {
            realUrlSource.onNext(new Pair<>(person, avatarUrl));
        }
    }


    public ReplaySubject<Pair<Person, String>> getRealUrlSource() {
        return realUrlSource;
    }

    @Override
    public void dispose() {
        subscriptions.clear();
    }

    @Override
    public boolean isDisposed() {
        return false;
    }
}
