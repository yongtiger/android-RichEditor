package cc.brainbook.android.richeditortoolbar.span.block;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import androidx.annotation.NonNull;

import cc.brainbook.android.richeditortoolbar.interfaces.IBlockCharacterStyle;

public class AudioSpan extends CustomImageSpan implements IBlockCharacterStyle {

    public AudioSpan(@NonNull Drawable drawable, @NonNull String uri, @NonNull String source) {
        super(drawable, uri, source, ALIGN_BOTTOM);
    }

    public AudioSpan(@NonNull Drawable drawable, @NonNull String uri, @NonNull String source, int verticalAlignment) {
        super(drawable, uri, source, verticalAlignment);
    }

    public AudioSpan(@NonNull Drawable drawable, @NonNull String uri, @NonNull String source, int verticalAlignment,
                     int imageWidth, int imageHeight) {
        super(drawable, uri, source, verticalAlignment, imageWidth, imageHeight);
    }


    public static final Creator<AudioSpan> CREATOR = new Creator<AudioSpan>() {
        @Override
        @NonNull
        public AudioSpan createFromParcel(@NonNull Parcel in) {
            final String uri = in.readString(); ///media source
            final String source = in.readString();  ///thumbnail
            final int verticalAlignment = in.readInt();

            ///获取Parcel中保存的Image宽高
            final int imageWidth = in.readInt();
            final int imageHeight = in.readInt();

            ///获取Parcel中保存的drawable宽高
            final int drawableWidth = in.readInt();
            final int drawableHeight = in.readInt();

            ///注意：构造要求必须有drawable！设置临时drawable，以后会用GlideImageLoader替换掉
            final Drawable drawable = new ColorDrawable(Color.TRANSPARENT);

            ///注意：Drawable必须设置Bounds才能显示
            drawable.setBounds(0, 0, drawableWidth, drawableHeight);

            return new AudioSpan(drawable, uri, source, verticalAlignment, imageWidth, imageHeight);
        }

        @Override
        @NonNull
        public AudioSpan[] newArray(int size) {
            return new AudioSpan[size];
        }
    };

}
