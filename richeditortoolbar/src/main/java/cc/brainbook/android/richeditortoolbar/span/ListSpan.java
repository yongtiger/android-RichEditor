package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import cc.brainbook.android.richeditortoolbar.util.ListSpanUtil;

import static cc.brainbook.android.richeditortoolbar.util.ListSpanUtil.INDICATOR_TEXT_LIST_TYPE_UNORDERED_CIRCLE;
import static cc.brainbook.android.richeditortoolbar.util.ListSpanUtil.LIST_TYPE_UNORDERED_CIRCLE;

public class ListSpan implements LeadingMarginSpan, Parcelable {
    public static final int DEFAULT_LIST_TYPE = LIST_TYPE_UNORDERED_CIRCLE;
    public static final String DEFAULT_INDICATOR_TEXT = INDICATOR_TEXT_LIST_TYPE_UNORDERED_CIRCLE;
    @IntRange(from = 0) public static final int DEFAULT_NESTING_LEVEL = 0;
    @IntRange(from = 1) public static final int DEFAULT_ORDER_INDEX = 1;
    @IntRange(from = 0) public static final int DEFAULT_INDENT_WIDTH = 80;
    @IntRange(from = 0) public static final int DEFAULT_INDICATOR_WIDTH = 20;
    @IntRange(from = 0) public static final int DEFAULT_INDICATOR_GAP_WIDTH = 40;
    @ColorInt public static final int DEFAULT_INDICATOR_COLOR = 0xdddddd;


    ///[ListType]
    private final int mListType;

    ///[IndicatorText]
    private final String mIndicatorText;

    ///[NestingLevel]
    @IntRange(from = 0) private final int mNestingLevel;

    ///[OrderIndex]
    @IntRange(from = 1) private final int mOrderIndex;

    ///[IndentWidth]
    @IntRange(from = 0) private final int mIndentWidth;

    ///[Indicator]
    @IntRange(from = 0)  private final int mIndicatorWidth;
    @IntRange(from = 0)  private final int mIndicatorGapWidth;
    @ColorInt private final int mIndicatorColor;

    private final boolean mWantColor;


    public ListSpan() {
        this(DEFAULT_LIST_TYPE, DEFAULT_INDICATOR_TEXT, DEFAULT_NESTING_LEVEL, DEFAULT_ORDER_INDEX, DEFAULT_INDENT_WIDTH,
                DEFAULT_INDICATOR_WIDTH, DEFAULT_INDICATOR_GAP_WIDTH, DEFAULT_INDICATOR_COLOR, false);
    }
    public ListSpan(int listType,
                    String indicatorText,
                    @IntRange(from = 0) int nestingLevel,
                    @IntRange(from = 1) int orderIndex) {
        this(listType, indicatorText, nestingLevel, orderIndex, DEFAULT_INDENT_WIDTH,
                DEFAULT_INDICATOR_WIDTH, DEFAULT_INDICATOR_GAP_WIDTH, DEFAULT_INDICATOR_COLOR, false);
    }
    public ListSpan(@IntRange(from = 0) int indentWidth,
                    @IntRange(from = 0) int indicatorWidth,
                    @IntRange(from = 0) int indicatorGapWidth,
                    @ColorInt int indicatorColor) {
        this(DEFAULT_LIST_TYPE, DEFAULT_INDICATOR_TEXT, DEFAULT_NESTING_LEVEL, DEFAULT_ORDER_INDEX, indentWidth,
                indicatorWidth, indicatorGapWidth, indicatorColor, true);
    }
    public ListSpan(int listType,
                     String indicatorText,
                     @IntRange(from = 0) int nestingLevel,
                     @IntRange(from = 1) int orderIndex,
                     @IntRange(from = 0) int indentWidth,
                     @IntRange(from = 0) int indicatorWidth,
                     @IntRange(from = 0) int indicatorGapWidth,
                     @ColorInt int indicatorColor,
                     boolean wantColor) {
        mListType = listType;
        mIndicatorText = indicatorText;
        mNestingLevel = nestingLevel;
        mOrderIndex = orderIndex;
        mIndentWidth = indentWidth;
        mIndicatorWidth = indicatorWidth;
        mIndicatorGapWidth = indicatorGapWidth;
        mIndicatorColor = indicatorColor;
        mWantColor = wantColor;
    }

    public int getListType() {
        return mListType;
    }

    public String getIndicatorText() {
        return mIndicatorText;
    }

    public int getNestingLevel() {
        return mNestingLevel;
    }

    public int getOrderIndex() {
        return mOrderIndex;
    }

    public int getIndentWidth() {
        return mIndentWidth;
    }

    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    public int getIndicatorWidth() {
        return mIndicatorWidth;
    }

    public int getIndicatorGapWidth() {
        return mIndicatorGapWidth;
    }

    public boolean ismWantColor() {
        return mWantColor;
    }


    @Override
    public int getLeadingMargin(boolean first) {
        return mNestingLevel * mIndentWidth + mIndicatorWidth + mIndicatorGapWidth;

    }

    @Override
    public void drawLeadingMargin(Canvas canvas, Paint paint, int x, int dir, int top,
                                  int baseline, int bottom, CharSequence text, int start, int end,
                                  boolean first, Layout layout) {
        if (first) {
            Paint.Style style = paint.getStyle();
            int oldColor = 0;

            if (mWantColor) {
                oldColor = paint.getColor();
                paint.setColor(mIndicatorColor);
            }

            paint.setStyle(Paint.Style.FILL);

            final String textToDraw = ListSpanUtil.getIndicatorText(mListType, mOrderIndex);
            final float textStart = x + dir * (mNestingLevel * mIndentWidth);
            canvas.drawText(textToDraw, textStart, baseline, paint);

            if (mWantColor) {
                paint.setColor(oldColor);
            }

            paint.setStyle(style);
        }
    }


    public static final Creator<ListSpan> CREATOR = new Creator<ListSpan>() {
        @Override
        public ListSpan createFromParcel(Parcel in) {
            final int listType = in.readInt();
            final String indicatorText = in.readString();
            final @IntRange(from = 0) int nestingLevel = in.readInt();
            final @IntRange(from = 1) int orderIndex = in.readInt();
            final @IntRange(from = 0) int indentWidth = in.readInt();
            final @IntRange(from = 0) int indicatorWidth = in.readInt();
            final @IntRange(from = 0) int indicatorGapWidth = in.readInt();
            final @ColorInt int indicatorColor = in.readInt();
            final boolean wantColor = in.readInt() == 1;
            return new ListSpan(listType,indicatorText, nestingLevel,orderIndex, indentWidth,
                    indicatorWidth, indicatorGapWidth, indicatorColor, wantColor);
        }

        @Override
        public ListSpan[] newArray(int size) {
            return new ListSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mListType);
        dest.writeString(mIndicatorText);
        dest.writeInt(mNestingLevel);
        dest.writeInt(mOrderIndex);
        dest.writeInt(mIndentWidth);
        dest.writeInt(mIndicatorWidth);
        dest.writeInt(mIndicatorGapWidth);
        dest.writeInt(mIndicatorColor);
        dest.writeInt(mWantColor ? 1 : 0);
    }

}