package cc.brainbook.android.richeditortoolbar.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class TextBean implements Parcelable {
    @Expose
    private String text;
    @Expose
    private ArrayList<SpanBean> spans;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<SpanBean> getSpans() {
        return spans;
    }

    public void setSpans(ArrayList<SpanBean> spans) {
        this.spans = spans;
    }

    public TextBean() {
        // initialization
        spans = new ArrayList<>();
    }

    protected TextBean(@NonNull Parcel in) {
        this();
        text = in.readString();
        ///[FIX#API24/25/26/27#readParcelable()#android.os.BadParcelableException: ClassNotFoundException when unmarshalling]
//        in.readTypedList(spans, SpanBean.CREATOR);
        in.readList(spans, getClass().getClassLoader());
    }

    public static final Creator<TextBean> CREATOR = new Creator<TextBean>() {
        @Override
        @NonNull
        public TextBean createFromParcel(Parcel in) {
            return new TextBean(in);
        }

        @Override
        @NonNull
        public TextBean[] newArray(int size) {
            return new TextBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(text);
        ///[FIX#API24/25/26/27#readParcelable()#android.os.BadParcelableException: ClassNotFoundException when unmarshalling]
//        dest.writeTypedList(spans);
        dest.writeList(spans);
    }
}
