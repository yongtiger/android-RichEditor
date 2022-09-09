package cc.brainbook.android.richeditortoolbar.span.character;

import android.os.Parcel;
import android.text.style.UnderlineSpan;

import androidx.annotation.NonNull;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IReadableStyle;

///消除EditText输入时自动产生UnderlineSpan
///https://stackoverflow.com/questions/35323111/android-edittext-is-underlined-when-typing
///https://stackoverflow.com/questions/46822580/edittext-remove-black-underline-while-typing/47704299#47704299
public class CustomUnderlineSpan extends UnderlineSpan implements ICharacterStyle, IReadableStyle {

    public static final Creator<CustomUnderlineSpan> CREATOR = new Creator<CustomUnderlineSpan>() {
        @Override
        @NonNull
        public CustomUnderlineSpan createFromParcel(Parcel in) {
            return new CustomUnderlineSpan();
        }

        @Override
        @NonNull
        public CustomUnderlineSpan[] newArray(int size) {
            return new CustomUnderlineSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

}
