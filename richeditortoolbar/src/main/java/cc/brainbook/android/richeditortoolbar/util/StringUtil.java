package cc.brainbook.android.richeditortoolbar.util;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.text.TextUtils;

public class StringUtil {
    public static CharSequence[] getItems(Context context, @ArrayRes int itemsId) {
        return context.getResources().getTextArray(itemsId);
    }

    public static int getIndex(Context context, @ArrayRes int itemsId, Object item) {
        if (item == null) {
            return -1;
        }
        CharSequence[] items = getItems(context, itemsId);
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public static CharSequence getItem(Context context, @ArrayRes int itemsId, int index) {
        CharSequence[] items = getItems(context, itemsId);
        if (index < 0 || index >= items.length) {
            return null;
        } else {
            return items[index];
        }
    }

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

    private StringUtil() {}
}
