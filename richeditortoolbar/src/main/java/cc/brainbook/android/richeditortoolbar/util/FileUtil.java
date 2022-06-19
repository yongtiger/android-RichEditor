package cc.brainbook.android.richeditortoolbar.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class FileUtil {

    // url = file path or whatever suitable URL you want.
    ///https://www.cnblogs.com/yongdaimi/p/13645719.html
    public static String getFileMimeType(String url) {
        String type = null;
        String extension = getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
    public static String getFileExtensionFromUrl(String url) {
        return MimeTypeMap.getFileExtensionFromUrl(url);
    }

    /**
     * 获取目录总大小（递归目录下的所有文件及子目录下所有文件）
     *
     * @param file
     * @return
     */
    public static long getDirSize(File file) {
        long size = 0;

        if (file == null || !file.exists()) {
            return 0;
        }

        if (file.isDirectory()) {
            final File[] children = file.listFiles();   ///注意：如果Android 6+没有SD卡写入权限则返回null！
            if (children != null && children.length > 0) {
                for (File f : children) {
                    size += getDirSize(f);
                }
            }
        } else {
            size = file.length();
        }

        return size;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param file 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File file) {
        if (file == null || !file.exists()) {
            return true;
        }

        boolean success = true;
        if (file.isDirectory()) {
            final File[] children = file.listFiles();   ///注意：如果Android 6+没有SD卡写入权限则返回null！
            if (children != null && children.length > 0) {
                for (File child : children) {
                    if (!deleteDir(child)) {
                        success = false;
                    }
                }
            }
        }

        return success & file.delete();
    }

    /**
     * 创建本地目录
     *
     * mkdir()和mkdirs()的区别：
     *     mkdir()  创建此抽象路径名指定的目录。如果父目录不存在则创建不成功。
     *     mkdirs() 创建此抽象路径名指定的目录，包括所有必需但不存在的父目录。
     *
     * @param dir
     */
    public static boolean mkdirs(@NonNull File dir) {
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return false;
            }
        }
        return true;
    }
    public static boolean mkdirs(String path) {
        return mkdirs(new File(path));
    }
    public static boolean mkdir(@NonNull File dir) {
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                return false;
            }
        }
        return true;
    }
    public static boolean mkdir(String path) {
        return mkdir(new File(path));
    }

    /**
     * 检查文件或目录是否可写入
     *
     * @param path
     * @return
     */
    public static boolean isWritable(String path) {
        return new File(path).canWrite();
    }

    /**
     * 关闭流Closeable
     *
     * @param closeables
     */
    public static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /* --------------- 使用信息流访问文件 --------------- */
    /* 注意：信息流访问文件时应在后台线程而非界面线程上完成此操作 */
    ///https://developer.android.com/training/data-storage/app-specific?hl=zh-cn#internal-access-stream

    @NonNull
    public static byte[] readFile(File file) throws IOException {
        return readFile(file, 4096);
    }
    @NonNull
    public static byte[] readFile(File file, int bufferSize) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            final byte[] buffer = new byte[bufferSize];
            int read;
            while ((read = bufferedInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, read);
            }

            return byteArrayOutputStream.toByteArray();
        }
    }

    public static void writeFile(File file, String text, boolean append) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, append))) {
            bufferedWriter.write(text);
        }
    }

    public static void writeFile(File file, @NonNull Bitmap bitmap, Bitmap.CompressFormat format, int quality) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            bitmap.compress(format, quality, outputStream);
        }
    }

    /**
     * Saves Drawable to File
     *
     * @param file
     * @param drawable
     * @param format    Bitmap.CompressFormat.JPEG/Bitmap.CompressFormat.PNG
     * @param quality
     */
    public static void writeFile(File file, Drawable drawable, Bitmap.CompressFormat format, int quality) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            ((BitmapDrawable) drawable).getBitmap().compress(format, quality, outputStream);
        }
    }

    public static void writeFile(File file, byte[] bytes) throws IOException {
        writeFile(file, bytes, 0);
//        writeFile(file, bytes, 4096);
    }
    public static void writeFile(File file, byte[] bytes, int bufferSize) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             BufferedOutputStream bufferedOutputStream = bufferSize == 0 ?
                     new BufferedOutputStream(fileOutputStream) : new BufferedOutputStream(fileOutputStream, bufferSize);) {

            bufferedOutputStream.write(bytes,0, bytes.length);
//            bufferedOutputStream.flush();   ///https://stackoverflow.com/questions/8181318/benefits-of-using-flush-close-in-android-streams
        }
    }

    public static void writeFile(File file, @NonNull InputStream inputStream) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
    }

}
