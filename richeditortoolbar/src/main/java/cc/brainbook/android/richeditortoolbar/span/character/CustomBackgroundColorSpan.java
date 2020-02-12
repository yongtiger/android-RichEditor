package cc.brainbook.android.richeditortoolbar.span.character;

import android.os.Parcel;
import android.support.annotation.ColorInt;
import android.text.style.BackgroundColorSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;

public class CustomBackgroundColorSpan extends BackgroundColorSpan implements ICharacterStyle {
    ///[Gson#Exclude父类成员变量的序列化和反序列化]
    ///Exclude后父类成员变量不被序列化，因此需要重新声明并设置@Expose
    @Expose
    @ColorInt
    private final int mColor;


    public CustomBackgroundColorSpan(int color) {
        super(color);
        mColor = color;
    }


    public static final Creator<CustomBackgroundColorSpan> CREATOR = new Creator<CustomBackgroundColorSpan>() {
        @Override
        public CustomBackgroundColorSpan createFromParcel(Parcel in) {
            @ColorInt final int color = in.readInt();
            return new CustomBackgroundColorSpan(color);
        }

        @Override
        public CustomBackgroundColorSpan[] newArray(int size) {
            return new CustomBackgroundColorSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mColor);
    }
}
