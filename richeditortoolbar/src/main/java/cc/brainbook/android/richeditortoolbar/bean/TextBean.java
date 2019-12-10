package cc.brainbook.android.richeditortoolbar.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TextBean implements Parcelable {
    private String text;
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

    protected TextBean(Parcel in) {
        this();
        text = in.readString();
        in.readTypedList(spans, SpanBean.CREATOR);
//        spans = in.createTypedArrayList(SpanBean.CREATOR);
//        spans = in.readArrayList(SpanBean.class.getClassLoader());
    }

    public static final Creator<TextBean> CREATOR = new Creator<TextBean>() {
        @Override
        public TextBean createFromParcel(Parcel in) {
            return new TextBean(in);
        }

        @Override
        public TextBean[] newArray(int size) {
            return new TextBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeTypedList(spans);
//        dest.writeList(spans);
//        dest.writeParcelableArray(spans.toArray(new SpanBean[0]), flags); ///https://stackoverflow.com/questions/1056683/should-we-use-type-cast-for-the-object-toarray
    }
}
