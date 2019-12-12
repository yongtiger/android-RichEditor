package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.LeadingMarginSpan;

public class CustomLeadingMarginSpan extends LeadingMarginSpan.Standard implements Parcelable {
    private final int mFirst, mRest;

    public CustomLeadingMarginSpan(int first, int rest) {
        super(first, rest);
        mFirst = first;
        mRest = rest;
    }

    public CustomLeadingMarginSpan(int every) {
        this(every, every);
    }


    public static final Creator<CustomLeadingMarginSpan> CREATOR = new Creator<CustomLeadingMarginSpan>() {
        @Override
        public CustomLeadingMarginSpan createFromParcel(Parcel in) {
            ///注意：必须按照成员变量声明的顺序！
            final int first = in.readInt();
            final int rest = in.readInt();
            return new CustomLeadingMarginSpan(first, rest);
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
        dest.writeInt(mFirst);
        dest.writeInt(mRest);
    }

}
