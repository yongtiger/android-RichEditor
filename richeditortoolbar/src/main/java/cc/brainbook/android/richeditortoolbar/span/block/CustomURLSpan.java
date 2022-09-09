package cc.brainbook.android.richeditortoolbar.span.block;

import android.os.Parcel;
import android.text.style.URLSpan;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.IBlockCharacterStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IReadableStyle;

public class CustomURLSpan extends URLSpan implements IBlockCharacterStyle, IReadableStyle {
    ///[Gson#Exclude父类成员变量的序列化和反序列化]
    ///Exclude后父类成员变量不被序列化，因此需要重新声明并设置@Expose
    @Expose
    private final String mURL;


    /**
     * Constructs a {@link CustomURLSpan} from a url string.
     *
     * @param url the url string
     */
    public CustomURLSpan(String url) {
        super(url);
        mURL = url;
    }


    public static final Creator<CustomURLSpan> CREATOR = new Creator<CustomURLSpan>() {
        @Override
        @NonNull
        public CustomURLSpan createFromParcel(@NonNull Parcel in) {
            final String url = in.readString();

            return new CustomURLSpan(url);
        }

        @Override
        @NonNull
        public CustomURLSpan[] newArray(int size) {
            return new CustomURLSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mURL);
    }

}
