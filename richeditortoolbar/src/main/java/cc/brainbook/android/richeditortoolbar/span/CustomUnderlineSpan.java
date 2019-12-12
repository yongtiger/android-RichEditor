package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.text.style.UnderlineSpan;

///消除EditText输入时自动产生UnderlineSpan
///https://stackoverflow.com/questions/35323111/android-edittext-is-underlined-when-typing
///https://stackoverflow.com/questions/46822580/edittext-remove-black-underline-while-typing/47704299#47704299
public class CustomUnderlineSpan extends UnderlineSpan {

    public static final Creator<CustomUnderlineSpan> CREATOR = new Creator<CustomUnderlineSpan>() {
        @Override
        public CustomUnderlineSpan createFromParcel(Parcel in) {
            return new CustomUnderlineSpan();
        }

        @Override
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
