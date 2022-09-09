package cc.brainbook.android.richeditortoolbar.span.block;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.annotations.Expose;

import java.lang.ref.WeakReference;

import cc.brainbook.android.richeditortoolbar.interfaces.Clickable;
import cc.brainbook.android.richeditortoolbar.interfaces.IBlockCharacterStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IUnReadableStyle;

//////??????注意：API29开始支持ALIGN_CENTER，但存在bug！
///https://developer.android.com/reference/android/text/style/DynamicDrawableSpan#ALIGN_CENTER
public class CustomImageSpan extends ImageSpan implements Clickable, IBlockCharacterStyle, IUnReadableStyle {
    public static final int MEDIA_TYPE = 0;
    public static final int ALIGN_CENTER = 2;


    @Nullable
    @Expose
    private String mUri;    ///若MEDIA_TYPE为0（图像）则空，否则为media的Uri
    @Nullable
    public String getUri() {
        return mUri;
    }
    public void setUri(String uri) {
        mUri = uri;
    }

    ///[Gson#Exclude父类成员变量的序列化和反序列化]
    ///Exclude后父类成员变量不被序列化，因此需要重新声明并设置@Expose
    @Expose
    private String mSource; ///image source，若MEDIA_TYPE不为0，则代表封面图片的source
    public String getSource() {
        return mSource;
    }
    public void setSource(String source) {
        mSource = source;
    }
    private WeakReference<Drawable> mDrawableRef;


    @Expose
    private final int mImageWidth, mImageHeight;    ///实际存储的Image宽高
    public int getImageWidth() {
        return mImageWidth;
    }
    public int getImageHeight() {
        return mImageHeight;
    }

    @Expose
    private final int mDrawableWidth, mDrawableHeight;    ///实际绘制图像宽高，仅供显示，不影响mImageWidth, mImageHeight
    public int getDrawableWidth() {
        return mDrawableWidth;
    }
    public int getDrawableHeight() {
        return mDrawableHeight;
    }
    @Expose
    private final int mVerticalAlignment;


    public CustomImageSpan(@NonNull Drawable drawable, @NonNull String source) {
        this(drawable, null, source, ALIGN_BOTTOM);
    }

    public CustomImageSpan(@NonNull Drawable drawable, @NonNull String source, int verticalAlignment) {
        this(drawable, null, source, verticalAlignment);
    }

    public CustomImageSpan(@NonNull Drawable drawable, @Nullable String uri, @NonNull String source, int verticalAlignment) {
        super(drawable, source, verticalAlignment);
        mUri = uri;
        mSource = source;
        mDrawableWidth = drawable.getBounds().right;
        mDrawableHeight = drawable.getBounds().bottom;
        mVerticalAlignment = verticalAlignment;
        mImageWidth = mDrawableWidth;
        mImageHeight = mDrawableHeight;
    }

    public CustomImageSpan(@NonNull Drawable drawable, @Nullable String uri, @NonNull String source, int verticalAlignment,
                           int imageWidth, int imageHeight) {
        super(drawable, source, verticalAlignment);
        mUri = uri;
        mSource = source;
        mDrawableWidth = drawable.getBounds().right;
        mDrawableHeight = drawable.getBounds().bottom;
        mVerticalAlignment = verticalAlignment;
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
    }

    ///https://segmentfault.com/a/1190000007133405
    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end,
                                 Paint.FontMetricsInt fm) {
        if (mVerticalAlignment != ALIGN_CENTER) {
            return super.getSize(paint, text, start, end, fm);
        }

        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.descent - fmPaint.ascent;
            int drHeight = rect.bottom - rect.top;
            int centerY = fmPaint.ascent + fontHeight / 2;

            fm.ascent = centerY - drHeight / 2;
            fm.top = fm.ascent;
            fm.bottom = centerY + drHeight / 2;
            fm.descent = fm.bottom;
        }
        return rect.right;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
                     int bottom, @NonNull Paint paint) {
        try {
            if (mVerticalAlignment != ALIGN_CENTER) {
                super.draw(canvas, text, start, end, x, top, y, bottom, paint);
                return;
            }

            Drawable drawable = getCachedDrawable();
            canvas.save();
            Paint.FontMetricsInt fm = paint.getFontMetricsInt();
//        int transY = (bottom - top) / 2 - b.getBounds().height() / 2;   //////??????注意：API29开始支持ALIGN_CENTER，但存在bug！

            int transY = (y + fm.descent + y + fm.ascent) / 2 - drawable.getBounds().bottom / 2;    ///实测有效！https://www.jianshu.com/p/add321678859

            ///https://segmentfault.com/a/1190000007133405，但存在bug！
//        int fontHeight = fm.descent - fm.ascent;
//        int centerY = y + fm.descent - fontHeight / 2;
//        int transY = centerY - (drawable.getBounds().bottom - drawable.getBounds().top) / 2;

            canvas.translate(x, transY);
            drawable.draw(canvas);
            canvas.restore();
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            Log.e("TAG", "CustomImageSpan# draw# ", error);
        }
    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;
        if (wr != null) {
            d = wr.get();
        }

        if (d == null) {
            d = getDrawable();
            mDrawableRef = new WeakReference<Drawable>(d);
        }

        return d;
    }


    public static final Creator<CustomImageSpan> CREATOR = new Creator<CustomImageSpan>() {
        @Override
        @NonNull
        public CustomImageSpan createFromParcel(@NonNull Parcel in) {
            final String uri = in.readString();
            final String source = in.readString();
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

            assert source != null;
            return new CustomImageSpan(drawable, uri, source, verticalAlignment, imageWidth, imageHeight);
        }

        @Override
        @NonNull
        public CustomImageSpan[] newArray(int size) {
            return new CustomImageSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mUri);
        dest.writeString(mSource);
        dest.writeInt(mVerticalAlignment);

        ///保存Image的宽高到Parcel
        dest.writeInt(mImageWidth);
        dest.writeInt(mImageHeight);

        ///保存drawable的宽高到Parcel
        dest.writeInt(mDrawableWidth);
        dest.writeInt(mDrawableHeight);
    }


    ///[CustomImageSpan.OnClickListener]
    public interface OnClickListener {
        void onClick(View widget, Clickable clickable, Drawable drawable, String uriString, String source);
    }
    private OnClickListener mOnClickListener;
    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }
    @Override
    public void onClick(View widget) {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(widget, this, getDrawable(), getUri(), getSource());
        }
    }

}
