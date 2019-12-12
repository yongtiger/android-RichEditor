package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.StyleSpan;

public class BoldSpan extends StyleSpan implements Parcelable {
    public BoldSpan() {
        super(Typeface.BOLD);
    }

    public static final Creator<BoldSpan> CREATOR = new Creator<BoldSpan>() {
        @Override
        public BoldSpan createFromParcel(Parcel in) {
            return new BoldSpan();
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
