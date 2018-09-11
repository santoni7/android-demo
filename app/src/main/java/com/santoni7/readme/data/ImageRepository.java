package com.santoni7.readme.data;

import com.santoni7.readme.async.WikipediaFileUrlAsyncTask;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.observers.DefaultObserver;

public class ImageRepository {
    private static ImageRepository _instance = null;
    public static ImageRepository instance(){
        if(_instance == null){
            _instance = new ImageRepository();
        }
        return _instance;
    }

    private ImageRepository(){}

//    public Observable<String> fixUrl(String remoteUrl) {
//        // 1. If already exists, skip
//        // 2. Check if wikimedia file page
//        Pattern p = Pattern.compile("https?://.+\\.wikipedia.org/wiki/File:(.+)");
//        Matcher m = p.matcher(remoteUrl);
//        if (m.matches()) {
//            MatchResult res = m.toMatchResult();
//            String wikiFile = res.group(1);
//            // Start AsyncTask to get final image url
//            new WikipediaFileUrlAsyncTask(new DefaultObserver<String>() {
//                @Override
//                public void onNext(String s) {
//                    Observable.just(s);
//                }
//
//                @Override
//                public void onError(Throwable e) {
//
//                }
//
//                @Override
//                public void onComplete() {
//
//                }
//            }).execute(wikiFile);
//
//        } else {
//            return Observable.just(remoteUrl);
//        }
//    }
//
    public void addPersonImage(Person person) {
//        Observable<String> fixedUrl = fixUrl(person.getAvatarUrl());
    }
}
