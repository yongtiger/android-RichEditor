package cc.brainbook.android.richeditortoolbar.util;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class FileUtil {

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
