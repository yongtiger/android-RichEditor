package cc.brainbook.android.richeditortoolbar.util;

import android.content.Context;
import androidx.annotation.ArrayRes;

public abstract class ArrayUtil {
    public static int getStringIndex(Context context, @ArrayRes int itemsId, String item) {
        if (item == null) {
            return -1;
        }
        final String[] items = context.getResources().getStringArray(itemsId);
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public static String getStringItem(Context context, @ArrayRes int itemsId, int index) {
        final String[] items = context.getResources().getStringArray(itemsId);
        if (index < 0 || index >= items.length) {
            return null;
        } else {
            return items[index];
        }
    }

    public static int getIntIndex(Context context, @ArrayRes int itemsId, int item) {
        final int[] items = context.getResources().getIntArray(itemsId);
        for (int i = 0; i < items.length; i++) {
            if (items[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public static int getIntItem(Context context, @ArrayRes int itemsId, int index) {
        final int[] items = context.getResources().getIntArray(itemsId);
        if (index < 0 || index >= items.length) {
            return Integer.MIN_VALUE;
        } else {
            return items[index];
        }
    }
}
