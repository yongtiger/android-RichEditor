package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Typeface;
import android.os.Parcel;
import android.text.style.StyleSpan;

public class BoldSpan extends StyleSpan {
    public BoldSpan() {
        super(Typeface.BOLD);
    }

    public BoldSpan(Parcel src) {
        super(src);
    }

    public static final Creator<BoldSpan> CREATOR = new Creator<BoldSpan>() {
        @Override
        public BoldSpan createFromParcel(Parcel source) {
            return new BoldSpan(source);
        }

        @Override
        public BoldSpan[] newArray(int size) {
            return new BoldSpan[size];
        }
    };
}
