package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;

import com.google.gson.annotations.Expose;

public class CustomRelativeSizeSpan extends RelativeSizeSpan {
    ///[Gson#Exclude父类成员变量的序列化和反序列化]
    ///Exclude后父类成员变量不被序列化，因此需要重新声明并设置@Expose
    @Expose
    private final float mProportion;

    public CustomRelativeSizeSpan(@FloatRange(from = 0) float proportion) {
        super(proportion);
        mProportion = proportion;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        ds.setTextSize(ds.getTextSize() * mProportion);
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint ds) {
        ds.setTextSize(ds.getTextSize() * mProportion);
    }


    public static final Creator<CustomRelativeSizeSpan> CREATOR = new Creator<CustomRelativeSizeSpan>() {
        @Override
        public CustomRelativeSizeSpan createFromParcel(Parcel in) {
            final float proportion = in.readFloat();
            return new CustomRelativeSizeSpan(proportion);
        }

        @Override
        public CustomRelativeSizeSpan[] newArray(int size) {
            return new CustomRelativeSizeSpan[size];
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
