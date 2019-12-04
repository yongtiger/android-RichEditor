package cc.brainbook.android.richeditortoolbar.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TextBean implements Parcelable {
    private String text;
    private List<SpanBean> spans;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<SpanBean> getSpans() {
        return spans;
    }

    public void setSpans(List<SpanBean> spans) {
        this.spans = spans;
    }

    public TextBean() {
        // initialization
        spans = new ArrayList<SpanBean>();
    }

    protected TextBean(Parcel in) {
        this();
        text = in.readString();
        in.readTypedList(spans, SpanBean.CREATOR);
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
    }
}
