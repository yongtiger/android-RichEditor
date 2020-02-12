package cc.brainbook.android.richeditortoolbar.span.character;

import android.os.Parcel;
import android.text.style.SuperscriptSpan;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;

public class CustomSuperscriptSpan extends SuperscriptSpan implements ICharacterStyle {

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
