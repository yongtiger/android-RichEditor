package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Typeface;
import android.os.Parcel;
import android.text.style.StyleSpan;

public class ItalicSpan extends StyleSpan {
    public ItalicSpan() {
        super(Typeface.ITALIC);
    }

//    private ItalicSpan(Parcel in) {
//        super(in);
//    }
//
//    public static final Creator<ItalicSpan> CREATOR = new Creator<ItalicSpan>() {
//        @Override
//        public ItalicSpan createFromParcel(Parcel source) {
//            return new ItalicSpan(source);
//        }
//
//        @Override
//        public ItalicSpan[] newArray(int size) {
//            return new ItalicSpan[size];
//        }
//    };
}
