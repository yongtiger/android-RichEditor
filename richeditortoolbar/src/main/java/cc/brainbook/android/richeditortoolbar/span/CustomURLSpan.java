package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.text.style.URLSpan;

public class CustomURLSpan extends URLSpan {
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
        public CustomURLSpan createFromParcel(Parcel in) {
            final String url = in.readString();
            return new CustomURLSpan(url);
        }

        @Override
        public CustomURLSpan[] newArray(int size) {
            return new CustomURLSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mURL);
    }

}
