package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Paint;
import android.text.style.LineHeightSpan;

///[UPGRADE#LineMarginSpan]
public class LineMarginSpan implements LineHeightSpan {

    private final int mMarginTop;
    private final int mMarginBottom;

    public LineMarginSpan(int mMarginTop, int mMarginBottom) {
        this.mMarginTop = mMarginTop;
        this.mMarginBottom = mMarginBottom;
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
}
