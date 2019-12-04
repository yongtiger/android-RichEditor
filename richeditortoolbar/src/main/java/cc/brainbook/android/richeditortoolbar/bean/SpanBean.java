package cc.brainbook.android.richeditortoolbar.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class SpanBean<T extends Parcelable> implements Parcelable {
    private int spanStart;
    private int spanEnd;
    private int spanFlags;
    private T span;

    public int getSpanStart() {
        return spanStart;
    }

    public void setSpanStart(int spanStart) {
        this.spanStart = spanStart;
    }

    public int getSpanEnd() {
        return spanEnd;
    }

    public void setSpanEnd(int spanEnd) {
        this.spanEnd = spanEnd;
    }

    public int getSpanFlags() {
        return spanFlags;
    }

    public void setSpanFlags(int spanFlags) {
        this.spanFlags = spanFlags;
    }

    public T getSpan() {
        return span;
    }

    public void setSpan(T span) {
        this.span = span;
    }

    public SpanBean(T span, int spanStart, int spanEnd, int spanFlags) {
        this.spanStart = spanStart;
        this.spanEnd = spanEnd;
        this.spanFlags = spanFlags;
        this.span = span;
    }

    protected SpanBean(Parcel in) {
        spanStart = in.readInt();
        spanEnd = in.readInt();
        spanFlags = in.readInt();
        span = in.readParcelable(getClass().getClassLoader());
    }

    public static final Creator<SpanBean> CREATOR = new Creator<SpanBean>() {
        @Override
        public SpanBean createFromParcel(Parcel in) {
            return new SpanBean(in);
        }

        @Override
        public SpanBean[] newArray(int size) {
            return new SpanBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(spanStart);
        dest.writeInt(spanEnd);
        dest.writeInt(spanFlags);
        dest.writeParcelable(span, flags);
    }
}
