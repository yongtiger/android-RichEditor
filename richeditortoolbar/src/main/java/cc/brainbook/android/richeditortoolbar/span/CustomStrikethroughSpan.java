package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.text.style.StrikethroughSpan;

public class CustomStrikethroughSpan extends StrikethroughSpan {

    public static final Creator<CustomStrikethroughSpan> CREATOR = new Creator<CustomStrikethroughSpan>() {
        @Override
        public CustomStrikethroughSpan createFromParcel(Parcel in) {
            return new CustomStrikethroughSpan();
        }

        @Override
        public CustomStrikethroughSpan[] newArray(int size) {
            return new CustomStrikethroughSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

}
