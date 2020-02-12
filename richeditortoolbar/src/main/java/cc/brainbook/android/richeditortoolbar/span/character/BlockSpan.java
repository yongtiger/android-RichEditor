package cc.brainbook.android.richeditortoolbar.span.character;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.ReplacementSpan;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;

public class BlockSpan extends ReplacementSpan implements Parcelable, ICharacterStyle {
    private static final int COLOR = Color.GRAY;
    private static final float RADIUS = 10;


    private final Drawable drawable;
    private float padding;
    private int width;

    public BlockSpan() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(COLOR);
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


    public static final Creator<BlockSpan> CREATOR = new Creator<BlockSpan>() {
        @Override
        public BlockSpan createFromParcel(Parcel in) {
            return new BlockSpan();
        }

        @Override
        public BlockSpan[] newArray(int size) {
            return new BlockSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

}
