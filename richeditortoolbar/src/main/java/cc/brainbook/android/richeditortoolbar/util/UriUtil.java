package cc.brainbook.android.richeditortoolbar.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;

public abstract class UriUtil {

    public static void grantReadPermissionToUri(@NonNull Context context, @NonNull Uri uri, int flags) {
        ///[FIX#java.lang.SecurityException: Permission Denial: opening provider com.android.providers.media.MediaDocumentsProvider requires that you obtain access using ACTION_OPEN_DOCUMENT or related APIs]
        ///https://stackoverflow.com/questions/19834842/android-gallery-on-android-4-4-kitkat-returns-different-uri-for-intent-action
        //////??????https://stackoverflow.com/questions/46785237/permission-for-an-image-from-gallery-is-lost-after-re-launch
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ///[FIX#java.lang.SecurityException#getContentResolver()#takePersistableUriPermission]
            ///https://stackoverflow.com/questions/37993762/java-lang-securityexception-on-takepersistableuripermission-saf
            try {
                context.grantUriPermission(context.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } catch (IllegalArgumentException e) {
                // on Kitkat api only 0x3 is allowed (FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION)
                context.grantUriPermission(context.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            try {
                final int takeFlags = flags
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

}
