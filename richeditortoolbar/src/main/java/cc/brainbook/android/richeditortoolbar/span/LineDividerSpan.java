package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;
import android.text.style.LineHeightSpan;

///[UPGRADE#LineDividerSpan]
public class LineDividerSpan implements LineHeightSpan, LineBackgroundSpan {
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

}
