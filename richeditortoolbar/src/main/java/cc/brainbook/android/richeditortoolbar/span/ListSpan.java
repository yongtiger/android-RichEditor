package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;

public class ListSpan extends NestSpan implements LeadingMarginSpan, Parcelable, INestParagraphStyle {
    @IntRange(from = 0) public static final int DEFAULT_INDICATOR_MARGIN = 160;


    @Expose
    @IntRange(from = 0) private int mStart;
    @Expose
    private boolean isReversed;

    ///[ListType]
    @Expose
    private int mListType;

    ///[IndicatorMargin]
    @Expose
    @IntRange(from = 0) private final int mIndicatorMargin;


    public ListSpan(int nestingLevel,
                    @IntRange(from = 0)int start,
                    boolean isReversed,
                    int listType,
                    @IntRange(from = 0) int indicatorMargin) {
        super(nestingLevel);
        mStart = start;
        this.isReversed = isReversed;
        mListType = listType;
        mIndicatorMargin = indicatorMargin;
    }


    public int getStart() {
        return mStart;
    }

    public void setStart(int start) {
        mStart = start;
    }

    public boolean isReversed() {
        return isReversed;
    }

    public void isReversed(boolean isReversed) {
        this.isReversed = isReversed;
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


    @Override
    public int getLeadingMargin(boolean first) {
        return mIndicatorMargin;
    }

    @Override
    public void drawLeadingMargin(Canvas canvas, Paint paint, int x, int dir, int top,
                                  int baseline, int bottom, CharSequence text, int start, int end,
                                  boolean first, Layout layout) {}


    public static final Creator<ListSpan> CREATOR = new Creator<ListSpan>() {
        @Override
        public ListSpan createFromParcel(Parcel in) {
            final int nestingLevel = in.readInt();
            final @IntRange(from = 0) int start = in.readInt();
            final boolean isReversed = in.readInt() == 1;
            final int listType = in.readInt();
            final @IntRange(from = 0) int indicatorMargin = in.readInt();
            return new ListSpan(nestingLevel, start, isReversed, listType, indicatorMargin);
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
        dest.writeInt(getNestingLevel());
        dest.writeInt(mStart);
        dest.writeInt(isReversed ? 1 : 0);
        dest.writeInt(mListType);
        dest.writeInt(mIndicatorMargin);
    }

}