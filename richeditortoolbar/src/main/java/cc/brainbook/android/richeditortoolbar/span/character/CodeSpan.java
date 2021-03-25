package cc.brainbook.android.richeditortoolbar.span.character;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;

public class CodeSpan extends MetricAffectingSpan implements Parcelable, ICharacterStyle {
    @Override
    public void updateDrawState(@NonNull TextPaint tp) {
        tp.setTypeface(Typeface.MONOSPACE);
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint textPaint) {

    }


    public static final Creator<CodeSpan> CREATOR = new Creator<CodeSpan>() {
        @Override
        @NonNull
        public CodeSpan createFromParcel(Parcel in) {
            return new CodeSpan();
        }

        @Override
        @NonNull
        public CodeSpan[] newArray(int size) {
            return new CodeSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

}
