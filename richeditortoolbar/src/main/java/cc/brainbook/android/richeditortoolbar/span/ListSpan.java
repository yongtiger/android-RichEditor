package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper;
import cc.brainbook.android.richeditortoolbar.interfaces.IParagraphStyle;

public class ListSpan implements LeadingMarginSpan, Parcelable, IParagraphStyle {
    @IntRange(from = 0) public static final int DEFAULT_NESTING_LEVEL = 0;
    public static final int DEFAULT_LIST_TYPE = ListSpanHelper.LIST_TYPE_UNORDERED_CIRCLE;
    @IntRange(from = 0) public static final int DEFAULT_INDICATOR_MARGIN = 160;


    @Expose
    @IntRange(from = 0) private final int mStart;
    @Expose
    private final boolean isReversed;

    ///[NestingLevel]
    @Expose
    @IntRange(from = 0) private final int mNestingLevel;

    ///[ListType]
    @Expose
    private final int mListType;

    ///[IndicatorMargin]
    @Expose
    @IntRange(from = 0) private final int mIndicatorMargin;


    public ListSpan() {
        this(1, false, DEFAULT_NESTING_LEVEL, DEFAULT_LIST_TYPE, DEFAULT_INDICATOR_MARGIN);
    }

    public ListSpan(@IntRange(from = 0) int nestingLevel, int listType) {
        this(1, false, nestingLevel, listType, DEFAULT_INDICATOR_MARGIN);
    }

    public ListSpan(int start, boolean isReversed,
                     @IntRange(from = 0) int nestingLevel,
                     int listType,
                     @IntRange(from = 0) int indicatorMargin) {
        mStart = start;
        this.isReversed = isReversed;
        mNestingLevel = nestingLevel;
        mListType = listType;
        mIndicatorMargin = indicatorMargin;
    }


    public int getStart() {
        return mStart;
    }

    public boolean isReversed() {
        return isReversed;
    }

    public int getNestingLevel() {
        return mNestingLevel;
    }

    public int getListType() {
        return mListType;
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
            final @IntRange(from = 0) int start = in.readInt();
            final boolean isReversed = in.readInt() == 1;
            final int listType = in.readInt();
            final @IntRange(from = 0) int nestingLevel = in.readInt();
            final @IntRange(from = 0) int indicatorMargin = in.readInt();

            return new ListSpan(start, isReversed, listType, nestingLevel, indicatorMargin);
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
        dest.writeInt(mStart);
        dest.writeInt(isReversed ? 1 : 0);
        dest.writeInt(mNestingLevel);
        dest.writeInt(mListType);
        dest.writeInt(mIndicatorMargin);
    }

}