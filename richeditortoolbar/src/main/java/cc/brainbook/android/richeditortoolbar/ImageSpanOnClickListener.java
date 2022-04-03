package cc.brainbook.android.richeditortoolbar;

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
import cc.brainbook.android.richeditortoolbar.util.UriUtil;

public class ImageSpanOnClickListener implements CustomImageSpan.OnClickListener {
    private final String mAuthority;

    public ImageSpanOnClickListener(String authority) {
        mAuthority = authority;
    }


    @Override
    public void onClick(@NonNull View view, Clickable clickable, Drawable drawable, String uriString, String source) {
        final Context context = view.getContext();

        final Intent intent = new Intent(Intent.ACTION_VIEW);
        final String mediaType = clickable instanceof AudioSpan ? "audio/*" : clickable instanceof VideoSpan ? "video/*" : "image/*";
        final String src = clickable instanceof AudioSpan || clickable instanceof VideoSpan ? uriString : source;
        final Uri mediaUri = UriUtil.parseToUri(context, src, mAuthority);

        if (mediaUri == null) {
            Log.e("TAG-ClickImageSpan", "Image does not exist, or the read and write permissions are not authorized. " + src);
            Toast.makeText(context.getApplicationContext(),
                    context.getString(R.string.click_image_span_dialog_builder_msg_image_does_not_exist,
                            src), Toast.LENGTH_SHORT).show();
        }

        ///如果Android N及以上，需要添加临时FileProvider的Uri读写权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.setDataAndType(mediaUri, mediaType);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.click_image_span_dialog_builder_msg_activity_not_found), Toast.LENGTH_SHORT).show();
        }
    }

}
