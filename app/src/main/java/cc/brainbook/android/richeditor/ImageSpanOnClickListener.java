package cc.brainbook.android.richeditor;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import cc.brainbook.android.richeditortoolbar.interfaces.Clickable;
import cc.brainbook.android.richeditortoolbar.span.block.AudioSpan;
import cc.brainbook.android.richeditortoolbar.span.block.CustomImageSpan;
import cc.brainbook.android.richeditortoolbar.span.block.VideoSpan;
import cc.brainbook.android.richeditortoolbar.util.UriUtil;

import static cc.brainbook.android.richeditor.EditorActivity.PROVIDER_AUTHORITIES;

public class ImageSpanOnClickListener implements CustomImageSpan.OnClickListener {
    @Override
    public void onClick(@NonNull View view, Clickable clickable, Drawable drawable, String uriString, String source) {
        final Context context = view.getContext();

        final Intent intent = new Intent(Intent.ACTION_VIEW);
        final String mediaType = clickable instanceof AudioSpan ? "audio/*" : clickable instanceof VideoSpan ? "video/*" : "image/*";
        final Uri mediaUri = UriUtil.parseToUri(context, clickable instanceof AudioSpan || clickable instanceof VideoSpan ? uriString : source,
                context.getPackageName() + PROVIDER_AUTHORITIES);

        ///如果Android N及以上，需要添加临时FileProvider的Uri读写权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.setDataAndType(mediaUri, mediaType);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Activity was not found for intent, " + intent.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
