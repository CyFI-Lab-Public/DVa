package com.company.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    public static boolean regexMatch(String pattern, String message){
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(message);
        return matcher.find();
    }

    public static boolean regexMatch(List<String> patterns, String message){
        for(String pattern : patterns){
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(message);
            if(matcher.find()) {
              return true;
            }
        }
        return false;
    }
}
