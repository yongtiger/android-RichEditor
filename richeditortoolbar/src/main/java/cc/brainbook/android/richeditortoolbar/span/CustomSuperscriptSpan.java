package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.text.style.SuperscriptSpan;

public class CustomSuperscriptSpan extends SuperscriptSpan {

    public static final Creator<CustomSuperscriptSpan> CREATOR = new Creator<CustomSuperscriptSpan>() {
        @Override
        public CustomSuperscriptSpan createFromParcel(Parcel in) {
            return new CustomSuperscriptSpan();
        }

        @Override
        public CustomSuperscriptSpan[] newArray(int size) {
            return new CustomSuperscriptSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

}
