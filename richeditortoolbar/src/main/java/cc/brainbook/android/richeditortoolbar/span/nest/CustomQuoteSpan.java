package cc.brainbook.android.richeditortoolbar.span.nest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;

///D:\AndroidStudioProjects\_demo_module\_rich_editor\zzhoujay-RichEditor\richeditor\src\main\java\com\zzhoujay\richeditor\span\QuoteSpan.java
///D:\AndroidStudioProjects\_demo_module\_rich_editor\yuruiyin-RichEditor\richeditor\src\main\java\com\yuruiyin\richeditor\span\CustomQuoteSpan.java
public class CustomQuoteSpan implements LeadingMarginSpan, INestParagraphStyle {
    /**
     * Default color for the quote stripe.
     */
    @ColorInt
    public static final int STANDARD_COLOR = Color.GRAY;//////////////////

    /**
     * Default stripe width in pixels.
     */
    public static final int STANDARD_STRIPE_WIDTH_PX = 16;//////////////////

    /**
     * Default gap width in pixels.
     */
    public static final int STANDARD_GAP_WIDTH_PX = 40;//////////////////


    ///[Gson#Exclude父类成员变量的序列化和反序列化]
    ///Exclude后父类成员变量不被序列化，因此需要重新声明并设置@Expose
    @Expose
    @ColorInt
    private final int mColor;

    @Expose
    @Px
    private final int mStripeWidth, mGapWidth;

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


    public CustomQuoteSpan() {
        this(1, STANDARD_COLOR, STANDARD_STRIPE_WIDTH_PX, STANDARD_GAP_WIDTH_PX);
    }
    public CustomQuoteSpan(int nestingLevel) {
        this(nestingLevel, STANDARD_COLOR, STANDARD_STRIPE_WIDTH_PX, STANDARD_GAP_WIDTH_PX);
    }
    public CustomQuoteSpan(int nestingLevel, @ColorInt int color, @IntRange(from = 0) int stripeWidth,
                           @IntRange(from = 0) int gapWidth) {
        mNestingLevel = nestingLevel;
        mColor = color;
        mStripeWidth = stripeWidth;
        mGapWidth = gapWidth;
    }


    /**
     * Get the width of the quote stripe.
     *
     * @return the width of the quote stripe.
     */
    public int getStripeWidth() {
        return mStripeWidth;
    }

    /**
     * Get the width of the gap between the stripe and the text.
     *
     * @return the width of the gap between the stripe and the text.
     */
    public int getGapWidth() {
        return mGapWidth;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return mStripeWidth + mGapWidth;
    }

    @Override
    public void drawLeadingMargin(@NonNull Canvas c, @NonNull Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        Paint.Style style = p.getStyle();
        int color = p.getColor();

        p.setStyle(Paint.Style.FILL);
        p.setColor(mColor);

        ///由于无法确定CustomQuoteSpan是否先执行，所以无法保证返回正确的x，所以不用x！
//        c.drawRect(x, top, x + dir * mStripeWidth, bottom, p);
        final float transX = (mStripeWidth + mGapWidth) * (getNestingLevel() - 1);
        c.drawRect(transX, top, transX + dir * mStripeWidth, bottom, p);

        p.setStyle(style);
        p.setColor(color);
    }


    public static final Creator<CustomQuoteSpan> CREATOR = new Creator<CustomQuoteSpan>() {
        @Override
        @NonNull
        public CustomQuoteSpan createFromParcel(@NonNull Parcel in) {
            final int nestingLevel = in.readInt();
            @ColorInt int color = in.readInt();
            @Px final int stripeWidth = in.readInt();
            @Px final int gapWidth = in.readInt();

            return new CustomQuoteSpan(nestingLevel, color, stripeWidth, gapWidth);
        }

        @Override
        @NonNull
        public CustomQuoteSpan[] newArray(int size) {
            return new CustomQuoteSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(getNestingLevel());
        dest.writeInt(mColor);
        dest.writeInt(mStripeWidth);
        dest.writeInt(mGapWidth);
    }
}
