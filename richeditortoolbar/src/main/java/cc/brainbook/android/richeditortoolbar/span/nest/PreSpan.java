package cc.brainbook.android.richeditortoolbar.span.nest;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Parcel;
import androidx.annotation.NonNull;

import android.text.style.LineBackgroundSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IUnReadableStyle;

import static cc.brainbook.android.richeditortoolbar.config.Config.PRE_SPAN_BACKGROUND_COLOR;
import static cc.brainbook.android.richeditortoolbar.config.Config.PRE_SPAN_PADDING;

public class PreSpan implements LineBackgroundSpan, INestParagraphStyle, IUnReadableStyle {

    private Rect mBgRect;

    ///[NestingLevel]
    @Expose
    private int mNestingLevel;
    @Override
    public int getNestingLevel() {
        return mNestingLevel;
    }
    @Override
    public void setNestingLevel(int nestingLevel) {
        mNestingLevel = nestingLevel;
    }


    public PreSpan(int nestingLevel) {
        mNestingLevel = nestingLevel;

        ///Pre-create rect for performance
        mBgRect = new Rect();
    }

    ///https://gist.github.com/tokudu/601320d9edb978bcbc31
    @Override
    public void drawBackground(@NonNull Canvas c, @NonNull Paint p, int left, int right, int top, int baseline,
                               int bottom, CharSequence text, int start, int end, int lnum) {
        ///save paint color
        final int paintColor = p.getColor();

        ///Draw background
        mBgRect.set(left - PRE_SPAN_PADDING,
                top - (lnum == 0 ? PRE_SPAN_PADDING / 2 : - (PRE_SPAN_PADDING / 2)),
                right + PRE_SPAN_PADDING,
                bottom + PRE_SPAN_PADDING / 2);
        p.setColor(PRE_SPAN_BACKGROUND_COLOR);
        c.drawRect(mBgRect, p);

        ///restore paint color
        p.setColor(paintColor);
    }

    private void applyTypeface(@NonNull Paint p) {
        ///save paint typeface
        final Typeface typeface = p.getTypeface();
        p.setTypeface(Typeface.MONOSPACE);



        ///restore paint typeface
        p.setTypeface(typeface);
    }


    public static final Creator<PreSpan> CREATOR = new Creator<PreSpan>() {
        @Override
        @NonNull
        public PreSpan createFromParcel(@NonNull Parcel in) {
            final int nestingLevel = in.readInt();

            return new PreSpan(nestingLevel);
        }

        @Override
        @NonNull
        public PreSpan[] newArray(int size) {
            return new PreSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(getNestingLevel());
    }

}
