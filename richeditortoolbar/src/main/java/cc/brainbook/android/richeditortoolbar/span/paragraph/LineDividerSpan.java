package cc.brainbook.android.richeditortoolbar.span.paragraph;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.LineBackgroundSpan;
import android.text.style.LineHeightSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.IParagraphStyle;

///[UPGRADE#LineDividerSpan]
public class LineDividerSpan implements LineHeightSpan, LineBackgroundSpan, Parcelable, IParagraphStyle {
    public static final int DEFAULT_MARGIN_TOP = 0;
    public static final int DEFAULT_MARGIN_BOTTOM = 0;


    ///[implements LineHeightSpan]
    @Expose
    private final int mMarginTop, mMarginBottom;

    ///[implements LineBackgroundSpan]
    private DrawBackgroundCallback mDrawBackgroundCallback;
    public void setDrawBackgroundCallback(DrawBackgroundCallback drawBackgroundCallback) {
        mDrawBackgroundCallback = drawBackgroundCallback;
    }


    public LineDividerSpan() {
        this(DEFAULT_MARGIN_TOP, DEFAULT_MARGIN_BOTTOM);
    }

    public LineDividerSpan(int marginTop, int marginBottom) {
        mMarginTop = marginTop;
        mMarginBottom = marginBottom;
    }

    public LineDividerSpan(int marginTop, int marginBottom, DrawBackgroundCallback drawBackgroundCallback) {
        this(marginTop, marginBottom);

        mDrawBackgroundCallback = drawBackgroundCallback;
    }


    @Override
    public void chooseHeight(CharSequence text, int start, int end,
                             int spanstartv, int lineHeight, Paint.FontMetricsInt fontMetricsInt) {
        if (start + 1 == end && text.charAt(start) == '\n') {
            fontMetricsInt.top -= mMarginTop;
            fontMetricsInt.ascent -= mMarginTop;

            fontMetricsInt.bottom += mMarginBottom;
            fontMetricsInt.descent += mMarginBottom;
        }
    }

    ///[implements LineBackgroundSpan]
    ///drawBackground() is timed to the rate of the flashing cursor which is about 500 ms
    ///https://stackoverflow.com/questions/43611613/linebackgroundspan-drawbackground-called-repeatedly
    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
        if (mDrawBackgroundCallback != null && start + 1 == end && text.charAt(start) == '\n') {
            mDrawBackgroundCallback.drawBackground(c, p, left, right, top, baseline, bottom, text, start, end, lnum);
        }
    }

    public interface DrawBackgroundCallback {
        void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum);
    }


    public static final Creator<LineDividerSpan> CREATOR = new Creator<LineDividerSpan>() {
        @Override
        public LineDividerSpan createFromParcel(Parcel in) {
            final int marginTop = in.readInt();
            final int marginBottom = in.readInt();

            return new LineDividerSpan(marginTop, marginBottom);
        }

        @Override
        public LineDividerSpan[] newArray(int size) {
            return new LineDividerSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mMarginTop);
        dest.writeInt(mMarginBottom);
    }

}
