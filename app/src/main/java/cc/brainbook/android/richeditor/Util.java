package cc.brainbook.android.richeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class Util {

    @NonNull
    public static String getDateFormat(Date date) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    @NonNull
    public static File generateFileByPrefix(@NonNull File file, String prefix) {
        final String path = file.getPath();
        final String name = file.getName();
        return new File(path, prefix + name);
    }

    /**
     * 生成视频的第一帧图片
     *
     * @param context
     * @param videoUri
     * @param videoCoverFile
     * @param format
     * @param quality
     */
    public static void generateVideoCover(@NonNull Context context, @NonNull Uri videoUri, File videoCoverFile,
                                          Bitmap.CompressFormat format, int quality) {
        final MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, videoUri);
        final Bitmap bitmap = mmr.getFrameAtTime(); ///第一帧图片
        mmr.release();

        if (bitmap == null) {
            return;
        }

        saveBitmapToFile(bitmap, videoCoverFile, format, quality);
    }

    /**
     * Saves Bitmap to File
     *
     * @param bitmap
     * @param file
     * @param format    Bitmap.CompressFormat.JPEG/Bitmap.CompressFormat.PNG
     * @param quality
     */
    public static void saveBitmapToFile(@NonNull Bitmap bitmap, File file, Bitmap.CompressFormat format, int quality) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(format, quality, fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
