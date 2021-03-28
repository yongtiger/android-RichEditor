package cc.brainbook.android.richeditortoolbar.util;

import android.text.TextUtils;

import androidx.annotation.Nullable;

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

}
