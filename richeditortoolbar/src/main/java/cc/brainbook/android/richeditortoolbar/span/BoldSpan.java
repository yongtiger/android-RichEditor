package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.StyleSpan;

public class BoldSpan extends StyleSpan implements Parcelable {
    public BoldSpan() {
        super(Typeface.BOLD);
    }

    ///注意：必须按照成员变量声明的顺序！
    public BoldSpan(Parcel in) {
//        ///直接调用父类的构造StyleSpan(@NonNull Parcel src)
//        super(in);
        ///直接调用构造（因为无需使用in来初始化BoldSpan的成员变量）
        this();
    }

    public static final Creator<BoldSpan> CREATOR = new Creator<BoldSpan>() {
        @Override
        public BoldSpan createFromParcel(Parcel in) {
            return new BoldSpan(in);
        }

        @Override
        public BoldSpan[] newArray(int size) {
            return new BoldSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}
}
