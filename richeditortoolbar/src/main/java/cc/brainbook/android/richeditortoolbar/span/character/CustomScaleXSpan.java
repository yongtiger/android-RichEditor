package cc.brainbook.android.richeditortoolbar.span.character;

import android.os.Parcel;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import android.text.TextPaint;
import android.text.style.ScaleXSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IReadableStyle;

public class CustomScaleXSpan extends ScaleXSpan implements ICharacterStyle, IReadableStyle {
    ///[Gson#Exclude父类成员变量的序列化和反序列化]
    ///Exclude后父类成员变量不被序列化，因此需要重新声明并设置@Expose
    @Expose
    private final float mProportion;


    public CustomScaleXSpan(@FloatRange(from = 0) float proportion) {
        super(proportion);
        mProportion = proportion;
    }


    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        ds.setTextScaleX(ds.getTextScaleX() * mProportion);
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint ds) {
        ds.setTextScaleX(ds.getTextScaleX() * mProportion);
    }


    public static final Creator<CustomScaleXSpan> CREATOR = new Creator<CustomScaleXSpan>() {
        @Override
        @NonNull
        public CustomScaleXSpan createFromParcel(@NonNull Parcel in) {
            final float proportion = in.readFloat();

            return new CustomScaleXSpan(proportion);
        }

        @Override
        @NonNull
        public CustomScaleXSpan[] newArray(int size) {
            return new CustomScaleXSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeFloat(mProportion);
    }

}
