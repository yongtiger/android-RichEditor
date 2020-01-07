package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.LeadingMarginSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.IBlockParagraphStyle;

public class CustomLeadingMarginSpan extends LeadingMarginSpan.Standard implements Parcelable, IBlockParagraphStyle {
    ///[Gson#Exclude父类成员变量的序列化和反序列化]
    ///Exclude后父类成员变量不被序列化，因此需要重新声明并设置@Expose
    @Expose
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
