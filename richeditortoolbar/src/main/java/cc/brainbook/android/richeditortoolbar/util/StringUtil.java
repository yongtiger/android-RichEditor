package cc.brainbook.android.richeditortoolbar.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public abstract class StringUtil {
//    @Nullable
//    public static String getParameter(String s, String paramStart, String paramEnd) {
//        if (TextUtils.isEmpty(s) || TextUtils.isEmpty(paramStart) || !s.contains(paramStart)) {
//            return null;
//        }
//        final int index = s.indexOf(paramStart);
//        final int start = index + paramStart.length();
//        final int end = s.indexOf(paramEnd, index);
//        return s.substring(start, end);
//    }

    public static boolean isUrl(@NonNull String src) {
        return src.startsWith("http://") || src.startsWith("https://");
    }

    public static boolean isInteger(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static int parseInt(String str) {
        if (!isInteger(str)) {
            return 0;
        }
        return Integer.parseInt(str);
    }

//    System.out.println(parseIntOrDefault("123", 0)); // 123
//    System.out.println(parseIntOrDefault("aaa", 0)); // 0
//    System.out.println(parseIntOrDefault("aaa456", 3, 0)); // 456
//    System.out.println(parseIntOrDefault("aaa789bbb", 3, 6, 0)); // 789
    public static int parseIntOrDefault(String value, int defaultValue) {
        int result = defaultValue;
        try {
            result = Integer.parseInt(value);
        }
        catch (Exception e) {
        }
        return result;
    }
    public static int parseIntOrDefault(String value, int beginIndex, int defaultValue) {
        int result = defaultValue;
        try {
            String stringValue = value.substring(beginIndex);
            result = Integer.parseInt(stringValue);
        }
        catch (Exception e) {
        }
        return result;
    }
    public static int parseIntOrDefault(String value, int beginIndex, int endIndex, int defaultValue) {
        int result = defaultValue;
        try {
            String stringValue = value.substring(beginIndex, endIndex);
            result = Integer.parseInt(stringValue);
        }
        catch (Exception e) {
        }
        return result;
    }


    @NonNull
    public static String getDateFormat(Date date) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        return dateFormat.format(date);
    }

}
