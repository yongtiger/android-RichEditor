package cc.brainbook.android.richeditortoolbar.span.nest;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;

public class ListSpan implements LeadingMarginSpan, Parcelable, INestParagraphStyle {
    @IntRange(from = 0) public static final int DEFAULT_INDENT = 160;


    ///[ListType]
    @Expose
    private int mListType;

    @Expose
    @IntRange(from = 0) private int mStart;
    @Expose
    private boolean isReversed;

    ///[Intent]
    @Expose
    @IntRange(from = 0) private final int mIntent;


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


    public ListSpan(int nestingLevel,
                    int listType) {
        this(nestingLevel, listType, 0, false, DEFAULT_INDENT);
    }
    public ListSpan(int nestingLevel,
                    int listType,
                    @IntRange(from = 0)int start,
                    boolean isReversed) {
        this(nestingLevel, listType, start, isReversed, DEFAULT_INDENT);
    }
    public ListSpan(int nestingLevel,
                    int listType,
                    @IntRange(from = 0)int start,
                    boolean isReversed,
                    @IntRange(from = 0) int intent) {
        mNestingLevel = nestingLevel;
        mListType = listType;
        mStart = start;
        this.isReversed = isReversed;
        mIntent = intent;
    }


    public int getListType() {
        return mListType;
    }

    public void setListType(int listType) {
        mListType = listType;
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

    public int getIntent() {
        return mIntent;
    }


    @Override
    public int getLeadingMargin(boolean first) {
        return mIntent;
    }

    @Override
    public void drawLeadingMargin(Canvas canvas, Paint paint, int x, int dir, int top,
                                  int baseline, int bottom, CharSequence text, int start, int end,
                                  boolean first, Layout layout) {}


    public static final Creator<ListSpan> CREATOR = new Creator<ListSpan>() {
        @Override
        public ListSpan createFromParcel(Parcel in) {
            final int nestingLevel = in.readInt();
            final int listType = in.readInt();
            final @IntRange(from = 0) int start = in.readInt();
            final boolean isReversed = in.readInt() == 1;
            final @IntRange(from = 0) int intent = in.readInt();

            return new ListSpan(nestingLevel, listType, start, isReversed, intent);
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
        dest.writeInt(mListType);
        dest.writeInt(mStart);
        dest.writeInt(isReversed ? 1 : 0);
        dest.writeInt(mIntent);
    }

}