package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.text.style.SubscriptSpan;

public class CustomSubscriptSpan extends SubscriptSpan {

    public static final Creator<CustomSubscriptSpan> CREATOR = new Creator<CustomSubscriptSpan>() {
        @Override
        public CustomSubscriptSpan createFromParcel(Parcel in) {
            return new CustomSubscriptSpan();
        }

        @Override
        public CustomSubscriptSpan[] newArray(int size) {
            return new CustomSubscriptSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

}
