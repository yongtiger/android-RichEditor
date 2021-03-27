package cc.brainbook.android.richeditortoolbar.span.character;

import android.graphics.Typeface;
import android.os.Parcel;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;

public class BoldSpan extends StyleSpan implements ICharacterStyle {
    public BoldSpan() {
        super(Typeface.BOLD);
    }


    public static final Creator<BoldSpan> CREATOR = new Creator<BoldSpan>() {
        @Override
        @NonNull
        public BoldSpan createFromParcel(Parcel in) {
            return new BoldSpan();
        }

        @Override
        @NonNull
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
