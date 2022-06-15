package cc.brainbook.android.richeditortoolbar.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public abstract class ContentUriUtil {

    ///https://stackoverflow.com/questions/13209494/how-to-get-the-full-file-path-from-uri/60642994#answer-60642994
    ///https://gist.github.com/HBiSoft/15899990b8cd0723c3a894c1636550a8
    ///https://github.com/saparkhid/AndroidFileNamePicker/blob/main/javautil/FileUtils.java
    ///https://github.com/HBiSoft/PickiT/blob/master/pickit/src/main/java/com/hbisoft/pickit/Utils.java

    ///注意：如果uri为FileProviderUri，则无法获取File path，返回null。所以FileProviderUri需要另行额外去处理

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
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (uri == null) {
            return null;
        }

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                // This is for checking Main Memory
                if ("primary".equalsIgnoreCase(type)) {
                    if (split.length > 1) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        return Environment.getExternalStorageDirectory() + "/";
                    }
                    // This is for checking SD Card
                } else {
                    // Some devices does not allow access to the SD Card using the UID, for example /storage/6551-1152/folder/video.mp4
                    // Instead, we first have to get the name of the SD Card, for example /storage/sdcard1/folder/video.mp4

                    // We first have to check if the device allows this access
                    if (new File("storage" + "/" + docId.replace(":", "/")).exists()){
                        return "/storage/" + docId.replace(":", "/");
                    }

                    // If the file is not available, we have to get the name of the SD Card, have a look at SDUtils
                    String[] availableExternalStorages = SDUtil.getStorageDirectories(context);
                    String root = "";
                    for (String s: availableExternalStorages) {
                        if (split[1].startsWith("/")){
                            root = s+split[1];
                        }else {
                            root = s+"/"+split[1];
                        }
                    }
                    if (root.contains(type)){
                        return "storage" + "/" + docId.replace(":", "/");
                    }else{
                        if (root.startsWith("/storage/")||root.startsWith("storage/")) {
                            return root;
                        }else if (root.startsWith("/")){
                            return "/storage"+root;
                        }else{
                            return "/storage/"+root;
                        }
                    }
                }

            }

            // DownloadsProvider
            else if (isRawDownloadsDocument(uri)){
                String fileName = getFilename(context, uri);
                String subFolderName = getSubFolders(uri);

                if (fileName != null) {
                    return Environment.getExternalStorageDirectory().toString() + "/Download/" + subFolderName + fileName;
                }
                String id = DocumentsContract.getDocumentId(uri);

                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            else if (isDownloadsDocument(uri)) {
                String fileName = getFilename(context, uri);
                if (fileName != null) {
                    return Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                }

                String id = DocumentsContract.getDocumentId(uri);
                if (id.startsWith("raw:")) {
                    id = id.replaceFirst("raw:", "");
                    File file = new File(id);
                    if (file.exists())
                        return id;
                }
                if (id.startsWith("raw%3A%2F")){
                    id = id.replaceFirst("raw%3A%2F", "");
                    File file = new File(id);
                    if (file.exists())
                        return id;
                }

                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
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

        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        }

        ///uri.getScheme() == null当作File处理，例如：/storage/emulated/0/Android/data/cc.brainbook.android.richeditor/files/Pictures/20201003050111.jpg
        else if (uri.getScheme() == null || "file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    ///https://developer.android.com/reference/kotlin/android/provider/MediaStore.MediaColumns?hl=zh-cn#data
    ///From Android 11 onwards, this column is read-only for apps that target R and higher.
    // On those devices, when creating or updating a uri, this column's value is not accepted.
    // Instead, to update the filesystem location of a file, use the values of the DISPLAY_NAME and RELATIVE_PATH columns.
    ///https://developer.android.com/training/data-storage/shared/media
    ///当您访问现有媒体文件(Media)时，您可以使用您的逻辑中 DATA 列的值，此值包含有效的文件路径。而Q版本及以上，Download和Document中的DATA则为null
    // 注意：不要假设文件始终可用！请准备好处理可能发生的任何基于文件的 I/O 错误。
    ///另一方面，如需创建或更新媒体文件，请勿使用 DATA 列的值。请改用 DISPLAY_NAME 和 RELATIVE_PATH 列的值。
    //    MediaStore.MediaColumns.DATA,   ///"/storage/emulated/0/Pictures/IMG_20220611_233005.jpg"
    //    MediaStore.MediaColumns.DISPLAY_NAME,   ///"IMG_20220611_233005.jpg"
    //    MediaStore.MediaColumns.RELATIVE_PATH   ///[API29+] "Pictures/"

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
    public static String getDataColumn(@NonNull Context context, Uri uri, String selection, String[] selectionArgs) {
        final String[] projection = {MediaStore.MediaColumns.DATA};
        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return uri != null && "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return uri != null && "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return uri != null && "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return uri != null && "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    ///https://github.com/HBiSoft/PickiT/blob/27cf11e87cc3f0bac682c3d6fc31d654c54de42d/pickit/src/main/java/com/hbisoft/pickit/Utils.java#L72
    public static boolean isRawDownloadsDocument(Uri uri) {
        String uriToString = String.valueOf(uri);
        return uriToString.contains("com.android.providers.downloads.documents/document/raw");
    }

    ///https://github.com/saparkhid/AndroidFileNamePicker/blob/82b8a215ee997409e4007f4ee4851a9746da8a8c/javautil/FileUtils.java#L150
    public static boolean isWhatsAppFile(Uri uri){
        return uri != null && "com.whatsapp.provider.media".equals(uri.getAuthority());
    }
    public static boolean isGoogleDriveUri(Uri uri) {
        return uri != null && "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    @Nullable
    public static String getFilename(@NonNull Context context, @NonNull Uri uri) {
        final String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                return cursor.getString(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @NonNull
    private static String getSubFolders(@NonNull Uri uri) {
        String replaceChars = String.valueOf(uri).replace("%2F", "/").replace("%20", " ").replace("%3A",":");
        String[] bits = replaceChars.split("/");
        String sub5 = bits[bits.length - 2];
        String sub4 = bits[bits.length - 3];
        String sub3 = bits[bits.length - 4];
        String sub2 = bits[bits.length - 5];
        String sub1 = bits[bits.length - 6];
        if (sub1.equals("Download")){
            return sub2+"/"+sub3+"/"+sub4+"/"+sub5+"/";
        }
        else if (sub2.equals("Download")){
            return sub3+"/"+sub4+"/"+sub5+"/";
        }
        else if (sub3.equals("Download")){
            return sub4+"/"+sub5+"/";
        }
        else if (sub4.equals("Download")){
            return sub5+"/";
        }
        else {
            return "";
        }
    }

}
