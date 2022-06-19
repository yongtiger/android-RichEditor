package cc.brainbook.android.richeditortoolbar.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;

public abstract class MediaUtil {

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

        try {
            FileUtil.writeFile(videoCoverFile, bitmap, format, quality);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
