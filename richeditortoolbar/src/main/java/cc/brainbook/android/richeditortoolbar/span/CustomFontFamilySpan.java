package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.style.TypefaceSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;

public class CustomFontFamilySpan extends TypefaceSpan implements Parcelable, ICharacterStyle {
    ///[Gson#Exclude父类成员变量的序列化和反序列化]
    ///Exclude后父类成员变量不被序列化，因此需要重新声明并设置@Expose
    @Expose
    @Nullable
    private final String mFamily;


    public CustomFontFamilySpan(@Nullable String family) {
        super(family);
        mFamily = family;
    }


    public static final Creator<CustomFontFamilySpan> CREATOR = new Creator<CustomFontFamilySpan>() {
        @Override
        public CustomFontFamilySpan createFromParcel(Parcel in) {
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
