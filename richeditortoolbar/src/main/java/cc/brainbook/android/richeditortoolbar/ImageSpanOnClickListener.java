package cc.brainbook.android.richeditortoolbar;

import static cc.brainbook.android.richeditortoolbar.constant.Constant.AUDIO_TYPE;
import static cc.brainbook.android.richeditortoolbar.constant.Constant.IMAGE_TYPE;
import static cc.brainbook.android.richeditortoolbar.constant.Constant.VIDEO_TYPE;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import cc.brainbook.android.richeditortoolbar.interfaces.Clickable;
import cc.brainbook.android.richeditortoolbar.span.block.AudioSpan;
import cc.brainbook.android.richeditortoolbar.span.block.CustomImageSpan;
import cc.brainbook.android.richeditortoolbar.span.block.VideoSpan;
import cc.brainbook.android.richeditortoolbar.util.FileProviderUtil;

public class ImageSpanOnClickListener implements CustomImageSpan.OnClickListener {
    private final String mAuthority;

    public ImageSpanOnClickListener(String authority) {
        mAuthority = authority;
    }


    @Override
    public void onClick(@NonNull View view, Clickable clickable, Drawable drawable, String uriString, String source) {
        final Context context = view.getContext();

        final String mediaType = clickable instanceof AudioSpan ? AUDIO_TYPE : clickable instanceof VideoSpan ? VIDEO_TYPE : IMAGE_TYPE;
        final String src = clickable instanceof AudioSpan || clickable instanceof VideoSpan ? uriString : source;
        final Uri mediaUri = FileProviderUtil.parseToUri(context, src, mAuthority);

        if (mediaUri == null) {
            Log.e("TAG-ClickImageSpan", "Image does not exist, or the read and write permissions are not authorized. " + src);
            Toast.makeText(context,
                    context.getString(R.string.click_image_span_dialog_builder_msg_image_does_not_exist,
                            src), Toast.LENGTH_SHORT).show();
        }

        final Intent intent = new Intent(Intent.ACTION_VIEW);

        ///如果Android N及以上，需要添加临时FileProvider的Uri读写权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        intent.setDataAndType(mediaUri, mediaType);

        try {
            context.startActivity(intent);
            ///[FIX#java.lang.SecurityException]
//        } catch (ActivityNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
