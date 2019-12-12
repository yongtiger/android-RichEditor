package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.style.TypefaceSpan;

public class CustomFontFamilySpan extends TypefaceSpan implements Parcelable {
    @Nullable
    private final String mFamily;

    public CustomFontFamilySpan(@Nullable String family) {
        super(family);
        mFamily = family;
    }

    @Nullable
    public String getFamily() {
        return mFamily;
    }


    public static final Creator<CustomFontFamilySpan> CREATOR = new Creator<CustomFontFamilySpan>() {
        @Override
        public CustomFontFamilySpan createFromParcel(Parcel in) {
            ///注意：必须按照成员变量声明的顺序！
            @Nullable String family = in.readString();
            return new CustomFontFamilySpan(family);
        }

        @Override
        public CustomFontFamilySpan[] newArray(int size) {
            return new CustomFontFamilySpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFamily);
    }
}
