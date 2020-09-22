package cc.brainbook.android.richeditortoolbar.span.character;

import android.os.Parcel;
import android.text.style.StrikethroughSpan;

import androidx.annotation.NonNull;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;

public class CustomStrikethroughSpan extends StrikethroughSpan implements ICharacterStyle {

    public static final Creator<CustomStrikethroughSpan> CREATOR = new Creator<CustomStrikethroughSpan>() {
        @Override
        @NonNull
        public CustomStrikethroughSpan createFromParcel(Parcel in) {
            return new CustomStrikethroughSpan();
        }

        @Override
        @NonNull
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
