package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;

public class CustomAbsoluteSizeSpan extends AbsoluteSizeSpan {

    private final int mSize;
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

    /**
     * Get the text size. This is in physical pixels if {@link #getDip()} returns false or in
     * device-independent pixels if {@link #getDip()} returns true.
     *
     * @return the text size, either in physical pixels or device-independent pixels.
     * @see AbsoluteSizeSpan#AbsoluteSizeSpan(int, boolean)
     */
    public int getSize() {
        return mSize;
    }

    /**
     * Returns whether the size is in device-independent pixels or not, depending on the
     * <code>dip</code> flag passed in {@link #CustomAbsoluteSizeSpan(int, boolean)}
     *
     * @return <code>true</code> if the size is in device-independent pixels, <code>false</code>
     * otherwise
     *
     * @see #CustomAbsoluteSizeSpan(int, boolean)
     */
    public boolean getDip() {
        return mDip;
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
