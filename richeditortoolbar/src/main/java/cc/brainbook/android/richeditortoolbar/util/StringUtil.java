package cc.brainbook.android.richeditortoolbar.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public static boolean isUrl(@NonNull String src) {
        return src.startsWith("http://") || src.startsWith("https://");
    }

    @NonNull
    public static String getDateFormat(Date date) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        return dateFormat.format(date);
    }

}
