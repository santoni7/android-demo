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

public class ImageRepository {
    private static ImageRepository _instance = null;

    public static ImageRepository instance() {
        if (_instance == null) {
            _instance = new ImageRepository();
        }
        return _instance;
    }

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private HashMap<String, String> fileUrlByPerson = new HashMap<>();

    private ReplaySubject<Pair<Person, String>> fixedUrlSubject = ReplaySubject.create();

    private ImageRepository() {
        Disposable d = fixedUrlSubject.subscribe(pair ->
                fileUrlByPerson.put(pair.first.getId(), pair.second)
        );
        compositeDisposable.add(d);
    }


    public String getImageUrl(String personID) {
        return fileUrlByPerson.get(personID);
    }

    public void addPersonImage(final Person person) {
        String avatarUrl = person.getAvatarUrl();
        Pattern p = Pattern.compile("https?://.+\\.wikipedia.org/wiki/File:(.+)");
        Matcher m = p.matcher(avatarUrl);
        if (m.matches()) {
            MatchResult res = m.toMatchResult();
            String wikiFile = res.group(1);
            // Start AsyncTask to get final image url
            new WikipediaFileUrlAsyncTask(new WikipediaFileUrlAsyncTask.ResultListener() {
                @Override
                public void onSuccess(String fileURL) {
                    fixedUrlSubject.onNext(new Pair<>(person, fileURL));
                }

                @Override
                public void onError() {
                    fixedUrlSubject.onNext(new Pair<>(person, "ERROR"));
                }
            }).execute(wikiFile);

        } else {
            fixedUrlSubject.onNext(new Pair<>(person, avatarUrl));
        }
    }


    public Subject<Pair<Person, String>> getFixedUrlSubject() {
        return fixedUrlSubject;
    }
}
