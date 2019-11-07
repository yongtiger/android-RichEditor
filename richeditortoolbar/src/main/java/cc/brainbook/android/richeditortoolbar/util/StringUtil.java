package cc.brainbook.android.richeditortoolbar.util;

import android.content.Context;
import android.support.annotation.ArrayRes;

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

    private StringUtil() {}
}
