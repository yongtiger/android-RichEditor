package cc.brainbook.android.richeditortoolbar.span.nest;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;

public class CustomLeadingMarginSpan implements LeadingMarginSpan, INestParagraphStyle {

    ///[Gson#Exclude父类成员变量的序列化和反序列化]
    ///Exclude后父类成员变量不被序列化，因此需要重新声明并设置@Expose
    @Expose
    private final int mFirst, mRest;

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


    public CustomLeadingMarginSpan(int nestingLevel, int first, int rest) {
        mNestingLevel = nestingLevel;
        mFirst = first;
        mRest = rest;
    }

    public CustomLeadingMarginSpan(int nestingLevel, int indent) {
        this(nestingLevel, indent, 0);
    }


    @Override
    public int getLeadingMargin(boolean first) {
        return first ? mFirst : mRest;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p,
                                  int x, int dir,
                                  int top, int baseline, int bottom,
                                  CharSequence text, int start, int end,
                                  boolean first, Layout layout) {}


    public static final Creator<CustomLeadingMarginSpan> CREATOR = new Creator<CustomLeadingMarginSpan>() {
        @Override
        @NonNull
        public CustomLeadingMarginSpan createFromParcel(@NonNull Parcel in) {
            final int nestingLevel = in.readInt();
            final int first = in.readInt();
            final int rest = in.readInt();

            return new CustomLeadingMarginSpan(nestingLevel, first, rest);
        }

        @Override
        @NonNull
        public CustomLeadingMarginSpan[] newArray(int size) {
            return new CustomLeadingMarginSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(getNestingLevel());
        dest.writeInt(mFirst);
        dest.writeInt(mRest);
    }

}
