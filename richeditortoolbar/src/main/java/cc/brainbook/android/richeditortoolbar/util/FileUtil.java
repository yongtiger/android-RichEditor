package cc.brainbook.android.richeditortoolbar.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public abstract class FileUtil {

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
    public static boolean isCanWrite(String path) {
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

    public static boolean saveBitmap(@NonNull Context context, @NonNull Bitmap bitmap, Uri imageFileUri,
                                     Bitmap.CompressFormat format, int quality) {
        boolean result = true;

        OutputStream outputStream = null;
        try {
            outputStream = context.getContentResolver().openOutputStream(imageFileUri);
            bitmap.compress(format, quality, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            result = false;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();

                result = false;
            }
        }

        return result;
    }

    public static boolean createFileFromString(@NonNull File file, String text) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            //BufferedWriter for performance, false to overwrite to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, false));
            buf.write(text);
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
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

}
