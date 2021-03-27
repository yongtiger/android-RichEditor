package cc.brainbook.android.richeditortoolbar.span.character;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;

///https://www.jianshu.com/p/deb28c22852a
public class BorderSpan  extends ReplacementSpan implements ICharacterStyle {
    private static final int COLOR = Color.GRAY;//////////////////

    private final Paint mPaint;
    private int mWidth;


    public BorderSpan() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(COLOR);
        mPaint.setAntiAlias(true);
    }


    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        //return text with relative to the Paint
        mWidth = (int) paint.measureText(text, start, end);
        return mWidth;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        //draw the frame with custom Paint
        canvas.drawRect(x, top, x + mWidth, bottom, mPaint);
        canvas.drawText(text, start, end, x, y, paint);
    }


    public static final Creator<BorderSpan> CREATOR = new Creator<BorderSpan>() {
        @Override
        @NonNull
        public BorderSpan createFromParcel(Parcel in) {
            return new BorderSpan();
        }

        @Override
        @NonNull
        public BorderSpan[] newArray(int size) {
            return new BorderSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

}
