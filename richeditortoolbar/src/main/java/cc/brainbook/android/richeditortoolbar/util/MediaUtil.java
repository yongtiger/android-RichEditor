package cc.brainbook.android.richeditortoolbar.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    @Nullable
    public static File generateVideoCover(@NonNull Context context, @NonNull Uri videoUri, File videoCoverFile,
                                          Bitmap.CompressFormat format, int quality) {
        Bitmap bitmap = null;
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, videoUri);
            bitmap = mmr.getFrameAtTime(); ///第一帧图片
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mmr != null) {
                try {
                    mmr.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (bitmap != null) {
            try {
                FileUtil.writeFile(videoCoverFile, bitmap, format, quality);

                return videoCoverFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
