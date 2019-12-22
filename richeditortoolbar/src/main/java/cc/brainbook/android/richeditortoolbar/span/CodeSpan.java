package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.ReplacementSpan;

public class CodeSpan extends ReplacementSpan implements Parcelable {

    private static final int CODE_COLOR = Color.parseColor("#F0F0F0");
    private static final float RADIUS = 10;

    private Drawable drawable;
    private float padding;
    private int width;

    public CodeSpan() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(CODE_COLOR);
        d.setCornerRadius(RADIUS);
        drawable = d;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        padding = paint.measureText("t");
        width = (int) (paint.measureText(text, start, end) + padding * 4);
        return width;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        drawable.setBounds((int) (x + padding), top, (int) (x - padding) + width, bottom);
        drawable.draw(canvas);
        canvas.drawText(text, start, end, x + padding * 2, y, paint);
    }


    public static final Creator<CodeSpan> CREATOR = new Creator<CodeSpan>() {
        @Override
        public CodeSpan createFromParcel(Parcel in) {
            ///注意：必须按照成员变量声明的顺序！
            return new CodeSpan();
        }

        @Override
        public CodeSpan[] newArray(int size) {
            return new CodeSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

}
