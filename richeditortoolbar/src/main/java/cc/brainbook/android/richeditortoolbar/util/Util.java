package cc.brainbook.android.richeditortoolbar.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

public abstract class Util {

    ///尽量直接使用mContext，避免用view.getContext()！否则可能获取不到Activity而导致异常
    ///java.lang.ClassCastException: android.support.v7.widget.TintContextWrapper cannot be cast to android.app.Activity
    ///https://blog.csdn.net/liuxu0703/article/details/70145168
    ///https://stackoverflow.com/questions/38814267/android-support-v7-widget-tintcontextwrapper-cannot-be-cast
    /**
     * try get host activity from view.
     * views hosted on floating window like dialog and toast will sure return null.
     * @return host activity; or null if not available
     */
    public static Activity getActivityFromView(View view) {
        Context context = view.getContext();
        return getActivityFromContext(context);
    }
    public static Activity getActivityFromContext(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

}
