package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;

public class CustomLeadingMarginSpan extends NestSpan implements LeadingMarginSpan, Parcelable, INestParagraphStyle {
    public static final int DEFAULT_INDENT = 40;


    ///[Gson#Exclude父类成员变量的序列化和反序列化]
    ///Exclude后父类成员变量不被序列化，因此需要重新声明并设置@Expose
    @Expose
    private final int mFirst, mRest;


    public CustomLeadingMarginSpan(int nestingLevel, int first, int rest) {
        super(nestingLevel);
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
        public CustomLeadingMarginSpan createFromParcel(Parcel in) {
            final int nestingLevel = in.readInt();
            final int first = in.readInt();
            final int rest = in.readInt();

            return new CustomLeadingMarginSpan(nestingLevel, first, rest);
        }

        @Override
        public CustomLeadingMarginSpan[] newArray(int size) {
            return new CustomLeadingMarginSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getNestingLevel());
        dest.writeInt(mFirst);
        dest.writeInt(mRest);
    }

}
