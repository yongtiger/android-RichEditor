package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.support.annotation.FloatRange;
import android.text.TextPaint;
import android.text.style.ScaleXSpan;

public class CustomScaleXSpan extends ScaleXSpan {
    private final float mProportion;

    public CustomScaleXSpan(@FloatRange(from = 0) float proportion) {
        super(proportion);
        mProportion = proportion;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setTextScaleX(ds.getTextScaleX() * mProportion);
    }

    @Override
    public void updateMeasureState(TextPaint ds) {
        ds.setTextScaleX(ds.getTextScaleX() * mProportion);
    }


    public static final Creator<CustomScaleXSpan> CREATOR = new Creator<CustomScaleXSpan>() {
        @Override
        public CustomScaleXSpan createFromParcel(Parcel in) {
            final float proportion = in.readFloat();
            return new CustomScaleXSpan(proportion);
        }

        @Override
        public CustomScaleXSpan[] newArray(int size) {
            return new CustomScaleXSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mProportion);
    }

}
