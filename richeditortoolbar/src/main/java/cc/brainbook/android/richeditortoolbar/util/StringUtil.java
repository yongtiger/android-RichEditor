package cc.brainbook.android.richeditortoolbar.util;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.regex.Pattern;

public abstract class StringUtil {
    @Nullable
    public static String getParameter(String s, String paramStart, String paramEnd) {
        if (TextUtils.isEmpty(s) || TextUtils.isEmpty(paramStart) || !s.contains(paramStart)) {
            return null;
        }
        final int index = s.indexOf(paramStart);
        final int start = index + paramStart.length();
        final int end = s.indexOf(paramEnd, index);
        return s.substring(start, end);
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
}
