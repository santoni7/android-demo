package com.santoni7.readme.data;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageRepository {

    public void addImage(String remoteUrl) {
        // 1. If already exists, skip
        // 2. Check if wikimedia file page
        Pattern p = Pattern.compile("https?://.+\\.wikipedia.org/wiki/File:(.+)");
        Matcher m = p.matcher(remoteUrl);
        if (m.matches()) {
            MatchResult res = m.toMatchResult();
            String wikiFile = res.group(1);
            // Start AsyncTask to get final image url
        } else {
            // Load image

        }
    }
}
