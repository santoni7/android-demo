package com.santoni7.readme.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextUtils {
    public static String readStringFromStream(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        try(InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(isr)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
    }

    public static String getFileNameFromURL(String url){
        return url.substring(url.lastIndexOf('/') + 1);
    }
}
