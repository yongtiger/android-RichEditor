package cc.brainbook.android.richeditortoolbar.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
////////////////////
/**
 * 适应Android 7.0及以上
 *
 * 在Android 7.0以上的系统中，尝试传递 file://URI可能会触发FileUriExposedException
 *
 * <root-path/> 代表设备的根目录new File("/");
 * <files-path/> 代表context.getFilesDir()
 * <cache-path/> 代表context.getCacheDir()
 * <external-path/> 代表Environment.getExternalStorageDirectory()
 * <external-files-path>代表context.getExternalFilesDirs()
 * <external-cache-path>代表getExternalCacheDirs()
 *
 * https://blog.csdn.net/lmj623565791/article/details/72859156
 * https://juejin.im/post/6844903687459078152
 *
 * FilePath:
 *          /storage/emulated/0/Android/data/cc.brainbook.android.richeditor/files/Pictures/20201003050111.jpg
 *
 * FileProviderUri:
 *          Android N及以上：content://cc.brainbook.android.richeditor.file.path.share/external_files_path/Movies/20201003185042.mp4
 *          Android N以下：file:///storage/emulated/0/Android/data/cc.brainbook.android.richeditor/files/Pictures/20201003050111.jpg
 *
 */
public abstract class UriUtil {

    /**
     * 获取File的FileProviderUri
     *
     * @param context
     * @param file
     * @param authorities
     * @return  FileProviderUri
     */
    public static Uri getFileProviderUriFromFile(@NonNull Context context, @NonNull File file, String authorities) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, authorities, file);
        } else {
            return Uri.fromFile(file);
        }
    }
    @Nullable
    public static File getFileFromFileProviderUri(@NonNull Context context, @NonNull Uri fileProviderUri) {
        String filePath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            filePath = getFilePathFromFileProviderUri(context, fileProviderUri);
        } else {
            filePath = fileProviderUri.getPath();
        }

        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        return new File(filePath);
    }

    /**
     * 获取FileProviderUri的FilePath
     *
     * https://blog.csdn.net/zhanggn_/article/details/89679585?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.edu_weight&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.edu_weight
     *
     * @param context
     * @param fileProviderUri
     * @return  FilePath
     */
    @Nullable
    public static String getFilePathFromFileProviderUri(@NonNull Context context, @NonNull Uri fileProviderUri) {
        try {
            List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (fileProviderUri.getAuthority() != null) {
                String fileProviderClassName = FileProvider.class.getName();
                for (PackageInfo pack : packs) {
                    ProviderInfo[] providers = pack.providers;
                    if (providers != null) {
                        for (ProviderInfo provider : providers) {
                            if (fileProviderUri.getAuthority().equals(provider.authority)) {
                                if (provider.name.equalsIgnoreCase(fileProviderClassName)) {
                                    Class<FileProvider> fileProviderClass = FileProvider.class;
                                    try {
                                        Method getPathStrategy = fileProviderClass.getDeclaredMethod("getPathStrategy", Context.class, String.class);
                                        getPathStrategy.setAccessible(true);
                                        Object invoke = getPathStrategy.invoke(null, context, fileProviderUri.getAuthority());
                                        if (invoke != null) {
                                            String PathStrategyStringClass = FileProvider.class.getName() + "$PathStrategy";
                                            Class<?> PathStrategy = Class.forName(PathStrategyStringClass);
                                            Method getFileForUri = PathStrategy.getDeclaredMethod("getFileForUri", Uri.class);
                                            getFileForUri.setAccessible(true);
                                            Object invoke1 = getFileForUri.invoke(invoke, fileProviderUri);
                                            if (invoke1 instanceof File) {
                                                return ((File) invoke1).getAbsolutePath();
                                            }
                                        }
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public static Uri getFileProviderUriFromFilePath(@NonNull Context context, @NonNull String filePath, String authorities) {
        return getFileProviderUriFromFile(context, new File(filePath), authorities);
    }

    /**
     * 把字符串解析为Uri
     *
     * @param context
     * @param uriString 任意字符串
     * @param authorities
     * @return
     *          null or 空字符串    =》 null
     *          uri.getScheme()为null or "file"  =》 FileProviderUri
     *          DocumentUri =》 FileProviderUri
     *          其它如"http"等  =》 Uri.parse(uriString)
     */
    @Nullable
    public static Uri parseToUri(@NonNull Context context, String uriString, String authorities) {
        if (TextUtils.isEmpty(uriString)) {
            return null;
        }

        final Uri uri = Uri.parse(uriString);

        if (uri == null || TextUtils.isEmpty(uri.getPath())) {
            return null;
        }

        if (uri.getScheme() == null || "file".equalsIgnoreCase(uri.getScheme())) {
            return UriUtil.getFileProviderUriFromFilePath(context, uri.getPath(), authorities);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            //////??????暂时将DocumentUri转换为FileProviderUri，以后考虑改为API读写DocumentUri
            final String filePath = UriUtil.getFilePathFromUri(context, uri);
            if (TextUtils.isEmpty(filePath)) {
                return null;
            } else {
                return UriUtil.getFileProviderUriFromFile(context, new File(filePath), authorities);
            }
        }

        return uri;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @return  FilePath
     */
    @Nullable
    public static String getFilePathFromUri(@NonNull Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    try {
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                        return getDataColumn(context, contentUri, null, null);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }

        ///null if this is a relative URI or empty
        else if(uri.getScheme() == null) {
            ///当作File处理，例如：/storage/emulated/0/Android/data/cc.brainbook.android.richeditor/files/Pictures/20201003050111.jpg
            return uri.getPath();
        }

        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            String path = getDataColumn(context, uri, null, null);
            if (path == null) { //如果getDataColumn()返回null，则尝试获取FileProvider Uri的path
                ///例如：content://cc.brainbook.android.richeditor.file.path.share/external_files_path/Movies/20201003185042.mp4
                path = getFilePathFromFileProviderUri(context, uri);
            }
            return path;
        }

        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    @Nullable
    public static String getDataColumn(@NonNull Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(@NonNull Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(@NonNull Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(@NonNull Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(@NonNull Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
