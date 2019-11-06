package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.text.ParcelableSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.LineHeightSpan;

import java.util.UUID;

///[UPGRADE#LineDividerSpan]
public class LineDividerSpan implements LineHeightSpan, LineBackgroundSpan, ParcelableSpan {
    private static final int SPAN_TYPE_ID = UUID.randomUUID().hashCode();

    ///[implements LineHeightSpan]
    private final int mMarginTop;
    private final int mMarginBottom;

    ///[implements LineBackgroundSpan]
    private LineDividerSpan.DrawBackgroundCallback mDrawBackgroundCallback;

    public LineDividerSpan(int marginTop, int marginBottom, LineDividerSpan.DrawBackgroundCallback drawBackgroundCallback) {
        mMarginTop = marginTop;
        mMarginBottom = marginBottom;

        mDrawBackgroundCallback = drawBackgroundCallback;
    }

    public LineDividerSpan(@NonNull Parcel src) {
        mMarginTop = src.readInt();
        mMarginBottom = src.readInt();
    }

    @Override
    public int getSpanTypeId() {
        return SPAN_TYPE_ID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mMarginTop);
        dest.writeInt(mMarginBottom);
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end,
                             int spanstartv, int lineHeight, Paint.FontMetricsInt fontMetricsInt) {
        if (start + 1 < end ||text.charAt(start) != '\n') {
            return;
        }

        fontMetricsInt.top -= mMarginTop;
        fontMetricsInt.ascent -= mMarginTop;

        fontMetricsInt.bottom += mMarginBottom;
        fontMetricsInt.descent += mMarginBottom;
    }

    ///[implements LineBackgroundSpan]
    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
        if (start + 1 < end ||text.charAt(start) != '\n') {
            return;
        }

        if (mDrawBackgroundCallback != null) {
            mDrawBackgroundCallback.drawBackground(c, p, left, right, top, baseline, bottom, text, start, end, lnum);
        }
    }

    interface DrawBackgroundCallback {
        void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum);
    }

}
