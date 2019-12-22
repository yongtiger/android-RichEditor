package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.support.annotation.FloatRange;
import android.text.TextPaint;
import android.text.style.ScaleXSpan;

import com.google.gson.annotations.Expose;

public class CustomScaleXSpan extends ScaleXSpan {
    ///[Gson#Exclude父类成员变量的序列化和反序列化]
    ///Exclude后父类成员变量不被序列化，因此需要重新声明并设置@Expose
    @Expose
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
