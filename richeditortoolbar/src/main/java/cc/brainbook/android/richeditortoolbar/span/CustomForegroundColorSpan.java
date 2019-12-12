package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.support.annotation.ColorInt;
import android.text.style.ForegroundColorSpan;

public class CustomForegroundColorSpan extends ForegroundColorSpan {
    @ColorInt
    private int mColor;

    public CustomForegroundColorSpan(int color) {
        super(color);
        mColor = color;
    }


    public static final Creator<CustomForegroundColorSpan> CREATOR = new Creator<CustomForegroundColorSpan>() {
        @Override
        public CustomForegroundColorSpan createFromParcel(Parcel in) {
            ///注意：必须按照成员变量声明的顺序！
            @ColorInt final int color = in.readInt();
            return new CustomForegroundColorSpan(color);
        }

        @Override
        public CustomForegroundColorSpan[] newArray(int size) {
            return new CustomForegroundColorSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mColor);
    }
}
