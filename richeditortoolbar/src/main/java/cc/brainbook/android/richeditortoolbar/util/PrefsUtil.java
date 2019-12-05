package cc.brainbook.android.richeditortoolbar.util;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class PrefsUtil {
    public static String getString(Context context, String sharedPreferencesName, String key, String defaultValue) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void putString(Context context, String sharedPreferencesName, String key, String value) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        ///注意：commit是同步写，可能会阻塞主线程，因此不建议
//                editor.commit();
        editor.apply();
    }

    public static void clear(Context context, String sharedPreferencesName) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        ///注意：commit是同步写，可能会阻塞主线程，因此不建议
//                editor.commit();
        editor.apply();
    }

}
