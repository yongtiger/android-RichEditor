package cc.brainbook.android.richeditortoolbar.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 在Android N 7.0 API 24及以上的系统中，尝试传递 file://URI可能会触发FileUriExposedException
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
public abstract class FileProviderUtil {

    /**
     * 获取File的FileProviderUri
     *
     * @param context
     * @param file
     * @param authorities
     * @return  FileProviderUri
     */
    @Nullable
    public static Uri getFileProviderUriFromFile(@NonNull Context context, @NonNull File file, String authorities) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return FileProvider.getUriForFile(context, authorities, file);
            } else {
                return Uri.fromFile(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Nullable
    public static File getFileFromFileProviderUri(@NonNull Context context, @NonNull Uri fileProviderUri) {
        String filePath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            filePath = getFilePathFromFileProviderUri(context, fileProviderUri);
        } else {
            filePath = fileProviderUri.getPath();
        }

        if (filePath == null || filePath.length() == 0) {
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
     * 注意：如果uri.getScheme()为null，则返回FileProviderUri
     *
     * @param context
     * @param uriString 任意字符串
     * @param authorities
     * @return
     *          null or 空字符串    =》 null
     *          uri.getScheme()为null  =》 FileProviderUri
     *          其它如file，DocumentUri，"http"等  =》 Uri.parse(uriString)
     */
    @Nullable
    public static Uri parseToUri(@NonNull Context context, String uriString, String authorities) {
        if (TextUtils.isEmpty(uriString)) {
            return null;
        }

        final Uri uri = Uri.parse(uriString);
        if (uri == null) {
            return null;
        }

        ///比如“/storage/emulated/0/Android/data/cc.brainbook.android.richeditor/files/Pictures/20201003050111.jpg”，或无效文本
        if (uri.getScheme() == null) {
            return FileProviderUtil.getFileProviderUriFromFilePath(context, uriString, authorities);    ///content://cc.brainbook.android.richeditor.file.path.share/external_files_path/Pictures/20220612084351.jpg
        }

        return uri;
    }

}
