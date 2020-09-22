package cc.brainbook.android.richeditortoolbar.span.character;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;

public class ItalicSpan extends StyleSpan implements Parcelable, ICharacterStyle {

    public ItalicSpan() {
        super(Typeface.ITALIC);
    }

    public static final Creator<ItalicSpan> CREATOR = new Creator<ItalicSpan>() {
        @Override
        @NonNull
        public ItalicSpan createFromParcel(Parcel in) {
            return new ItalicSpan();
        }

        @Override
        @NonNull
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
