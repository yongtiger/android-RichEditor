package cc.brainbook.android.richeditortoolbar.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class FileUtil {

    public static String generateImageFileName(String suffix) {
        ///注意：尽量不要用空格、冒号、下划线、减号等特殊字符！
        ///比如：URI的fromFile方法会将路径中的空格用“%20”取代，而个别手机（如酷派7260）系统自带的相机没有将“%20”读成空格，拍照后的照片的名字是123%201.jpg
        ///https://www.jianshu.com/p/b78aaebd9a88
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        return dateFormat.format(new Date()) + "." + suffix;
    }

    public static File generateFileByPreffix(File file, String preffix) {
        final String path = file.getPath();
        final String name = file.getName();
        return new File(path, preffix + name);
    }

    /**
     * 获取文件的Uri（适应Android 7.0）
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
     *
     * @param context
     * @param file
     * @return
     */
    public static Uri getUriFromFile(@NonNull Context context, @NonNull File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".android7.fileprovider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * Saves Bitmap to File
     * @param bitmap
     * @param file
     * @param format    Bitmap.CompressFormat.JPEG/Bitmap.CompressFormat.PNG
     * @param quality
     */
    public static void saveBitmapToFile(Bitmap bitmap, File file, Bitmap.CompressFormat format, int quality) {
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

    public static String readFile(File file) {
        return readFile(file, 1024);
    }
    public static String readFile(File file, int bufferSize) {
        final StringBuilder contentBuilder = new StringBuilder();
        try {
            final FileInputStream fileInputStream = new FileInputStream(file);
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            final byte[] buffer = new byte[bufferSize];
            int flag;
            while ((flag = bufferedInputStream.read(buffer)) != -1) {
                contentBuilder.append(new String(buffer, 0, flag));
            }

            bufferedInputStream.close();
            return contentBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeFile(File file, String content) {
        writeFile(file, content, 0);
    }
    public static void writeFile(File file, String content, int bufferSize) {
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            final BufferedOutputStream bufferedOutputStream = bufferSize == 0 ?
                    new BufferedOutputStream(fileOutputStream) : new BufferedOutputStream(fileOutputStream, bufferSize);
            bufferedOutputStream.write(content.getBytes(),0,content.getBytes().length);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
