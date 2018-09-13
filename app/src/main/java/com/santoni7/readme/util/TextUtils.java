package com.santoni7.readme.util;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class with static utility methods, related with text processing
 */
public class TextUtils {

    private static final String REGEX_WIKIPEDIA_FILE = "https?://.+\\.wikipedia.org/wiki/File:(.+)";

    public static String readStringFromStream(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        try (InputStreamReader isr = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(isr)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
    }

    public static String getImageFileName(String url) {
        Pattern p = Pattern.compile(REGEX_WIKIPEDIA_FILE);
        Matcher m = p.matcher(url);
        if (m.matches()) { // Url points to Wiki file
            MatchResult res = m.toMatchResult();
            return res.group(1);
        } else {
            return Uri.parse(url).getLastPathSegment();
        }
    }

    public static boolean isWikiPageUrl(String url) {
        Pattern p = Pattern.compile(REGEX_WIKIPEDIA_FILE);
        Matcher m = p.matcher(url);
        return m.matches();
    }
}
