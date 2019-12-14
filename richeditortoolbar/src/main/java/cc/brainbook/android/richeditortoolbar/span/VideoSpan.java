package cc.brainbook.android.richeditortoolbar.span;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.view.View;

public class VideoSpan extends CustomImageSpan {
    public VideoSpan(@NonNull Drawable drawable, @NonNull String uri, @NonNull String source, int verticalAlignment) {
        super(drawable, uri, source, verticalAlignment);
    }

    @Override
    public void onClick(View widget) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        final Uri uri = Uri.parse(getUri());
        intent.setDataAndType(uri, "video/*");
        widget.getContext().startActivity(intent);
    }


    public static final Creator<VideoSpan> CREATOR = new Creator<VideoSpan>() {
        @Override
        public VideoSpan createFromParcel(Parcel in) {
            final String uri = in.readString(); ///media source
            final String source = in.readString();  ///thumbnail
            final int verticalAlignment = in.readInt();

            ///获取Parcel中保存的drawable宽高
            final int drawableWidth = in.readInt();
            final int drawableHeight = in.readInt();

            ///注意：构造要求必须有drawable！设置临时drawable，以后会用GlideImageLoader替换掉
            final Drawable drawable = new ColorDrawable(Color.TRANSPARENT);

            ///注意：Drawable必须设置Bounds才能显示
            drawable.setBounds(0, 0, drawableWidth, drawableHeight);

            return new VideoSpan(drawable, uri, source, verticalAlignment);
        }

        @Override
        public VideoSpan[] newArray(int size) {
            return new VideoSpan[size];
        }
    };
}
