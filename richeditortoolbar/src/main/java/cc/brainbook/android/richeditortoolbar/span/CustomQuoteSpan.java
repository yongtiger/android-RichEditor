package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.Px;
import android.text.Layout;
import android.text.style.QuoteSpan;

///D:\AndroidStudioProjects\_demo_module\_rich_editor\zzhoujay-RichEditor\richeditor\src\main\java\com\zzhoujay\richeditor\span\QuoteSpan.java
///D:\AndroidStudioProjects\_demo_module\_rich_editor\yuruiyin-RichEditor\richeditor\src\main\java\com\yuruiyin\richeditor\span\CustomQuoteSpan.java
public class CustomQuoteSpan extends QuoteSpan {
    /**
     * Default color for the quote stripe.
     */
    @ColorInt
    public static final int STANDARD_COLOR = 0xffdddddd;

    /**
     * Default stripe width in pixels.
     */
    public static final int STANDARD_STRIPE_WIDTH_PX = 16;

    /**
     * Default gap width in pixels.
     */
    public static final int STANDARD_GAP_WIDTH_PX = 40;

    @ColorInt
    private final int mColor;
    @Px
    private final int mStripeWidth;
    @Px
    private final int mGapWidth;

    public CustomQuoteSpan() {
        this(STANDARD_COLOR, STANDARD_STRIPE_WIDTH_PX, STANDARD_GAP_WIDTH_PX);
    }
    public CustomQuoteSpan(@ColorInt int color) {
        this(color, STANDARD_STRIPE_WIDTH_PX, STANDARD_GAP_WIDTH_PX);
    }
    public CustomQuoteSpan(@ColorInt int color, @IntRange(from = 0) int stripeWidth,
                           @IntRange(from = 0) int gapWidth) {
        super(color);
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
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        Paint.Style style = p.getStyle();
        int color = p.getColor();

        p.setStyle(Paint.Style.FILL);
        p.setColor(getColor());

        c.drawRect(x, top, x + dir * mStripeWidth, bottom, p);

        p.setStyle(style);
        p.setColor(color);
    }


    public static final Creator<CustomQuoteSpan> CREATOR = new Creator<CustomQuoteSpan>() {
        @Override
        public CustomQuoteSpan createFromParcel(Parcel in) {
            ///注意：必须按照成员变量声明的顺序！
            @ColorInt int color = in.readInt();
            @Px final int stripeWidth = in.readInt();
            @Px final int gapWidth = in.readInt();
            return new CustomQuoteSpan(color, stripeWidth, gapWidth);
        }

        @Override
        public CustomQuoteSpan[] newArray(int size) {
            return new CustomQuoteSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mColor);
        dest.writeInt(mStripeWidth);
        dest.writeInt(mGapWidth);
    }
}
