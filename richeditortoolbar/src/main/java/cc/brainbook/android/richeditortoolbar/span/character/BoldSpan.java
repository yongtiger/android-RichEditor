package cc.brainbook.android.richeditortoolbar.span.character;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.StyleSpan;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;

public class BoldSpan extends StyleSpan implements Parcelable, ICharacterStyle {
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
