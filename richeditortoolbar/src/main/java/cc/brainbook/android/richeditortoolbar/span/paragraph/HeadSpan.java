package cc.brainbook.android.richeditortoolbar.span.paragraph;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.LineHeightSpan;
import android.text.style.RelativeSizeSpan;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.IParagraphStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IReadableStyle;

import static cc.brainbook.android.richeditortoolbar.config.Config.HEAD_SPAN_DEFAULT_MARGIN_BOTTOM;
import static cc.brainbook.android.richeditortoolbar.config.Config.HEAD_SPAN_DEFAULT_MARGIN_TOP;
import static cc.brainbook.android.richeditortoolbar.config.Config.HEAD_SPAN_HEADING_SIZES;

///D:\AndroidStudioProjects\_demo_module\_rich_editor\zzhoujay-RichEditor\richeditor\src\main\java\com\zzhoujay\richeditor\span\HeadSpan.java
public class HeadSpan extends RelativeSizeSpan implements LineHeightSpan, IParagraphStyle, IReadableStyle {

    @Expose
    private final int mLevel;
    public int getLevel() {
        return mLevel;
    }


    public HeadSpan(int level) {
        this(level, HEAD_SPAN_DEFAULT_MARGIN_TOP[level], HEAD_SPAN_DEFAULT_MARGIN_BOTTOM[level]);
    }

    public HeadSpan(int level, int marginTop, int marginBottom) {
        super(HEAD_SPAN_HEADING_SIZES[level]);
        mLevel = level;
        mMarginTop = marginTop;
        mMarginBottom = marginBottom;
    }


    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        applyStyle(ds);
    }

    @Override
    public void updateMeasureState(TextPaint ds) {
        super.updateMeasureState(ds);
        applyStyle(ds);
    }

    ///StyleSpan#apply(Paint paint, int style)
    private static void applyStyle(@NonNull Paint paint) {
        int style = Typeface.BOLD;
        int oldStyle;

        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int want = oldStyle | style;

        Typeface tf;
        if (old == null) {
            tf = Typeface.defaultFromStyle(want);
        } else {
            tf = Typeface.create(old, want);
        }

        int fake = want & ~tf.getStyle();

        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }


    /* ----------- LineHeightSpan ------------ */
    @Expose
    private final int mMarginTop;
    public int getMarginTop() {
        return mMarginTop;
    }
    @Expose
    private final int mMarginBottom;
    public int getMarginBottom() {
        return mMarginBottom;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm) {
        if (lineHeight == 0) {  ///避免多行文本时重复叠加计算
            fm.top -= mMarginTop;
            fm.ascent -= mMarginTop;

            fm.bottom += mMarginBottom;
            fm.descent += mMarginBottom;
        }
    }


    public static final Creator<HeadSpan> CREATOR = new Creator<HeadSpan>() {
        @Override
        @NonNull
        public HeadSpan createFromParcel(@NonNull Parcel in) {
            final int level = in.readInt();
            final int marginTop = in.readInt();
            final int marginBottom = in.readInt();

            return new HeadSpan(level, marginTop, marginBottom);
        }

        @Override
        @NonNull
        public HeadSpan[] newArray(int size) {
            return new HeadSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(mLevel);
        dest.writeInt(mMarginTop);
        dest.writeInt(mMarginBottom);
    }

}
