package cc.brainbook.android.richeditortoolbar.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public abstract class FileUtil {

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

        FileUtil.saveBitmapToFile(bitmap, videoCoverFile, format, quality);
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

    /**
     * Saves Drawable to File
     *
     * @param drawable
     * @param file
     * @param format    Bitmap.CompressFormat.JPEG/Bitmap.CompressFormat.PNG
     * @param quality
     */
    public static void saveDrawableToFile(Drawable drawable, File file, Bitmap.CompressFormat format, int quality) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            ((BitmapDrawable) drawable).getBitmap().compress(format, quality, fos);
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

    @NonNull
    public static byte[] readFile(@NonNull File file) throws IOException {
        return readFile(file, 4096);
    }
    @NonNull
    public static byte[] readFile(@NonNull File file, int bufferSize) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);

            final byte[] buffer = new byte[bufferSize];
            int read;
            while ((read = bufferedInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, read);
            }

            bufferedInputStream.close();
            return byteArrayOutputStream.toByteArray();
        } finally {
            try {
                if (byteArrayOutputStream != null)
                    byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (bufferedInputStream != null)
                    bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(File file, byte[] bytes) throws IOException {
        writeFile(file, bytes, 0);
//        writeFile(file, bytes, 4096);
    }
    public static void writeFile(File file, byte[] bytes, int bufferSize) throws IOException {
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            bufferedOutputStream = bufferSize == 0 ?
                    new BufferedOutputStream(fileOutputStream) : new BufferedOutputStream(fileOutputStream, bufferSize);
            bufferedOutputStream.write(bytes,0, bytes.length);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } finally {
            try {
                if (bufferedOutputStream != null)
                    bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
