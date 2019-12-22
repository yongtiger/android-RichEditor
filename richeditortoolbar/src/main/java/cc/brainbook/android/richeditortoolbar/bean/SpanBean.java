package cc.brainbook.android.richeditortoolbar.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class SpanBean<T extends Parcelable> implements Parcelable {
    @Expose
    private int spanStart;
    @Expose
    private int spanEnd;
    @Expose
    private int spanFlags;
    @Expose
    private T span;
    @Expose
    private String spanClassName;

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

    public String getSpanClassName() {
        return spanClassName;
    }

    public void setSpanClassName(String spanClassName) {
        this.spanClassName = spanClassName;
    }

    public SpanBean(T span, String spanClassName, int spanStart, int spanEnd, int spanFlags) {
        this.spanStart = spanStart;
        this.spanEnd = spanEnd;
        this.spanFlags = spanFlags;
        this.span = span;
        this.spanClassName = spanClassName;
    }

    protected SpanBean(Parcel in) {
        spanStart = in.readInt();
        spanEnd = in.readInt();
        spanFlags = in.readInt();
//        span = in.readParcelable(null);   ///IDE提示使用getClass().getClassLoader()
        //////??????这里ClassLoader应是span的类型，由于span是泛型类，所以问题！
        ///参考：Java中泛型得到T.class
        ///https://www.cnblogs.com/EasonJim/p/8289778.html
        ///http://www.blogjava.net/calvin/archive/2009/12/10/43830.html
        span = in.readParcelable(getClass().getClassLoader());
//        span = in.readParcelable(Thread.currentThread().getContextClassLoader()); ///不使用span的类型
        spanClassName = in.readString();
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
        dest.writeString(spanClassName);
    }
}
