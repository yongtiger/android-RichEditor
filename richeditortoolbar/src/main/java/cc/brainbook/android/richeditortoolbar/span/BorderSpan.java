package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

///https://www.jianshu.com/p/deb28c22852a
public class BorderSpan  extends ReplacementSpan {

    private final Paint mPaint;
    private int mWidth;

    public BorderSpan() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        //return text with relative to the Paint
        mWidth = (int) paint.measureText(text, start, end);
        return mWidth;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        //draw the frame with custom Paint
        canvas.drawRect(x, top, x + mWidth, bottom, mPaint);
        canvas.drawText(text, start, end, x, y, paint);
    }
}
