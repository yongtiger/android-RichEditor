package cc.brainbook.android.richeditortoolbar.util;

import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class UriUtil {

    public static void grantReadPermissionToUri(@NonNull Context context, @NonNull Uri uri, int flags) {
        ///[FIX#java.lang.SecurityException: Permission Denial: opening provider com.android.providers.media.MediaDocumentsProvider requires that you obtain access using ACTION_OPEN_DOCUMENT or related APIs]
        ///https://stackoverflow.com/questions/19834842/android-gallery-on-android-4-4-kitkat-returns-different-uri-for-intent-action
        //////??????https://stackoverflow.com/questions/46785237/permission-for-an-image-from-gallery-is-lost-after-re-launch
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ///[FIX#java.lang.SecurityException#getContentResolver()#takePersistableUriPermission]
            ///https://stackoverflow.com/questions/37993762/java-lang-securityexception-on-takepersistableuripermission-saf
            try {
                context.grantUriPermission(context.getPackageName(), uri, FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } catch (IllegalArgumentException e) {
                // on Kitkat api only 0x3 is allowed (FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION)
                ///https://android.googlesource.com/platform/frameworks/base/+/kitkat-mr2.2-release/services/java/com/android/server/am/ActivityManagerService.java#6214
                context.grantUriPermission(context.getPackageName(), uri, FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            try {
                final int takeFlags = flags & (FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
                context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }


    @Nullable
    public static String getFilename(@NonNull Context context, @Nullable String authority, Uri uri) {
        if (uri == null || TextUtils.isEmpty(uri.getPath())) {
            return null;
        }

        if (uri.getScheme() == null ///"/storage/emulated/0/Pictures/IMG_20220611_233005.jpg"
                || "file".equalsIgnoreCase(uri.getScheme()) && TextUtils.isEmpty(uri.getAuthority())) {    ///"file:///storage/emulated/0/Pictures/IMG_20220611_233005.jpg"
            final String filePath = uri.getPath();
            if (!TextUtils.isEmpty(filePath)) {
                return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
            }
        } else

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (authority != null && authority.equalsIgnoreCase(uri.getAuthority())) {   ///FileProviderUri："content://cc.brainbook.android.richeditor.file.path.share/external_files_path/Pictures/20220612084351.jpg"
                final File file = FileProviderUtil.getFileFromFileProviderUri(context, uri);
                if (file != null) {
                    return file.getName();
                }
            } else {    ///"content://com.android.providers.downloads.documents/document/15"或GoogleDrive等
                return ContentUriUtil.getFilename(context, uri);
            }
        }

        //////?????? todo: 网络http等

        return null;
    }

    @Nullable
    public static File getExportableFile(@NonNull Context context, String mediaFilesDirPath, @Nullable String authority, String uriString) {
        if (TextUtils.isEmpty(uriString)) {
            return null;
        }

        final Uri uri = Uri.parse(uriString);
        if (uri == null || TextUtils.isEmpty(uri.getPath())) {
            return null;
        }

        ///"/storage/emulated/0/Pictures/IMG_20220611_233005.jpg"
        if (uri.getScheme() == null && uriString.startsWith(mediaFilesDirPath)) {
            return new File(uriString);
        }

        ///"file:///storage/emulated/0/Pictures/IMG_20220611_233005.jpg"
        if ("file".equalsIgnoreCase(uri.getScheme()) && TextUtils.isEmpty(uri.getAuthority())) {
            final String filePath = uri.getPath();
            if (filePath.startsWith(mediaFilesDirPath)) {
                return new File(filePath);
            }
        }

        ///FileProviderUri："content://cc.brainbook.android.richeditor.file.path.share/external_files_path/Pictures/20220612084351.jpg"
        if ("content".equalsIgnoreCase(uri.getScheme())
                && authority != null && authority.equalsIgnoreCase(uri.getAuthority())) {
            return FileProviderUtil.getFileFromFileProviderUri(context, uri);
        }

        return null;
    }

    @Nullable
    public static File copyToExportableFile(@NonNull Context context, @Nullable String authority, String uriString) {
        if (TextUtils.isEmpty(uriString)) {
            return null;
        }

        final Uri uri = Uri.parse(uriString);

        return copyToExportableFile(context, authority, uri);
    }

    @Nullable
    public static File copyToExportableFile(@NonNull Context context, @Nullable String authority, Uri uri) {
        if (uri == null || TextUtils.isEmpty(uri.getPath())) {
            return null;
        }

        ///媒体文件：content://com.android.providers.media.documents/document/image%3A37
        ///文档文件：content://com.android.providers.media.documents/document/document%3A37
        ///下载文件：content://com.android.providers.downloads.documents/document/8
        ///GoogleDrive文件：content://com.google.android.apps.docs.storage/document/acc%3D1%3Bdoc%3Dencoded%3De5uABJKZks

        //////?????? todo: 网络http等

        if ("content".equalsIgnoreCase(uri.getScheme())
                && (authority == null || !authority.equalsIgnoreCase(uri.getAuthority()))) {

            final String newFilename = getFilename(context, authority, uri);
            if (TextUtils.isEmpty(newFilename)) {
                return null;
            }

            final String mimeType = FileUtil.getFileMimeType(newFilename);
            final File file = TextUtils.isEmpty(mimeType) ? DirUtil.getFilesDir(context)
                    : mimeType.startsWith("image") ? DirUtil.getPictureFilesDir(context)
                    : mimeType.startsWith("audio") ? DirUtil.getAudioFilesDir(context)
                    : mimeType.startsWith("video") ? DirUtil.getVideoFilesDir(context)
                    : DirUtil.getFilesDir(context);
            final File newFile = new File(file + File.separator + newFilename);

            try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
                 FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    fileOutputStream.write(buf, 0, len);
                }

                return newFile;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @NonNull
    public static Uri tryToGetExportableUri(@NonNull Context context, @Nullable String authority, Uri uri) {
        Uri result = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                result = MediaStore.getMediaUri(context, uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (result == null) {
            ///[接收分享]不包含持久URI权限！需要复制文件到本地目录
            ///Generally, no. takePersistableUriPermission() is for Storage Access Framework Uri values, as in your ACTION_OPEN_DOCUMENT scenario. It is not for arbitrary Uri values, like you might get via ACTION_PICK, or EXTRA_STREAM on receiving ACTION_SEND, or the Uri passed to ACTION_VIEW, etc.
            ///For ACTION_SEND, ACTION_VIEW, etc., you need to assume that your app has only transitory access to the content. You would need to have some sort of "import" operation and make your own copy of that content if you need longer-term access.
            ///https://stackoverflow.com/questions/69698865/take-persistable-uri-permission-for-action-send-intent-filter-uri-results-in-sec
            final File file = copyToExportableFile(context, authority, uri);
            if (file != null && file.exists()) {
                result = Uri.fromFile(file);
            } else {
                result = uri;
            }
        }

        return result;
    }

}
