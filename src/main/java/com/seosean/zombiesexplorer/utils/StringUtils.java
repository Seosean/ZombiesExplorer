package com.seosean.zombiesexplorer.utils;

import com.seosean.zombiesexplorer.ZombiesExplorer;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static List<String> ZOMBIES_TITLE = Arrays.asList("ZOMBIES", "僵尸末日", "殭屍末日");

    public static String trim(String s) {
        return s.replaceAll(ZombiesExplorer.EMOJI_REGEX, "").replaceAll(ZombiesExplorer.COLOR_REGEX, "").trim();
    }

    public static int getNumberInString(String s) {
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 0;
    }
}
