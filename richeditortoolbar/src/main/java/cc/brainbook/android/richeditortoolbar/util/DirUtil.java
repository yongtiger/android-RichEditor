package cc.brainbook.android.richeditortoolbar.util;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;

public abstract class DirUtil {

    public static File getPictureFilesDir(@NonNull Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            ///外部存储可用
            ///注意：Android 10 Q (API 29) 访问公共外部存储必须MediaStore API！而11 R (API 30)则恢复29之前，即照常访问
//            imageFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else {
            ///外部存储不可用
            return new File(context.getFilesDir(), Environment.DIRECTORY_PICTURES);
        }
    }

    public static File getVideoFilesDir(@NonNull Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            ///外部存储可用
            ///注意：Android 10 Q (API 29) 访问公共外部存储必须MediaStore API！而11 R (API 30)则恢复29之前，即照常访问
//            imageFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            return context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        } else {
            ///外部存储不可用
            return new File(context.getFilesDir(), Environment.DIRECTORY_MOVIES);
        }
    }

    public static File getAudioFilesDir(@NonNull Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            ///外部存储可用
            ///注意：Android 10 Q (API 29) 访问公共外部存储必须MediaStore API！而11 R (API 30)则恢复29之前，即照常访问
//            imageFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            return context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        } else {
            ///外部存储不可用
            return new File(context.getFilesDir(), Environment.DIRECTORY_MUSIC);
        }
    }

    public static File getFilesDir(@NonNull Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            return context.getExternalFilesDir(null);
        } else {
            return context.getFilesDir();
        }
    }

    public static File getCacheDir(@NonNull Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir();
        } else {
            return context.getCacheDir();
        }
    }

}
