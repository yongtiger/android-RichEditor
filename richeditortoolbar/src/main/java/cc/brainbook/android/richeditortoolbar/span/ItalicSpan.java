package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.StyleSpan;

public class ItalicSpan extends StyleSpan implements Parcelable {

    public ItalicSpan() {
        super(Typeface.ITALIC);
    }

    public static final Creator<ItalicSpan> CREATOR = new Creator<ItalicSpan>() {
        @Override
        public ItalicSpan createFromParcel(Parcel in) {
            return new ItalicSpan();
        }

        @Override
        public ItalicSpan[] newArray(int size) {
            return new ItalicSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

}
