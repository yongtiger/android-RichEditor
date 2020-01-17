package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.IParagraphStyle;

import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.getIndicatorText;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.isListTypeOrdered;

public class ListItemSpan implements LeadingMarginSpan, Parcelable, IParagraphStyle {
    @IntRange(from = 0) public static final int DEFAULT_INDICATOR_WIDTH = 20;
    @IntRange(from = 0) public static final int DEFAULT_INDICATOR_GAP_WIDTH = 40;
    @ColorInt public static final int DEFAULT_INDICATOR_COLOR = Color.parseColor("#DDDDDD");


    @Expose
    private ListSpan mListSpan;

    @Expose
    @IntRange(from = 0) private int mIndex;

    ///[Indicator]
    @Expose
    @IntRange(from = 0)  private final int mIndicatorWidth;
    @Expose
    @IntRange(from = 0)  private final int mIndicatorGapWidth;
    @Expose
    @ColorInt private final int mIndicatorColor;

    @Expose
    private final boolean mWantColor;


    public ListItemSpan(ListSpan listSpan,
                        @IntRange(from = 0) int index,
                        @IntRange(from = 0) int indicatorWidth,
                        @IntRange(from = 0) int indicatorGapWidth,
                        @ColorInt int indicatorColor,
                        boolean wantColor) {
        mListSpan = listSpan;
        mIndex = index;
        mIndicatorWidth = indicatorWidth;
        mIndicatorGapWidth = indicatorGapWidth;
        mIndicatorColor = indicatorColor;
        mWantColor = wantColor;
    }

    public ListSpan getListSpan() {
        return mListSpan;
    }

    public void setListSpan(ListSpan listSpan) {
        this.mListSpan = listSpan;
    }

    public int getNestingLevel() {
        return mListSpan.getNestingLevel();
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getListType() {
        return mListSpan.getListType();
    }

    public int getIndicatorMargin() {
        return mListSpan.getIndicatorMargin();
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

    public boolean isWantColor() {
        return mWantColor;
    }


    @Override
    public int getLeadingMargin(boolean first) {
        return 0;
    }

    @Override
    public void drawLeadingMargin(Canvas canvas, Paint paint, int x, int dir, int top,
                                  int baseline, int bottom, CharSequence text, int start, int end,
                                  boolean first, Layout layout) {
        if (!first) return;

        ///如果ListItemSpan包含多行，则只绘制行首等于spanStart的
        final int spanStart = ((Spanned) text).getSpanStart(this);
//        final int spanEnd = ((Spanned) text).getSpanEnd(this);
        if (start != spanStart) return;

        Paint.Style style = paint.getStyle();
        int oldColor = 0;

        if (mWantColor) {
            oldColor = paint.getColor();
            paint.setColor(mIndicatorColor);
        }

        paint.setStyle(Paint.Style.FILL);

        final String textToDraw = getIndicatorText(getListType(), mIndex);
        final float textWidth = isListTypeOrdered(getListType()) ? paint.measureText(textToDraw) : mIndicatorWidth;
        ///由于无法确定ListSpan是否先执行，所以无法保证返回正确的x，所以不用x！
//            final float textStart = x + dir * (0 - mIndicatorGapWidth - textWidth);
        final float textStart = getIndicatorMargin() * getNestingLevel() - dir * (mIndicatorGapWidth + textWidth);

        if (isListTypeOrdered(getListType())) {
            canvas.drawText(textToDraw, textStart, baseline, paint);
        } else {
            ///垂直居中
            final float centerY = (top + bottom) / 2;
            final float transY = centerY + mIndicatorWidth / 2;

            ///保存paint的TextSize
            final float oldTextSize = paint.getTextSize();
            paint.setTextSize(mIndicatorWidth); ///设置字体大小
            canvas.drawText(textToDraw, textStart, transY, paint);
            ///恢复paint的TextSize
            paint.setTextSize(oldTextSize);
        }

        if (mWantColor) {
            paint.setColor(oldColor);
        }

        paint.setStyle(style);
    }


    public static final Creator<ListItemSpan> CREATOR = new Creator<ListItemSpan>() {
        @Override
        public ListItemSpan createFromParcel(Parcel in) {
            final ListSpan listSpan = in.readParcelable(ListSpan.class.getClassLoader());
            final @IntRange(from = 0) int index = in.readInt();
            final @IntRange(from = 0) int indicatorWidth = in.readInt();
            final @IntRange(from = 0) int indicatorGapWidth = in.readInt();
            final @ColorInt int indicatorColor = in.readInt();
            final boolean wantColor = in.readInt() == 1;

            return new ListItemSpan(listSpan, index,
                    indicatorWidth, indicatorGapWidth, indicatorColor, wantColor);
        }

        @Override
        public ListItemSpan[] newArray(int size) {
            return new ListItemSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mListSpan, flags);
        dest.writeInt(mIndex);
        dest.writeInt(mIndicatorWidth);
        dest.writeInt(mIndicatorGapWidth);
        dest.writeInt(mIndicatorColor);
        dest.writeInt(mWantColor ? 1 : 0);
    }

}
