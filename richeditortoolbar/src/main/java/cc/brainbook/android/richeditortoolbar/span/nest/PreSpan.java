package cc.brainbook.android.richeditortoolbar.span.nest;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.text.style.LineBackgroundSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;

///[PreSpan]注意：cc.brainbook.android.richeditortoolbar.helper.Html要求PreSpan同时继承ParagraphStyle和ICharacterStyle！
public class PreSpan implements LineBackgroundSpan, Parcelable, INestParagraphStyle, ICharacterStyle {
    @ColorInt
    public static final int BACKGROUND_COLOR = 0xffdddddd;
    public static final int PADDING = 0;

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
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline,
                               int bottom, CharSequence text, int start, int end, int lnum) {
        ///save paint color
        final int paintColor = p.getColor();

        ///Draw background
        mBgRect.set(left - PADDING,
                top - (lnum == 0 ? PADDING / 2 : - (PADDING / 2)),
                right + PADDING,
                bottom + PADDING / 2);
        p.setColor(BACKGROUND_COLOR);
        c.drawRect(mBgRect, p);

        ///restore paint color
        p.setColor(paintColor);
    }

    private void applyTypeface(Paint p) {
        ///save paint typeface
        final Typeface typeface = p.getTypeface();
        p.setTypeface(Typeface.MONOSPACE);



        ///restore paint typeface
        p.setTypeface(typeface);
    }


    public static final Creator<PreSpan> CREATOR = new Creator<PreSpan>() {
        @Override
        public PreSpan createFromParcel(Parcel in) {
            final int nestingLevel = in.readInt();

            return new PreSpan(nestingLevel);
        }

        @Override
        public PreSpan[] newArray(int size) {
            return new PreSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getNestingLevel());
    }

}
