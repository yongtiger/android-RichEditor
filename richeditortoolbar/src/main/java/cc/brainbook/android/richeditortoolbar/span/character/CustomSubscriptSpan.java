package cc.brainbook.android.richeditortoolbar.span.character;

import android.os.Parcel;
import android.text.style.SubscriptSpan;

import androidx.annotation.NonNull;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IReadableStyle;

public class CustomSubscriptSpan extends SubscriptSpan implements ICharacterStyle, IReadableStyle {

    public static final Creator<CustomSubscriptSpan> CREATOR = new Creator<CustomSubscriptSpan>() {
        @Override
        @NonNull
        public CustomSubscriptSpan createFromParcel(Parcel in) {
            return new CustomSubscriptSpan();
        }

        @Override
        @NonNull
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
