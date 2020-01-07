package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;

public class CustomAbsoluteSizeSpan extends AbsoluteSizeSpan implements ICharacterStyle {
    ///[Gson#Exclude父类成员变量的序列化和反序列化]
    ///Exclude后父类成员变量不被序列化，因此需要重新声明并设置@Expose
    @Expose
    private final int mSize;
    @Expose
    private final boolean mDip;

    /**
     * Set the text size to <code>size</code> physical pixels.
     */
    public CustomAbsoluteSizeSpan(int size) {
        this(size, false);
    }

    /**
     * Set the text size to <code>size</code> physical pixels, or to <code>size</code>
     * device-independent pixels if <code>dip</code> is true.
     */
    public CustomAbsoluteSizeSpan(int size, boolean dip) {
        super(size, dip);
        mSize = size;
        mDip = dip;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        if (mDip) {
            ds.setTextSize(mSize * ds.density);
        } else {
            ds.setTextSize(mSize);
        }
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint ds) {
        if (mDip) {
            ds.setTextSize(mSize * ds.density);
        } else {
            ds.setTextSize(mSize);
        }
    }


    public static final Creator<CustomAbsoluteSizeSpan> CREATOR = new Creator<CustomAbsoluteSizeSpan>() {
        @Override
        public CustomAbsoluteSizeSpan createFromParcel(Parcel in) {
            final int size = in.readInt();
            final boolean dip = in.readInt() == 1;

            return new CustomAbsoluteSizeSpan(size, dip);
        }

        @Override
        public CustomAbsoluteSizeSpan[] newArray(int size) {
            return new CustomAbsoluteSizeSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mSize);
        dest.writeInt(mDip ? 1 : 0);
    }

}
