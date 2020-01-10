package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.text.Layout;
import android.text.style.BulletSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.IParagraphStyle;

public class CustomBulletSpan extends BulletSpan implements IParagraphStyle {
    // Bullet is slightly bigger to avoid aliasing artifacts on mdpi devices.
    private static final int STANDARD_BULLET_RADIUS = 20;
    public static final int STANDARD_GAP_WIDTH = 40;
    @ColorInt
    private static final int STANDARD_COLOR = 0xffdddddd;


    ///[Gson#Exclude父类成员变量的序列化和反序列化]
    ///Exclude后父类成员变量不被序列化，因此需要重新声明并设置@Expose
    @Expose
    @Px
    private final int mGapWidth, mBulletRadius;

    private Path mBulletPath = null;
    @Expose
    @ColorInt
    private final int mColor;
    @Expose
    private final boolean mWantColor;


    public CustomBulletSpan() {
        this(STANDARD_GAP_WIDTH, STANDARD_COLOR, false, STANDARD_BULLET_RADIUS);
    }
    public CustomBulletSpan(int gapWidth) {
        this(gapWidth, STANDARD_COLOR, false, STANDARD_BULLET_RADIUS);
    }
    public CustomBulletSpan(int gapWidth, @ColorInt int color) {
        this(gapWidth, color, true, STANDARD_BULLET_RADIUS);
    }
    public CustomBulletSpan(int gapWidth, @ColorInt int color, @IntRange(from = 0) int bulletRadius) {
        this(gapWidth, color, true, bulletRadius);
    }
    private CustomBulletSpan(int gapWidth, @ColorInt int color, boolean wantColor,
                       @IntRange(from = 0) int bulletRadius) {
        mGapWidth = gapWidth;
        mBulletRadius = bulletRadius;
        mColor = color;
        mWantColor = wantColor;
    }

    /**
     * Get the distance, in pixels, between the bullet point and the paragraph.
     *
     * @return the distance, in pixels, between the bullet point and the paragraph.
     */
    public int getGapWidth() {
        return mGapWidth;
    }

    /**
     * Get the radius, in pixels, of the bullet point.
     *
     * @return the radius, in pixels, of the bullet point.
     */
    public int getBulletRadius() {
        return mBulletRadius;
    }

    /**
     * Get the bullet point color.
     *
     * @return the bullet point color
     */
    public int getColor() {
        return mColor;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return 2 * mBulletRadius + mGapWidth;
    }

    @Override
    public void drawLeadingMargin(@NonNull Canvas canvas, @NonNull Paint paint, int x, int dir,
                                  int top, int baseline, int bottom,
                                  @NonNull CharSequence text, int start, int end,
                                  boolean first, @Nullable Layout layout) {
        if (first) {
            Paint.Style style = paint.getStyle();
            int oldColor = 0;

            if (mWantColor) {
                oldColor = paint.getColor();
                paint.setColor(mColor);
            }

            paint.setStyle(Paint.Style.FILL);

//////??????
//            if (layout != null) {
//                // "bottom" position might include extra space as a result of line spacing
//                // configuration. Subtract extra space in order to show bullet in the vertical
//                // center of characters.
//                final int line = layout.getLineForOffset(start);
////                bottom = bottom - layout.getLineExtra(line);  ///hidden!!!
//            }

            final float yPosition = (top + bottom) / 2f;
            final float xPosition = x + dir * mBulletRadius;

            if (canvas.isHardwareAccelerated()) {
                if (mBulletPath == null) {
                    mBulletPath = new Path();
                    mBulletPath.addCircle(0.0f, 0.0f, mBulletRadius, Path.Direction.CW);
                }

                canvas.save();
                canvas.translate(xPosition, yPosition);
                canvas.drawPath(mBulletPath, paint);
                canvas.restore();
            } else {
                canvas.drawCircle(xPosition, yPosition, mBulletRadius, paint);
            }

            if (mWantColor) {
                paint.setColor(oldColor);
            }

            paint.setStyle(style);
        }
    }


    public static final Creator<CustomBulletSpan> CREATOR = new Creator<CustomBulletSpan>() {
        @Override
        public CustomBulletSpan createFromParcel(Parcel in) {
            final int gapWidth = in.readInt();
            @ColorInt final int color = in.readInt();
            final boolean wantColor = in.readInt() == 1;
            @IntRange(from = 0) final int bulletRadius = in.readInt();

            return new CustomBulletSpan(gapWidth, color, wantColor, bulletRadius);
        }

        @Override
        public CustomBulletSpan[] newArray(int size) {
            return new CustomBulletSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mGapWidth);
        dest.writeInt(mBulletRadius);
        dest.writeInt(mColor);
        dest.writeInt(mWantColor ? 1 : 0);
    }
}
