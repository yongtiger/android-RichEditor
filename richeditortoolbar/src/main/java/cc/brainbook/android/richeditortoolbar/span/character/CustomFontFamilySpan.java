package cc.brainbook.android.richeditortoolbar.span.character;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.style.TypefaceSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IReadableStyle;

public class CustomFontFamilySpan extends TypefaceSpan implements ICharacterStyle, IReadableStyle {
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
        @NonNull
        public CustomFontFamilySpan createFromParcel(@NonNull Parcel in) {
            @Nullable String family = in.readString();

            return new CustomFontFamilySpan(family);
        }

        @Override
        @NonNull
        public CustomFontFamilySpan[] newArray(int size) {
            return new CustomFontFamilySpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mFamily);
    }
}
