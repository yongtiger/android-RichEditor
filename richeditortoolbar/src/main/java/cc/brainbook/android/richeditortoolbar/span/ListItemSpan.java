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


    ///[NestingLevel]
    @Expose
    private int mNestingLevel;

    @Expose
    @IntRange(from = 0) private int mIndex;

    @Expose
    private int mListType;

    ///[IndicatorMargin]
    @Expose
    @IntRange(from = 0) private final int mIndicatorMargin;

    ///[Indicator]
    @Expose
    @IntRange(from = 0)  private final int mIndicatorWidth;
    @Expose
    @IntRange(from = 0)  private final int mIndicatorGapWidth;
    @Expose
    @ColorInt private final int mIndicatorColor;

    @Expose
    private final boolean mWantColor;


    public ListItemSpan(int nestingLevel,
                        @IntRange(from = 0) int index,
                        int listType,
                        @IntRange(from = 0) int indicatorMargin,
                        @IntRange(from = 0) int indicatorWidth,
                        @IntRange(from = 0) int indicatorGapWidth,
                        @ColorInt int indicatorColor,
                        boolean wantColor) {
        mNestingLevel = nestingLevel;
        mIndex = index;
        mListType = listType;
        mIndicatorMargin = indicatorMargin;
        mIndicatorWidth = indicatorWidth;
        mIndicatorGapWidth = indicatorGapWidth;
        mIndicatorColor = indicatorColor;
        mWantColor = wantColor;
    }


    public int getNestingLevel() {
        return mNestingLevel;
    }

    public void setNestingLevel(int nestingLevel) {
        mNestingLevel = nestingLevel;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getListType() {
        return mListType;
    }

    public void setListType(int listType) {
        mListType = listType;
    }

    public int getIndicatorMargin() {
        return mIndicatorMargin;
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

        final String textToDraw = getIndicatorText(mListType, mIndex);
        final float textWidth = isListTypeOrdered(mListType) ? paint.measureText(textToDraw) : mIndicatorWidth;
        ///由于无法确定ListSpan是否先执行，所以无法保证返回正确的x，所以不用x！
//            final float textStart = x + dir * (0 - mIndicatorGapWidth - textWidth);
        final float textStart = mIndicatorMargin * getNestingLevel() - dir * (mIndicatorGapWidth + textWidth);

        if (isListTypeOrdered(mListType)) {
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
            final int nestingLevel = in.readInt();
            final @IntRange(from = 0) int index = in.readInt();
            final int listType = in.readInt();
            final @IntRange(from = 0) int indicatorMargin = in.readInt();
            final @IntRange(from = 0) int indicatorWidth = in.readInt();
            final @IntRange(from = 0) int indicatorGapWidth = in.readInt();
            final @ColorInt int indicatorColor = in.readInt();
            final boolean wantColor = in.readInt() == 1;

            return new ListItemSpan(nestingLevel, index, listType, indicatorMargin,
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
        dest.writeInt(getNestingLevel());
        dest.writeInt(mIndex);
        dest.writeInt(mListType);
        dest.writeInt(mIndicatorMargin);
        dest.writeInt(mIndicatorWidth);
        dest.writeInt(mIndicatorGapWidth);
        dest.writeInt(mIndicatorColor);
        dest.writeInt(mWantColor ? 1 : 0);
    }


//    private int getIndexOfProcessedLine(CharSequence text, int end) {///////////////////////////
//        final int spanStart = ((Spanned) text).getSpanStart(this);
//        final int spanEnd = ((Spanned) text).getSpanEnd(this);
//
//        final Spanned listText = (Spanned) text.subSequence(spanStart, spanEnd);
//
//        if (end - spanStart - 1 >= 0 && end - spanStart <= listText.length()) {
//            boolean hasSublist = false;
//            final ListSpan[] listSpans = listText.getSpans(end - spanStart - 1, end - spanStart, ListSpan.class);
//            for (ListSpan listSpan : listSpans) {
//                if (listSpan.getNestingLevel() > getNestingLevel()) {
//                    hasSublist = true;
//                    break;
//                }
//            }
//            if (hasSublist) {
//                return -1;
//            }
//        }
//
//        // only display a line indicator when it's the first line of a list item
//        final String textBeforeBeforeEnd = listText.subSequence(0, end - spanStart).toString();
//        final int startOfLine = textBeforeBeforeEnd.lastIndexOf('\n') + 1;
//        boolean isValidListItem = false;
//        final ListItemSpan[] listItemSpans = listText.getSpans(0, listText.length(), ListItemSpan.class);
//        for (ListItemSpan listItemSpan : listItemSpans) {
//            if (listItemSpan.getNestingLevel() == getNestingLevel() + 1 && listText.getSpanStart(listItemSpan) == startOfLine) {
//                isValidListItem = true;
//                break;
//            }
//        }
//
//        if (!isValidListItem) {
//            return -1;
//        }
//
//        // count the list item spans up to the current line with the expected nesting level => item number
//        int result = 0;
//        final int checkEnd = Math.min(textBeforeBeforeEnd.length() + 1, listText.length());
//        final ListItemSpan[] listItemSpans2 = listText.getSpans(0, checkEnd, ListItemSpan.class);
//        for (ListItemSpan listItemSpan : listItemSpans2) {
//            if (listItemSpan.getNestingLevel() == getNestingLevel() + 1) {
//                result++;
//            }
//        }
//        return result;
//    }
//
//    private int getNumberOfItemsInProcessedLine(CharSequence text) {
//        final int spanStart = ((Spanned) text).getSpanStart(this);
//        final int spanEnd = ((Spanned) text).getSpanEnd(this);
//
//        final Spanned listText = (Spanned) text.subSequence(spanStart, spanEnd);
//
//        int result = 0;
//        final ListItemSpan[] listItemSpans = listText.getSpans(0, listText.length(), ListItemSpan.class);
//        for (ListItemSpan listItemSpan : listItemSpans) {
//            if (listItemSpan.getNestingLevel() == getNestingLevel() + 1) {
//                result++;
//            }
//        }
//        return result;
//    }

}
