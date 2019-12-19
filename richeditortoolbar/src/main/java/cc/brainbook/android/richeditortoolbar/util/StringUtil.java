package cc.brainbook.android.richeditortoolbar.util;

import android.text.TextUtils;

public abstract class StringUtil {
    public static String getParameter(String s, String paramStart, String paramEnd) {
        if (TextUtils.isEmpty(s) || TextUtils.isEmpty(paramStart) || !s.contains(paramStart)) {
            return null;
        }
        final int index = s.indexOf(paramStart);
        final int start = index + paramStart.length();
        final int end = s.indexOf(paramEnd, index);
        return s.substring(start, end);
    }

    public static boolean isUrl(String src) {
        return src.startsWith("http://") || src.startsWith("https://");
    }

}
